package edu.cmu.lti.f14.hw3.hw3_feixia.casconsumers;

import java.util.Comparator;

import edu.cmu.lti.f14.hw3.hw3_feixia.docrept.DocVec;
import edu.cmu.lti.f14.hw3.hw3_feixia.docrept.GeneralDocRept;

/**
 * Different kinds of comparators that are used to do sorting.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
class CosSimComparator implements Comparator<DocVec> {
  @Override
  public int compare(DocVec lhs, DocVec rhs) {
    if (lhs.getCosSim() < rhs.getCosSim()) {
      return 1;
    } else if (lhs.getCosSim() > rhs.getCosSim()) {
      return -1;
    } else {
      return rhs.getRel() - lhs.getRel();
    }
  }
}


class QidComparator implements Comparator<DocVec> {
  @Override
  public int compare(DocVec lhs, DocVec rhs) {
    if (lhs.getqid() < rhs.getqid()) {
      return -1;
    } else if (lhs.getqid() > rhs.getqid()) {
      return 1;
    } else {
      return 0;
    }
  }
}

class GeneralSimComparator implements Comparator<GeneralDocRept> {
  @Override
  public int compare(GeneralDocRept lhs, GeneralDocRept rhs) {
    if (lhs.getSim() < rhs.getSim()) {
      return 1;
    } else if (lhs.getSim() > rhs.getSim()) {
      return -1;
    } else {
      return rhs.getRel() - lhs.getRel();
    }
  }
}

class GeneralQidComparator implements Comparator<GeneralDocRept> {
  @Override
  public int compare(GeneralDocRept lhs, GeneralDocRept rhs) {
    if (lhs.getqid() < rhs.getqid()) {
      return -1;
    } else if (lhs.getqid() > rhs.getqid()) {
      return 1;
    } else {
      return 0;
    }
  }
}