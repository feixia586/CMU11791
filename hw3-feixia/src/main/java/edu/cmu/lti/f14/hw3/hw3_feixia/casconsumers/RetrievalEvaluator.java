package edu.cmu.lti.f14.hw3.hw3_feixia.casconsumers;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.print.Doc;
import javax.wsdl.Output;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_feixia.docrept.DocVec;
import edu.cmu.lti.f14.hw3.hw3_feixia.docrept.DocVecOps;
import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_feixia.utils.FileOp;

public class RetrievalEvaluator extends CasConsumer_ImplBase {

  /**
   * Output file path
   */
  String oPath;

  /**
   * All unique qid
   */
  private Set<Integer> allqid;

  /**
   * The map from qid to query DocVec
   */
  private Map<Integer, DocVec> qid2Query;

  /**
   * The map from qid to a list of DocVec
   */
  private Map<Integer, List<DocVec>> qid2DocVecs;

  /**
   * Initializes this CAS Consumer with the parameters specified in the descriptor.
   * 
   * @throws ResourceInitializationException
   *           if there is error in initializing the resources
   */
  public void initialize() throws ResourceInitializationException {
    oPath = (String) getUimaContext().getConfigParameterValue("outputFile");

    allqid = new HashSet<Integer>();
    qid2Query = new HashMap<Integer, DocVec>();
    qid2DocVecs = new HashMap<Integer, List<DocVec>>();
  }

  /**
   * For each Document object, construct the corresponding DocVec object. Memorize relative
   * information to construct allqid, qid2Query, qid2DocVecs, which will be used later
   * 
   * @param aCAS
   *          CasContainer which has been populated by the TAEs
   * 
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * 
   * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(CAS)
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {

    JCas jcas;
    try {
      jcas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator<?> it = jcas.getAnnotationIndex(Document.type).iterator();

    if (it.hasNext()) {
      Document doc = (Document) it.next();
      int qid = doc.getQueryID();

      // construct the allqid
      allqid.add(qid);

      // construct the qid2Query and qid2DocVecs
      DocVec docVec = new DocVec(doc);
      if (docVec.isQuery()) {
        qid2Query.put(qid, docVec);
      } else {
        if (qid2DocVecs.containsKey(qid)) {
          qid2DocVecs.get(qid).add(docVec);
        } else {
          List<DocVec> newList = new ArrayList<DocVec>();
          newList.add(docVec);
          qid2DocVecs.put(qid, newList);
        }
      }
    }

  }

  /**
   * Compute Cosine Similarity and rank the retrieved sentences. Compute the MRR metric.
   * 
   * @param arg0
   *          ProcessTrace object that will log events in this method.
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * @throws IOException
   *           if there is an IO Error
   * @see org.apache.uima.collection.CasConsumer#collectionProcessComplete(ProcessTrace)
   */
  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    super.collectionProcessComplete(arg0);

    // compute the cosine similarity measure
    for (int qid : allqid) {
      DocVec query = qid2Query.get(qid);
      List<DocVec> docVecList = qid2DocVecs.get(qid);
      for (DocVec docVec : docVecList) {
        double cos_sim = DocVecOps.computeCosineSim(query, docVec);
        docVec.setCosSim(cos_sim);
      }
    }

    // compute the rank of retrieved sentences
    for (int qid : allqid) {
      Collections.sort(qid2DocVecs.get(qid), new CosSimComparator());
    }
    List<DocVec> relDocVecs = DocVecOps.getRelDocVecs(qid2DocVecs);
    Collections.sort(relDocVecs, new QidComparator());
    outputAll(qid2DocVecs, qid2Query);

    // compute the metric:: mean reciprocal rank
    double mrr = DocVecOps.compute_mrr(relDocVecs);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + mrr);

    // output to file
    String outString = DocVecOps.consOutStr(relDocVecs, mrr);
    FileOp.writeToFile(oPath, outString);
  }

  public void outputAll(Map<Integer, List<DocVec>> qid2DocVecs, Map<Integer, DocVec> qid2Query) {
    StringBuilder sb = new StringBuilder();
    for (int qid : allqid) {
      sb.append(qid2Query.get(qid).getDocText() + "\n");
      List<DocVec> docVecList = qid2DocVecs.get(qid);
      for (DocVec docVec : docVecList) {
        sb.append("qid=" + docVec.getqid() + "\t");
        sb.append("rel=" + docVec.getRel() + "\t");
        sb.append("cos=" + docVec.getCosSim() + "\t");
        sb.append("rank=" + docVec.getRank() + "\t");
        sb.append(docVec.getDocText() + "\n");
      }
      sb.append("\n");
    }
    FileOp.writeToFile("analysis.txt", sb.toString());
  }

}
