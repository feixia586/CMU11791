package edu.cmu.lti.f14.hw3.hw3_feixia.casconsumers;

import java.util.Comparator;

import edu.cmu.lti.f14.hw3.hw3_feixia.docrept.DocVec;

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
