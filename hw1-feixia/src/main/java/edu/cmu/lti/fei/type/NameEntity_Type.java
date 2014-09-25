
/* First created by JCasGen Mon Sep 22 20:29:49 EDT 2014 */
package edu.cmu.lti.fei.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** 
 * Updated by JCasGen Wed Sep 24 22:23:40 EDT 2014
 * @generated */
public class NameEntity_Type extends Sentence_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (NameEntity_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = NameEntity_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new NameEntity(addr, NameEntity_Type.this);
  			   NameEntity_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new NameEntity(addr, NameEntity_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NameEntity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.fei.type.NameEntity");
 
  /** @generated */
  final Feature casFeat_boffset;
  /** @generated */
  final int     casFeatCode_boffset;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getBoffset(int addr) {
        if (featOkTst && casFeat_boffset == null)
      jcas.throwFeatMissing("boffset", "edu.cmu.lti.fei.type.NameEntity");
    return ll_cas.ll_getIntValue(addr, casFeatCode_boffset);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBoffset(int addr, int v) {
        if (featOkTst && casFeat_boffset == null)
      jcas.throwFeatMissing("boffset", "edu.cmu.lti.fei.type.NameEntity");
    ll_cas.ll_setIntValue(addr, casFeatCode_boffset, v);}
    
  
 
  /** @generated */
  final Feature casFeat_eoffset;
  /** @generated */
  final int     casFeatCode_eoffset;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getEoffset(int addr) {
        if (featOkTst && casFeat_eoffset == null)
      jcas.throwFeatMissing("eoffset", "edu.cmu.lti.fei.type.NameEntity");
    return ll_cas.ll_getIntValue(addr, casFeatCode_eoffset);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEoffset(int addr, int v) {
        if (featOkTst && casFeat_eoffset == null)
      jcas.throwFeatMissing("eoffset", "edu.cmu.lti.fei.type.NameEntity");
    ll_cas.ll_setIntValue(addr, casFeatCode_eoffset, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public NameEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_boffset = jcas.getRequiredFeatureDE(casType, "boffset", "uima.cas.Integer", featOkTst);
    casFeatCode_boffset  = (null == casFeat_boffset) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_boffset).getCode();

 
    casFeat_eoffset = jcas.getRequiredFeatureDE(casType, "eoffset", "uima.cas.Integer", featOkTst);
    casFeatCode_eoffset  = (null == casFeat_eoffset) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eoffset).getCode();

  }
}



    