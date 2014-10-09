package edu.cmu.lti.fei.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.Annotation;
import edu.cmu.deiis.types.BestAnnot;
import edu.cmu.lti.fei.util.CasProcessID;

/**
 * An annotator that does the voting. It will annotate the best begin and end index.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class VotingAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Process to try to find out the best annotation.
   * 
   * @param aJCas
   *          the JCas object.
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    Iterator<?> AnnotationIter = aJCas.getAnnotationIndex(Annotation.type).iterator();

    List<Annotation> lpConfAnnots = new ArrayList<Annotation>();
    List<Annotation> lpDictExactAnnots = new ArrayList<Annotation>();
    List<Annotation> abnerAnnots = new ArrayList<Annotation>();

    while (AnnotationIter.hasNext()) {
      Annotation annot = (Annotation) AnnotationIter.next();
      if (annot.getCasProcessorId().equals(CasProcessID.LPCONF)) {
        lpConfAnnots.add(annot);
      } else if (annot.getCasProcessorId().equals(CasProcessID.LPDICTExact)) {
        lpDictExactAnnots.add(annot);
      } else if (annot.getCasProcessorId().equals(CasProcessID.ABNER)) {
        abnerAnnots.add(annot);
      }
    }

    // get the winning annotation list
    List<Annotation> annotList = doVoting(lpConfAnnots, lpDictExactAnnots, abnerAnnots);

    // add the annotation info to the BestAnnot
    for (Annotation annot : annotList) {
      BestAnnot bestAnnot = new BestAnnot(aJCas);
      bestAnnot.setBegin(annot.getBegin());
      bestAnnot.setEnd(annot.getEnd());
      bestAnnot.setIdentifier(annot.getIdentifier());
      bestAnnot.setCasProcessorId(CasProcessID.BEST);
      bestAnnot.setConfidence((float) 1.0);
      bestAnnot.addToIndexes();
    }
  }

  /*
  public List<Annotation> takeOneVoting(List<Annotation> lpConfAnnots,
          List<Annotation> lpDictExactAnnots, List<Annotation> abnerAnnots) {
    List<Annotation> annots = new ArrayList<Annotation>();
    for (int i = 0; i < lpConfAnnots.size(); i++) {
      annots.add(lpConfAnnots.get(i));
    }
    for (int i = 0; i < lpDictExactAnnots.size(); i++) {
      annots.add(lpDictExactAnnots.get(i));
    }
    for (int i = 0; i < abnerAnnots.size(); i++) {
      annots.add(abnerAnnots.get(i));
    }

    return annots;
  }
  */

  /**
   * Do the voting.
   * 
   * @param lpConfAnnots the LPConf annotations
   * @param lpDictExactAnnots the LPDict annotations
   * @param abnerAnnots the abner annotations
   * @return a list of winning annotations
   */
  private List<Annotation> doVoting(List<Annotation> lpConfAnnots,
          List<Annotation> lpDictExactAnnots, List<Annotation> abnerAnnots) {
    List<Annotation> annots = new ArrayList<Annotation>();
    Set<String> visited = new HashSet<String>();

    // checking from LPConf annotations
    for (int i = 0; i < lpConfAnnots.size(); ++i) {
      int lpConfBegin = lpConfAnnots.get(i).getBegin();
      int lpConfEnd = lpConfAnnots.get(i).getEnd();
      for (int j = 0; j < lpDictExactAnnots.size(); ++j) {
        if (lpConfBegin <= lpDictExactAnnots.get(j).getBegin()
                && lpConfEnd >= lpDictExactAnnots.get(j).getEnd()
                && !visited.contains(lpConfBegin + "-" + lpConfEnd)) {
          visited.add(lpConfBegin + "-" + lpConfEnd);
          annots.add(lpConfAnnots.get(i));
        }
      }

      for (int j = 0; j < abnerAnnots.size(); ++j) {
        if (lpConfBegin <= abnerAnnots.get(j).getBegin()
                && lpConfEnd >= abnerAnnots.get(j).getEnd()
                && !visited.contains(lpConfBegin + "-" + lpConfEnd)) {
          visited.add(lpConfBegin + "-" + lpConfEnd);
          annots.add(lpConfAnnots.get(i));
        }
      }

    }

    // checking from LPDict annotations
    for (int i = 0; i < lpDictExactAnnots.size(); ++i) {
      int lpDictExactBegin = lpDictExactAnnots.get(i).getBegin();
      int lpDictExactEnd = lpDictExactAnnots.get(i).getEnd();
      for (int j = 0; j < lpConfAnnots.size(); ++j) {
        if (lpDictExactBegin <= lpConfAnnots.get(j).getBegin()
                && lpDictExactEnd >= lpConfAnnots.get(j).getEnd()
                && !visited.contains(lpDictExactBegin + "-" + lpDictExactEnd)) {
          visited.add(lpDictExactBegin + "-" + lpDictExactEnd);
          annots.add(lpDictExactAnnots.get(i));
        }
      }

      for (int j = 0; j < abnerAnnots.size(); ++j) {
        if (lpDictExactBegin <= abnerAnnots.get(j).getBegin()
                && lpDictExactEnd >= abnerAnnots.get(j).getEnd()
                && !visited.contains(lpDictExactBegin + "-" + lpDictExactEnd)) {
          visited.add(lpDictExactBegin + "-" + lpDictExactEnd);
          annots.add(lpDictExactAnnots.get(i));
        }
      }

    }

    // checking from the abner annotations
    for (int i = 0; i < abnerAnnots.size(); ++i) {
      int abnerBegin = abnerAnnots.get(i).getBegin();
      int abnerEnd = abnerAnnots.get(i).getEnd();
      for (int j = 0; j < lpDictExactAnnots.size(); ++j) {
        if (abnerBegin == lpDictExactAnnots.get(j).getBegin()
                && abnerEnd == lpDictExactAnnots.get(j).getEnd()
                && !visited.contains(abnerBegin + "-" + abnerEnd)) {
          visited.add(abnerBegin + "-" + abnerEnd);
          annots.add(abnerAnnots.get(i));
        }
      }

      for (int j = 0; j < lpConfAnnots.size(); ++j) {
        if (abnerBegin == lpConfAnnots.get(j).getBegin()
                && abnerEnd == lpConfAnnots.get(j).getEnd()
                && !visited.contains(abnerBegin + "-" + abnerBegin)) {
          visited.add(abnerAnnots + "-" + abnerEnd);
          annots.add(abnerAnnots.get(i));
        }
      }

    }

    return annots;
  }

}
