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
  
  String oPath;

  private Set<Integer> allqid; 

  private Map<Integer, DocVec> qid2Query;

  private Map<Integer, List<DocVec>> qid2DocVecs;

  public void initialize() throws ResourceInitializationException {
    oPath = (String) getUimaContext().getConfigParameterValue("outputFile");

    allqid = new HashSet<Integer>();
    qid2Query = new HashMap<Integer, DocVec>();
    qid2DocVecs = new HashMap<Integer, List<DocVec>>();
  }

  /**
   * 1. construct the global word dictionary 2. keep the word frequency for each sentence
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

      // Do something useful here
      allqid.add(qid);
      
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
   * 1. Compute Cosine Similarity and rank the retrieved sentences 2. Compute the MRR metric
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
    

    // compute the metric:: mean reciprocal rank
    double mrr = DocVecOps.compute_mrr(relDocVecs);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + mrr);
    
    String outString = DocVecOps.consOutStr(relDocVecs, mrr);
    FileOp.writeToFile(oPath, outString);
  }

  

  

}
