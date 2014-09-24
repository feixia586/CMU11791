package edu.cmu.lti.fei.annotator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.fei.type.NameEntity;
import edu.cmu.lti.fei.util.FileOp;

/**
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class EvaluationAnnotator extends JCasAnnotator_ImplBase {

  private String mGoldFilePath;

  private String mGoldContent;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    mGoldFilePath = (String) getContext().getConfigParameterValue("GoldFilePath");
    mGoldContent = FileOp.readFromFile(mGoldFilePath);
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    Set<String> goldSet = getGoldSet();
    Set<String> mySet = getMySet(aJCas);
    
    int P = goldSet.size();
    int TP = 0, FP = 0;
    for (String line : mySet) {
      if (goldSet.contains(line)) {
        TP += 1;
      } else {
        FP += 1;
      }
    }
    
    double precision = TP / (double)(TP + FP);
    double recall = TP / (double) P;
    double fone = 2 * precision * recall / (precision + recall);

    System.out.println("-------------------------------------------");
    System.out.println("Precision = " + precision);
    System.out.println("Recall = " + recall);
    System.out.println("F-1 Score = " + fone);
    System.out.println("-------------------------------------------");
  }

  private Set<String> getGoldSet() {
    String[] lines = mGoldContent.split("\n");
    Set<String> goldSet = new HashSet<String>();
    for (int i = 0; i < lines.length; i++) {
      goldSet.add(lines[i]);
    }

    return goldSet;
  }

  private Set<String> getMySet(JCas aJCas) {
    Set<String> mySet = new HashSet<String>();
    Iterator<?> NameEntityIter = aJCas.getAnnotationIndex(NameEntity.type).iterator();
    while (NameEntityIter.hasNext()) {
      NameEntity nameEntity = (NameEntity) NameEntityIter.next();

      String identifier = nameEntity.getIdentifier();
      int boffset = nameEntity.getBoffset();
      int eoffset = nameEntity.getEoffset();

      // get the text that is enclosed within the annotation in the CAS
      String text = nameEntity.getCoveredText();
      mySet.add(identifier + "|" + boffset + " " + eoffset + "|" + text);
    }
    
    return mySet;
  }

}
