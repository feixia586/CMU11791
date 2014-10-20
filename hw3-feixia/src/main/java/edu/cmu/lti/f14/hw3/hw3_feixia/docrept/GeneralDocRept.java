package edu.cmu.lti.f14.hw3.hw3_feixia.docrept;

import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Document;

public class GeneralDocRept {
  private int qid;

  private int rel;

  private String docText;

  private double sim;

  private int rank;

  public GeneralDocRept(Document doc) {
    qid = doc.getQueryID();
    rel = doc.getRelevanceValue();
    docText = doc.getText();

    sim = 1.0;
    rank = 1;
  }

  public int getqid() {
    return qid;
  }
  public boolean isQuery() {
    return (rel == 99);
  }

  public int getRel() {
    return rel;
  }

  public String getDocText() {
    return docText;
  }

  public void setSim(double sim) {
    this.sim = sim;
  }

  public double getSim() {
    return sim;
  }
  
  public void setRank(int rank) {
    this.rank = rank;
  }
  
  public int getRank() {
    return rank;
  }
}
