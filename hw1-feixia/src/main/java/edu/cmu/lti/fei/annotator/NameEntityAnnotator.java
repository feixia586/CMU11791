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
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class NameEntityAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    // init Stanford NLP
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    while (SentenceIter.hasNext()) {
      Sentence sentence = (Sentence) SentenceIter.next();
      String text = sentence.getCoveredText();

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

  private void addField(Sentence sentence, List<CoreLabel> candidate, JCas aJCas) {
    NameEntity annot = new NameEntity(aJCas);

    String text = sentence.getCoveredText();
    int begin = candidate.get(0).beginPosition();
    int end = candidate.get(candidate.size() - 1).endPosition();
    String Bprev = text.substring(0, begin);
    String Eprev = text.substring(0, end - 1);

    int begin_offset = Bprev.replaceAll("\\s+", "").length();
    int end_offset = Eprev.replaceAll("\\s+", "").length();

    annot.setBegin(begin + sentence.getBegin());
    annot.setEnd(end + sentence.getBegin());
    annot.setBoffset(begin_offset);
    annot.setEoffset(end_offset);
    annot.setIdentifier(sentence.getIdentifier());
    annot.addToIndexes();

    candidate.clear();
    System.out.println(sentence.getIdentifier() + "|" + begin_offset + " " + end_offset + "|" + annot.getCoveredText());
  }

}