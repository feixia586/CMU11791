package edu.cmu.lti.fei.util;

import java.util.HashSet;
import java.util.Set;

public class Evaluation {
  String mGoldContent;
  String mMyContent;
  
  public Evaluation(String myContent, String goldContent ) {
    mMyContent = myContent;
    mGoldContent = goldContent;
  }

  public void evaluate() {
    System.out.println("");
    System.out.println("## Starting to do Evaluation...");
    Set<String> goldSet = getGoldSet();
    Set<String> mySet = getMySet();
    
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
    System.out.println("## Evaluation Finished!");
    System.out.println("");
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
   * @return the system's result set
   */
  private Set<String> getMySet() {
    String[] lines = mMyContent.split("\n");
    Set<String> mySet = new HashSet<String>();

    for (int i = 0; i < lines.length; ++i) {
      mySet.add(lines[i]);
    }
    
    return mySet;
  }
}
