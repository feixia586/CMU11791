package edu.cmu.lti.f14.hw3.hw3_feixia.docrept;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocVecOps {
  public static double computeCosineSim(DocVec query, DocVec doc) {
    Map<String, Integer> qryStrs = query.getToken2Freq();
    Map<String, Integer> docStrs = doc.getToken2Freq();
    double cosSim = 0.0;
    for (Map.Entry<String, Integer> qryEntry : qryStrs.entrySet()) {
      String qryStr = qryEntry.getKey();
      int qryFreq = qryEntry.getValue();
      if (docStrs.containsKey(qryStr)) {
        int docFreq = docStrs.get(qryStr);
        cosSim += qryFreq * docFreq;
      }
    }
    cosSim /= (query.getLenNorm() * doc.getLenNorm());

    return cosSim;
  }

  public static List<DocVec> getRelDocVecs(Map<Integer, List<DocVec>> qid2DocVecs) {
    List<DocVec> relDocVecs = new ArrayList<DocVec>();

    for (Map.Entry<Integer, List<DocVec>> entry : qid2DocVecs.entrySet()) {
      List<DocVec> docVecs = entry.getValue();
      for (int i = 0; i < docVecs.size(); i++) {
        docVecs.get(i).setRank(i + 1);
        if (docVecs.get(i).getRel() == 1) {
          relDocVecs.add(docVecs.get(i));
        }
      }
    }

    return relDocVecs;
  }

  /**
   * 
   * @return mrr
   */
  public static double compute_mrr(List<DocVec> relDocVecs) {
    double mrr = 0.0;

    // compute Mean Reciprocal Rank (MRR) of the text collection
    int size = relDocVecs.size();
    for (DocVec docVec : relDocVecs) {
      mrr += (1.0 / docVec.getRank());
    }

    mrr /= size;

    return mrr;
  }

  public static String consOutStr(List<DocVec> relDocVecs, double mrr) {
    StringBuilder sb = new StringBuilder();
    DecimalFormat df = new DecimalFormat("#.####");
    for (DocVec docVec : relDocVecs) {
      sb.append("cosine=" + df.format(docVec.getCosSim()) + "\t");
      sb.append("rank=" + docVec.getRank() + "\t");
      sb.append("qid=" + docVec.getqid() + "\t");
      sb.append("rel=" + docVec.getRel() + "\t");
      sb.append(docVec.getDocText() + "\n");
    }
    sb.append("MRR=" + df.format(mrr) + "\n");

    return sb.toString();
  }
}
