package edu.cmu.lti.fei.annotator;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.fei.type.Entry;
import edu.cmu.lti.fei.type.Sentence;

/**
 * An annotator that discovers Sentence in an Entry. An Entry contains an identifier
 * and a Sentence. This annotator will annotate those two fields.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class SentenceAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Annotate a document. This find the first space in an Entry and split 
   * it into two parts, then annotate.
   * 
   * @param aJCas the JCas object.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Get the Entry Iterator
    FSIndex<?> EntryIndex = aJCas.getAnnotationIndex(Entry.type);
    Iterator<?> EntryIter = EntryIndex.iterator();
    
    // Iterate and annotate
    while(EntryIter.hasNext()) {
      Sentence annot = new Sentence(aJCas);

      Entry entry = (Entry) EntryIter.next();
      String text = entry.getCoveredText();
      int idIdx = text.indexOf(' ');
      
      String identifier = text.substring(0, idIdx);
      annot.setIdentifier(identifier);
      annot.setBegin(entry.getBegin() + idIdx + 1);
      annot.setEnd(entry.getEnd());
      annot.addToIndexes();
    }
  }


}
