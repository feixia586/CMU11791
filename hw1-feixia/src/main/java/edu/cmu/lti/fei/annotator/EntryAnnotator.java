package edu.cmu.lti.fei.annotator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.fei.type.Entry;

public class EntryAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    String docText = aJCas.getDocumentText();
    String[] lines = docText.split("\n");

    int offset = 0;
    for (int i = 0; i < lines.length; i++) {
      Entry annot = new Entry(aJCas);
      int begin = offset;
      int end = offset + lines[i].length();
      annot.setBegin(begin);
      annot.setEnd(end);
      annot.addToIndexes();
      
      offset += (lines[i].length() + 1);
    }
  }

}
