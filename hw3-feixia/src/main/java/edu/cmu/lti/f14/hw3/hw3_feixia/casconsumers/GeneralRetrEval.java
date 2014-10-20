package edu.cmu.lti.f14.hw3.hw3_feixia.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_feixia.docrept.GeneralDocReptOps;
import edu.cmu.lti.f14.hw3.hw3_feixia.docrept.GeneralDocRept;
import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Document;

public class GeneralRetrEval extends CasConsumer_ImplBase {
  String oPath;

  private Set<Integer> allqid;

  private Map<Integer, GeneralDocRept> qid2Query;

  private Map<Integer, List<GeneralDocRept>> qid2DocRepts;

  public void initialize() throws ResourceInitializationException {
    oPath = (String) getUimaContext().getConfigParameterValue("outputFile");

    allqid = new HashSet<Integer>();
    qid2Query = new HashMap<Integer, GeneralDocRept>();
    qid2DocRepts = new HashMap<Integer, List<GeneralDocRept>>();
  }

  @Override
  public void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator<?> it = jcas.getAnnotationIndex(Document.type).iterator();
    if (it.hasNext()) {
      Document doc = (Document) it.next();
      int qid = doc.getQueryID();

      // Do something useful here
      allqid.add(qid);

      GeneralDocRept docRept = new GeneralDocRept(doc);
      if (docRept.isQuery()) {
        qid2Query.put(qid, docRept);
      } else {
        if (qid2DocRepts.containsKey(qid)) {
          qid2DocRepts.get(qid).add(docRept);
        } else {
          List<GeneralDocRept> newList = new ArrayList<GeneralDocRept>();
          newList.add(docRept);
          qid2DocRepts.put(qid, newList);
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

    // compute the similarity measure
    for (int qid : allqid) {
      GeneralDocRept query = qid2Query.get(qid);
      List<GeneralDocRept> docReptList = qid2DocRepts.get(qid);
      //GeneralDocReptOps.calcAndSetTFIDF(query, docReptList);
      //GeneralDocReptOps.calcAndSetEditDis(query, docReptList);
      GeneralDocReptOps.calcAndSetJaccard(query, docReptList);
    }

    // compute the rank of retrieved sentences
    for (int qid : allqid) {
      Collections.sort(qid2DocRepts.get(qid), new GeneralSimComparator());
    }
    List<GeneralDocRept> relDocRepts = GeneralDocReptOps.getRelDocVecs(qid2DocRepts);
    Collections.sort(relDocRepts, new GeneralQidComparator());

    // compute the metric:: mean reciprocal rank
    double mrr = GeneralDocReptOps.compute_mrr(relDocRepts);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + mrr);

    String outString = GeneralDocReptOps.consOutStr(relDocRepts, mrr);
    System.out.println(outString);
  }

}
