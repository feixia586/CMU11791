package edu.cmu.lti.fei.annotator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.fei.type.NameEntity;
import edu.cmu.lti.fei.type.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * An annotator that discovers Gene Name Entity in the document text. This use
 * Stanford NLP and annotate the consecutive nouns.
 * 
 * @author Fei Xia
 *
 */
public class BSNameEntityAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Annotate to find out the Gene Name Entity. This use Stanford NLP and detect nouns. All 
   * nouns that are consecutive will be viewed as gene name entity.
   * 
   * @param aJCas the JCas object. 
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    // init Stanford NLP
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    int num = 0;
    // iterate over all sentences
    while (SentenceIter.hasNext()) {
      num++;
      if (num % 500 == 0) {
        System.out.println("Working: " + num);
      }

      Sentence sentence = (Sentence) SentenceIter.next();
      String text = sentence.getCoveredText();

      // use stanford nlp to do annotation
      Annotation document = new Annotation(text);
      pipeline.annotate(document);
      List<CoreMap> sens = document.get(SentencesAnnotation.class);

      for (CoreMap sen : sens) {
        List<CoreLabel> candidate = new ArrayList<CoreLabel>();
        for (CoreLabel token : sen.get(TokensAnnotation.class)) {
          String pos = token.get(PartOfSpeechAnnotation.class);
          if (pos.startsWith("NN")) {
            candidate.add(token);
          } else if (candidate.size() > 0) {
            addField(sentence, candidate, aJCas);
          }
        }

        if (candidate.size() > 0) {
          addField(sentence, candidate, aJCas);
        }
      }
    }
  }

  /**
   * Calculate and add name entity annotation to JCas object.
   * 
   * @param sentence the sentence that are currently annotated.
   * @param candidate a list of objects that can be recognized as name entity when combined
   * @param aJCas the JCas object.
   */
  private void addField(Sentence sentence, List<CoreLabel> candidate, JCas aJCas) {
    NameEntity annot = new NameEntity(aJCas);

    String text = sentence.getCoveredText();
    int begin = candidate.get(0).beginPosition();
    int end = candidate.get(candidate.size() - 1).endPosition();
    
    // calculate the begin-offset and end-offset
    String Bprev = text.substring(0, begin);
    String Eprev = text.substring(0, end - 1);
    int begin_offset = Bprev.replaceAll("\\s+", "").length();
    int end_offset = Eprev.replaceAll("\\s+", "").length();

    // add to aJCas
    annot.setBegin(begin + sentence.getBegin());
    annot.setEnd(end + sentence.getBegin());
    annot.setBoffset(begin_offset);
    annot.setEoffset(end_offset);
    annot.setIdentifier(sentence.getIdentifier());
    annot.addToIndexes();

    candidate.clear();
  }
}
