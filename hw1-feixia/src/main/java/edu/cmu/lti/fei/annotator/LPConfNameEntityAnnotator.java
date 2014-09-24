package edu.cmu.lti.fei.annotator;

import java.io.File;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.lti.fei.type.NameEntity;
import edu.cmu.lti.fei.type.Sentence;

/**
 * An annotator that discovers Gene Name Entity in the document text. This uses
 * LingPipe tool and a pretrained model to do the annotation. It will filter the 
 * results according the confidence threshold.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class LPConfNameEntityAnnotator extends JCasAnnotator_ImplBase {
  private String mModelPath;
  
  /**
   * The number of best matches in a sentence
   */
  private Integer mMAX_N_BEST_CHUNKS;
  
  /**
   * The confidence threshold
   */
  private Float mThreshold;

  /**
   * The ConfidenceChunker object, used to do annotation
   */
  private ConfidenceChunker mChunker = null;

  /**
   * Perform initialization logic. Read the model and initialize the mChunker.
   * 
   * @param aContext the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    mModelPath = (String) getContext().getConfigParameterValue("ModelPath");
    mMAX_N_BEST_CHUNKS = (Integer) getContext().getConfigParameterValue("MAX_N_BEST_CHUNKS");
    mThreshold = (Float) getContext().getConfigParameterValue("Threshold");

    try {
      mChunker = (ConfidenceChunker) AbstractExternalizable.readObject(new File(mModelPath));
    } catch (Exception ex) {
      System.err.println("[Error] Reading Gene Tag Model Error!!!");
      System.exit(1);
    }
  }

  /**
   * Annotate to find out the Gene Name Entity. This use LingPipe and a pretrained model to do 
   * annotation. Then the annotation with low confidence will be filtered out.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    // iterate over all sentences
    int num = 0;
    while (SentenceIter.hasNext()) {
      num++;
      if (num % 500 == 0) {
        System.out.println("Working: " + num);
      }

      Sentence sentence = (Sentence) SentenceIter.next();
      String text = sentence.getCoveredText();
      char[] cs = text.toCharArray();

      Iterator<Chunk> iter = mChunker.nBestChunks(cs, 0, cs.length, mMAX_N_BEST_CHUNKS);
      while (iter.hasNext()) {
        NameEntity annot = new NameEntity(aJCas);

        Chunk chunk = iter.next();
        if (!chunk.type().equals("GENE")) {
          System.out.println(chunk.type());
        }
        
        // check confidence and threshold
        double conf = (double) Math.pow(2.0, chunk.score());
        if (conf < mThreshold) {
          continue;
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
        annot.setConfidence((float)conf);
        annot.addToIndexes();
      }
    }
  }

}
