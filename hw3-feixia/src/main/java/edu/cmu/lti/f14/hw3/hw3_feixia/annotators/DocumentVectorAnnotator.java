package edu.cmu.lti.f14.hw3.hw3_feixia.annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_feixia.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Document doc = (Document) iter.get();
      createTermFreqVector(jcas, doc);
    }

  }

  /**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   *
   * @param doc
   *          input text
   * @return a list of tokens.
   */
  private List<String> tokenize0(String doc) {
    List<String> res = new ArrayList<String>();

    for (String s : doc.split("\\s+")) {
      res.add(s);
    }
    return res;
  }

  /**
   * 
   * @param jcas
   * @param doc
   */
  private void createTermFreqVector(JCas jcas, Document doc) {

    String docText = doc.getText();

    // TO DO: construct a vector of tokens and update the tokenList in CAS
    List<String> tokens = tokenize0(docText);

    HashMap<String, Integer> token2Freq = new HashMap<String, Integer>();
    for (String token : tokens) {
      if (token2Freq.containsKey(token)) {
        int number = token2Freq.get(token);
        token2Freq.put(token, number + 1);
      } else {
        token2Freq.put(token, 1);
      }
    }

    List<Token> tokenList = new ArrayList<Token>();
    for (Map.Entry<String, Integer> entry : token2Freq.entrySet()) {
      Token token = new Token(jcas);
      token.setText(entry.getKey());
      token.setFrequency(entry.getValue());
      token.addToIndexes();
      tokenList.add(token);
    }

    FSList tokenFSList = Utils.fromCollectionToFSList(jcas, tokenList);
    doc.setTokenList(tokenFSList);
    doc.addToIndexes();
  }

  private void printDocInfo(Document doc) {
    System.out.println(doc.getText());
    FSList tokenFSList = doc.getTokenList();
    List<Token> tokenList = Utils.fromFSListToCollection(tokenFSList, Token.class);
    for (Token token : tokenList) {
      System.out.print(token.getText() + "(" + token.getFrequency() + ")");
    }
    System.out.println("\n");
  }

}
