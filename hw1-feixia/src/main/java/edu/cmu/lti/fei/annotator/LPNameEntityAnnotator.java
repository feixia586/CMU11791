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
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.lti.fei.type.NameEntity;
import edu.cmu.lti.fei.type.Sentence;

/**
 * An annotator that discovers Gene Name Entity in the document text. This uses
 * LingPipe tool and a pretrained model to do the annotation. It won't give 
 * confidence of the annotation.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class LPNameEntityAnnotator extends JCasAnnotator_ImplBase {

  /**
   * The model path
   */
  private String mModelPath;

  /**
   * The Chunker instance, used to do the annotation
   */
  private Chunker mChunker = null;

  /**
   * Perform initialization logic. Read the model and initialize the mChunker.
   * 
   * @param aContext the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    mModelPath = (String) getContext().getConfigParameterValue("ModelPath");
    try {
      mChunker = (Chunker) AbstractExternalizable.readObject(new File(mModelPath));
    } catch (Exception ex) {
      System.err.println("[Error] Reading Gene Tag Model Error!!!");
      System.exit(1);
    }
  }

  /**
   * Annotate to find out the Gene Name Entity. This use LingPipe and a pretrained model to do 
   * annotation. 
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    // iterate all sentences
    int num = 0;
    while (SentenceIter.hasNext()) {
      num++;
      if (num % 500 == 0) {
        System.out.println("Working: " + num);
      }

      Sentence sentence = (Sentence) SentenceIter.next();
      String text = sentence.getCoveredText();

      // get annotated chunks
      Set<Chunk> chunkSet = mChunker.chunk(text).chunkSet();
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

        // add to aJCas
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
