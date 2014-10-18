package edu.cmu.lti.f14.hw3.hw3_feixia.docrept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.cas.FSList;

import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_feixia.utils.Utils;

public class DocVec {
  private int qid;

  private int rel;

  private String docText;

  private Map<String, Integer> token2freq;

  private double lenNorm;
  
  private double cosineSim;
  
  private int rank;
  
  public DocVec(Document doc) {
    token2freq = new HashMap<String, Integer>();

    qid = doc.getQueryID();
    rel = doc.getRelevanceValue();
    docText = doc.getText();

    int sqSum = 0;
    FSList fsTokenList = doc.getTokenList();
    ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);
    for (Token token : tokenList) {
      String text = token.getText();
      int freq = token.getFrequency();
      token2freq.put(text, freq);
      sqSum += (freq * freq);
    }
    lenNorm = Math.sqrt((double)sqSum);

    cosineSim = 1.0;
    rank = 1;
  }

  public boolean isQuery() {
    return (rel == 99);
  }

  public int getqid() {
    return qid;
  }
  
  public int getRel() {
    return rel;
  }
  
  public String getDocText() {
    return docText;
  }
  
  public Map<String, Integer> getToken2Freq() {
    return token2freq;
  }
  
  public double getLenNorm() {
    return lenNorm;
  }
  
  public void setCosSim(double cosineSim) {
    this.cosineSim = cosineSim;
  }
  
  public double getCosSim() {
    return cosineSim;
  }
  
  public void setRank(int rank) {
    this.rank = rank;
  }
  
  public int getRank() {
    return rank;
  }
  
  
  
}
