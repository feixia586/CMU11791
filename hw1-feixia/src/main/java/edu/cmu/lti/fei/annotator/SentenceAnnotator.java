package edu.cmu.lti.fei.annotator;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.fei.type.Entry;
import edu.cmu.lti.fei.type.Sentence;

public class SentenceAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    
    FSIndex<?> EntryIndex = aJCas.getAnnotationIndex(Entry.type);
    Iterator<?> EntryIter = EntryIndex.iterator();
    
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
      
      //System.out.println(annot.getIdentifier() + ", " + annot.getCoveredText());
    }
  }


}
