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
 * The evaluator. It is used to evaluate the gene name entity recognition.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class EvaluationAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Groud truth data path
   */
  private String mGoldFilePath;

  /**
   * Ground truth data content
   */
  private String mGoldContent;

  /**
   * Perform initialization logic. Load the ground truth data.
   * 
   * @param aContext the UimaContext object
   * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    mGoldFilePath = (String) getContext().getConfigParameterValue("GoldFilePath");
    mGoldContent = FileOp.readFromFile(mGoldFilePath);
  }

  /**
   * Calculate the precision, recall and F-1 score.
   * 
   * @param aJCas the JCas object
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
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
    
    // calculate
    double precision = TP / (double)(TP + FP);
    double recall = TP / (double) P;
    double fone = 2 * precision * recall / (precision + recall);

    // print to the console
    System.out.println("-------------------------------------------");
    System.out.println("Precision = " + precision);
    System.out.println("Recall = " + recall);
    System.out.println("F-1 Score = " + fone);
    System.out.println("-------------------------------------------");
  }

  /**
   * Get the ground truth set.
   * 
   * @return the ground truth set
   */
  private Set<String> getGoldSet() {
    String[] lines = mGoldContent.split("\n");
    Set<String> goldSet = new HashSet<String>();
    for (int i = 0; i < lines.length; i++) {
      goldSet.add(lines[i]);
    }

    return goldSet;
  }

  /**
   * Get the system's result set.
   * 
   * @param aJCas the JCas object
   * @return the system's result set
   */
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
