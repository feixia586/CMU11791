package edu.cmu.lti.fei.annotator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.fei.type.Entry;

/**
 * An annotator that discovers Entry in the document text. An Entry is 
 * defined as an identifier followed by a sentence. Specifically, an entry
 * is one line in the document.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class EntryAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Annotates a document. The annotator simply split the document by lines
   * and annotate.
   * @param aJCas the JCas object.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Get document text and split
    String docText = aJCas.getDocumentText();
    String[] lines = docText.split("\n");

    // Iterate and annotate
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
