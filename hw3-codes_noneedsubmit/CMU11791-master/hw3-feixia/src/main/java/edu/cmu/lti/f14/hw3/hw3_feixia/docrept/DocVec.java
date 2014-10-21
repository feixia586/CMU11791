package edu.cmu.lti.f14.hw3.hw3_feixia.docrept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.cas.FSList;

import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_feixia.utils.Utils;

/**
 * The DocVec class, used to represent a document.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class DocVec {
  /**
   * The qid
   */
  private int qid;

  /**
   * The relevance value
   */
  private int rel;

  /**
   * The actual doc text
   */
  private String docText;

  /**
   * The map from token to frequency
   */
  private Map<String, Integer> token2freq;

  /**
   * The len normalizer
   */
  private double lenNorm;

  /**
   * The cosine similarity
   */
  private double cosineSim;

  /**
   * The rank
   */
  private int rank;

  /**
   * Constructor. Construct the DocVec object.
   * 
   * @param doc
   *          the doc in Document type
   */
  public DocVec(Document doc) {
    token2freq = new HashMap<String, Integer>();

    qid = doc.getQueryID();
    rel = doc.getRelevanceValue();
    docText = doc.getText();

    // calculate the lenNorm
    int sqSum = 0;
    FSList fsTokenList = doc.getTokenList();
    ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);
    for (Token token : tokenList) {
      String text = token.getText();
      int freq = token.getFrequency();
      token2freq.put(text, freq);
      sqSum += (freq * freq);
    }
    lenNorm = Math.sqrt((double) sqSum);

    cosineSim = 1.0;
    rank = 1;
  }

  /**
   * Check if this DocVec is a query
   * 
   * @return true if it is a query; false if it isn't
   */
  public boolean isQuery() {
    return (rel == 99);
  }

  /**
   * Get the qid.
   * 
   * @return the qid
   */
  public int getqid() {
    return qid;
  }

  /**
   * Get the relevance value
   * 
   * @return the relevance value
   */
  public int getRel() {
    return rel;
  }

  /**
   * Get the doc text.
   * 
   * @return the doc text
   */
  public String getDocText() {
    return docText;
  }

  /**
   * Get the token to frequency map
   * 
   * @return the token to frequency map
   */
  public Map<String, Integer> getToken2Freq() {
    return token2freq;
  }

  /**
   * Get the lenNorm
   * 
   * @return the lenNorm
   */
  public double getLenNorm() {
    return lenNorm;
  }

  /**
   * Set the cosine similarity
   * 
   * @param cosineSim
   *          the cosine similarity to be set
   */
  public void setCosSim(double cosineSim) {
    this.cosineSim = cosineSim;
  }

  /**
   * Get the cosine similarity
   * 
   * @return the cosine similarity
   */
  public double getCosSim() {
    return cosineSim;
  }

  /**
   * Set the rank
   * 
   * @param rank
   *          the rank
   */
  public void setRank(int rank) {
    this.rank = rank;
  }

  /**
   * Get the rank
   * 
   * @return the rank
   */
  public int getRank() {
    return rank;
  }

}
