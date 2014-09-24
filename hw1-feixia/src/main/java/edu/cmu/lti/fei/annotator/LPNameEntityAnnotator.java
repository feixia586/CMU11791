package edu.cmu.lti.fei.annotator;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.lti.fei.type.NameEntity;
import edu.cmu.lti.fei.type.Sentence;

public class LPNameEntityAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIndex<?> SentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator<?> SentenceIter = SentenceIndex.iterator();

    // init LingPipe Model
    Chunker chunker = null;
    try {
      chunker = (Chunker) AbstractExternalizable
              .readObject(new File("model/ne-en-bio-genetag.HmmChunker"));
    } catch (Exception ex) {
      System.err.println("[Error] Reading Gene Tag Model Error!!!");
      System.exit(1);
    }
    
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

        if (!chunk.type().equals("GENE")) {System.out.println(chunk.type());}
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
