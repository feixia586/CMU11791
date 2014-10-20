package edu.cmu.lti.f14.hw3.hw3_feixia.annotators;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_feixia.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_feixia.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  private TokenizerFactory TOKENIZER_FACTORY;

  private Set<String> stopSet;

  private String stopFilePath;

  /**
   * Perform initialization logic.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    stopFilePath = (String) getContext().getConfigParameterValue("stopFilePath");
    stopSet = new HashSet<String>();

    String content = getFileAsStream(stopFilePath);
    String[] lines = content.split("\n");
    for (String line : lines) {
      stopSet.add(line);
    }

    // TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    // TOKENIZER_FACTORY = new PorterStemmerTokenizerFactory(IndoEuropeanTokenizerFactory.INSTANCE);

    // TOKENIZER_FACTORY = new LowerCaseTokenizerFactory(IndoEuropeanTokenizerFactory.INSTANCE);
     TOKENIZER_FACTORY = new StopTokenizerFactory(new
     LowerCaseTokenizerFactory(IndoEuropeanTokenizerFactory.INSTANCE), stopSet);
    // TOKENIZER_FACTORY = new PorterStemmerTokenizerFactory(new
    // LowerCaseTokenizerFactory(IndoEuropeanTokenizerFactory.INSTANCE));

  }

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

  private List<String> tokenize1(String doc) {
    List<String> res = new ArrayList<String>();
    List<String> whiteList = new ArrayList<String>();

    Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(doc.toCharArray(), 0, doc.length());

    tokenizer.tokenize(res, whiteList);

    /*
    for (int i = 0; i < res.size(); i++) {
      System.out.print(res.get(i) + "|");
    }
    System.out.println("");
    for (int i = 0; i < whiteList.size(); i++) {
      System.out.print(whiteList.get(i) + "|");
    }
    System.out.println("\n");*/

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
    List<String> tokens = tokenize1(docText);

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

  /**
   * Read file through stream LPDictExactNERAnnotator
   * 
   * @param filePath
   *          the file path
   * @return the string of the file
   * @throws ResourceInitializationException
   */
  private String getFileAsStream(String filePath) throws ResourceInitializationException {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = DocumentVectorAnnotator.class.getClassLoader().getResourceAsStream(filePath);

      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      br.close();
    } catch (Exception ex) {
      System.out.println("[Error]: Look Below.");
      ex.printStackTrace();
      System.out.println("[Error]: Look Above.");
      throw new ResourceInitializationException();
    }

    String content = sb.toString();
    return content;
  }
}
