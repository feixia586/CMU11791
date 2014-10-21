package edu.cmu.lti.f14.hw3.hw3_feixia.docrept;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliasi.spell.EditDistance;
import com.aliasi.spell.JaccardDistance;
import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Proximity;

public class GeneralDocReptOps {
  private static String stopFilePath = "src/main/resources/stopwords.txt";

  private static TokenizerFactory tkf;

  private static final Set<String> stopSet;
  static {
    String content = readFromFile(stopFilePath);
    String[] lines = content.split("\n");
    Set<String> tmpSet = new HashSet<String>();
    for (String line : lines) {
      tmpSet.add(line);
    }
    stopSet = Collections.unmodifiableSet(tmpSet);

    TokenizerFactory baseTKF = IndoEuropeanTokenizerFactory.INSTANCE;
    baseTKF = new LowerCaseTokenizerFactory(baseTKF);
    baseTKF = new StopTokenizerFactory(baseTKF, stopSet);
    baseTKF = new PorterStemmerTokenizerFactory(baseTKF);
    tkf = baseTKF;
  }

  public static void calcAndSetTFIDF(GeneralDocRept query, List<GeneralDocRept> docReptList) {
    TfIdfDistance tfidf = new TfIdfDistance(tkf);

    String queryText = query.getDocText();
    tfidf.handle(queryText);
    for (GeneralDocRept docRept : docReptList) {
      tfidf.handle(docRept.getDocText());
    }

    for (GeneralDocRept docRept : docReptList) {
      double sim = tfidf.proximity(queryText, docRept.getDocText());
      docRept.setSim(sim);
    }
  }
  
  public static void calcAndSetEditDis(GeneralDocRept query, List<GeneralDocRept> docReptList) {
    Proximity<CharSequence> Prox = new EditDistance(true);
    
    String queryText = query.getDocText();
    for (GeneralDocRept docRept : docReptList) {
      double sim = Prox.proximity(queryText, docRept.getDocText());
      docRept.setSim(sim);
    }
  }
  
  public static void calcAndSetJaccard(GeneralDocRept query, List<GeneralDocRept> docReptList) {
    JaccardDistance jaccard = new JaccardDistance(tkf);

    String queryText = query.getDocText();
    for (GeneralDocRept docRept : docReptList) {
      double sim = jaccard.proximity(queryText, docRept.getDocText());
      docRept.setSim(sim);
    }
  }

  public static List<GeneralDocRept> getRelDocVecs(Map<Integer, List<GeneralDocRept>> qid2DocRepts) {
    List<GeneralDocRept> relDocRepts = new ArrayList<GeneralDocRept>();

    for (Map.Entry<Integer, List<GeneralDocRept>> entry : qid2DocRepts.entrySet()) {
      List<GeneralDocRept> docRepts = entry.getValue();
      for (int i = 0; i < docRepts.size(); i++) {
        docRepts.get(i).setRank(i + 1);
        if (docRepts.get(i).getRel() == 1) {
          relDocRepts.add(docRepts.get(i));
        }
      }
    }

    return relDocRepts;
  }

  public static double compute_mrr(List<GeneralDocRept> relDocRepts) {
    double mrr = 0.0;

    // compute Mean Reciprocal Rank (MRR) of the text collection
    int size = relDocRepts.size();
    for (GeneralDocRept docRept : relDocRepts) {
      mrr += (1.0 / docRept.getRank());
    }

    mrr /= size;

    return mrr;
  }

  public static String consOutStr(List<GeneralDocRept> relDocRepts, double mrr) {
    StringBuilder sb = new StringBuilder();
    DecimalFormat df = new DecimalFormat("0.0000");
    for (GeneralDocRept docRept : relDocRepts) {
      sb.append("sim=" + df.format(docRept.getSim()) + "\t");
      sb.append("rank=" + docRept.getRank() + "\t");
      sb.append("qid=" + docRept.getqid() + "\t");
      sb.append("rel=" + docRept.getRel() + "\t");
      sb.append(docRept.getDocText() + "\n");
    }
    sb.append("MRR=" + df.format(mrr) + "\n");

    return sb.toString();
  }

  /**
   * Read content from a file
   * 
   * @param filename
   *          the file path
   * @return the content of the file
   */
  public static String readFromFile(String filename) {
    String content = null;
    StringBuilder sb = new StringBuilder();
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        // sb.append(System.lineSeparator());
        sb.append("\n");
        line = br.readLine();
      }
      br.close();

    } catch (IOException fexp) {
      System.out.println("Error: error in open file: " + filename);
      System.exit(1);
    }

    content = sb.toString();
    return content;
  }
}
