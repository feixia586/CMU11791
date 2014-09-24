package edu.cmu.lti.fei.annotator;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.lti.fei.type.NameEntity;
import edu.cmu.lti.fei.type.Sentence;

public class LPNameEntityAnnotator extends JCasAnnotator_ImplBase {

  private String mModelPath;

  private Chunker chunker = null;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    mModelPath = (String) getContext().getConfigParameterValue("ModelPath");
    try {
      chunker = (Chunker) AbstractExternalizable.readObject(new File(mModelPath));
    } catch (Exception ex) {
      System.err.println("[Error] Reading Gene Tag Model Error!!!");
      System.exit(1);
    }
  };

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    // init LingPipe Model

    int num = 0;
    while (SentenceIter.hasNext()) {
      num++;
      if (num % 500 == 0) {
        System.out.println("Working: " + num);
      }

      Sentence sentence = (Sentence) SentenceIter.next();
      String text = sentence.getCoveredText();

      Set<Chunk> chunkSet = chunker.chunk(text).chunkSet();
      for (Chunk chunk : chunkSet) {
        NameEntity annot = new NameEntity(aJCas);

        if (!chunk.type().equals("GENE")) {
          System.out.println(chunk.type());
        }
        int begin = chunk.start();
        int end = chunk.end();
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
      }
    }
  }

}
