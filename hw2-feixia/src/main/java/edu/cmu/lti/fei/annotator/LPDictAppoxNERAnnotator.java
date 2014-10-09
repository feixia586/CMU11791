package edu.cmu.lti.fei.annotator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.ApproxDictionaryChunker;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.TrieDictionary;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.spell.WeightedEditDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.cmu.deiis.types.Annotation;
import edu.cmu.deiis.types.Sentence;
import edu.cmu.lti.fei.util.CasProcessID;
import edu.cmu.lti.fei.util.FileOp;

/**
 * An annotator that discovers Gene Name Entity in the document text. This uses LingPipe tool and a
 * Dictionary to do the approximate annotation.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class LPDictAppoxNERAnnotator extends JCasAnnotator_ImplBase {
  /**
   * The dictionary path
   */
  private String mDictPath;

  /**
   * The maximum distance of the approximation
   */
  private float mMaxDistance;

  /**
   * The object of ApproxDictionaryChunker, used to do chunk
   */
  private ApproxDictionaryChunker mChunker;

  /**
   * Perform initialization logic. Read the dict and initialize the mChunker.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    mDictPath = (String) getContext().getConfigParameterValue("DictPath");
    mMaxDistance = (Float) getContext().getConfigParameterValue("MaxDistance");
    TrieDictionary<String> dict = new TrieDictionary<String>();

    String content = getFileAsStream(mDictPath);
    String[] lines = content.split("\n");
    for (int i = 0; i < lines.length; ++i) {
      DictionaryEntry<String> entry = new DictionaryEntry<String>(lines[i], "GENE");
      dict.addEntry(entry);
    }

    TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;

    WeightedEditDistance editDistance = new FixedWeightEditDistance(0, -1, -1, 1, Double.NaN);

    mChunker = new ApproxDictionaryChunker(dict, tokenizerFactory, editDistance, mMaxDistance);
  }

  /**
   * Annotate to find out the Gene Name Entity. This use LingPipe and a dictionary to do Approximate
   * annotation. The confidence score of the annotation will be set to 1.0
   * 
   * @param aJCas
   *          the JCas object.
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    Sentence sentence = (Sentence) SentenceIter.next();
    String text = sentence.getCoveredText();
    Chunking chunking = mChunker.chunk(text);
    Set<Chunk> chunkSet = chunking.chunkSet();
    for (Chunk chunk : chunkSet) {
      Annotation annot = new Annotation(aJCas);

      int begin = chunk.start();
      int end = chunk.end();

      // add to index
      annot.setBegin(sentence.getBegin() + begin);
      annot.setEnd(sentence.getBegin() + end);
      annot.setIdentifier(sentence.getIdentifier());
      annot.setCasProcessorId(CasProcessID.LPDICTAppox);
      annot.setConfidence((float) 1.0);
      annot.addToIndexes();
    }
  }

  /**
   * Read file through stream for LPDictAppoxNERAnnotator
   * 
   * @param filePath
   *          the file path
   * @return the string of the file
   * @throws ResourceInitializationException
   */
  private String getFileAsStream(String filePath) throws ResourceInitializationException {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = LPDictAppoxNERAnnotator.class.getClassLoader().getResourceAsStream(filePath);

      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      br.close();
    } catch (Exception ex) {
      System.out.println("[Error]: Look Below.");
      ex.printStackTrace();
      System.out.println("[Error]: Look Above.");
      throw new ResourceInitializationException();
    }

    String content = sb.toString();
    return content;
  }
}
