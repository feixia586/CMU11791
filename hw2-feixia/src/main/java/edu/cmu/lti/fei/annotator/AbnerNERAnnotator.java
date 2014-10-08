package edu.cmu.lti.fei.annotator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.fei.util.*;
import edu.cmu.deiis.types.Annotation;
import edu.cmu.deiis.types.Sentence;
import abner.Tagger;

public class AbnerNERAnnotator extends JCasAnnotator_ImplBase {

  Tagger mTagger;

  /**
   * Perform initialization logic. Read the model and initialize the mChunker.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    mTagger = new Tagger(Tagger.BIOCREATIVE);
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    // iterate over all sentences
    while (SentenceIter.hasNext()) {
      try {
        Sentence sentence = (Sentence) SentenceIter.next();
        String text = sentence.getCoveredText();
        String tagRes = mTagger.tagABNER(text);

        String[] items = tagRes.split("  ");
        items[items.length - 1] = "XX|O";
        int count = 0;
        int k = 0;
        while (k < items.length) {
          String[] parts = items[k].split("\\|");
          if (parts[1].charAt(0) == 'O') {
            ++k;
            count += parts[0].length();
            continue;
          }

          List<String> list = new ArrayList<String>();
          int subCount = 0;
          while ((parts[1].charAt(0) == 'B' || parts[1].charAt(0) == 'I') && k < items.length) {
            list.add(parts[0]);
            ++k;
            subCount += parts[0].length();
            parts = items[k].split("\\|");
          }

          int begin = text.indexOf(list.get(0), count);
          int end = begin + list.get(0).length();
          for (int i = 1; i < list.size(); i++) {
            end = text.indexOf(list.get(i), end) + list.get(i).length();
          }

          Annotation annot = new Annotation(aJCas);
          annot.setBegin(sentence.getBegin() + begin);
          annot.setEnd(sentence.getBegin() + end);
          annot.setIdentifier(sentence.getIdentifier());
          annot.setCasProcessorId(CasProcessID.ABNER);
          annot.setConfidence((float) 1.0);
          annot.addToIndexes();
          count += subCount;

        }
      } catch (Exception ex) {
      }

    }
  }
}
