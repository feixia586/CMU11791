package edu.cmu.lti.fei.annotator;

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

public class LPDictAppoxNERAnnotator extends JCasAnnotator_ImplBase {
  String mDictPath;

  float mMaxDistance;

  ApproxDictionaryChunker mChunker;

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

    String content = FileOp.readFromFile(mDictPath);
    String[] lines = content.split("\n");
    for (int i = 0; i < lines.length; ++i) {
      if (i % 100 == 0) {
        System.out.println(i);
      }
      DictionaryEntry<String> entry = new DictionaryEntry<String>(lines[i], "GENE");
      dict.addEntry(entry);
    }

    TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;

    WeightedEditDistance editDistance = new FixedWeightEditDistance(0, -1, -1, 1, Double.NaN);

    mChunker = new ApproxDictionaryChunker(dict, tokenizerFactory, editDistance, mMaxDistance);
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();
    while (SentenceIter.hasNext()) {
      Sentence sentence = (Sentence) SentenceIter.next();
      String text = sentence.getCoveredText();
      Chunking chunking = mChunker.chunk(text);
      Set<Chunk> chunkSet = chunking.chunkSet();
      for (Chunk chunk : chunkSet) {
        Annotation annot = new Annotation(aJCas);
        
        int begin = chunk.start();
        int end = chunk.end();
        
        annot.setBegin(sentence.getBegin() + begin);
        annot.setEnd(sentence.getBegin() + end);
        annot.setIdentifier(sentence.getIdentifier());
        annot.setCasProcessorId(CasProcessID.LPDICTAppox);
        annot.setConfidence((float)1.0);
        annot.addToIndexes();
      }
    }
  }

}
