

/* First created by JCasGen Mon Sep 22 20:29:49 EDT 2014 */
package edu.cmu.lti.fei.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Sep 23 21:46:49 EDT 2014
 * XML source: /home/fei/Projects/java_workspace/CMU11791/hw1-feixia/src/main/resources/descriptors/AAE.xml
 * @generated */
public class NameEntity extends BaseAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NameEntity.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected NameEntity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NameEntity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NameEntity(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NameEntity(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: identifier

  /** getter for identifier - gets 
   * @generated
   * @return value of the feature 
   */
  public String getIdentifier() {
    if (NameEntity_Type.featOkTst && ((NameEntity_Type)jcasType).casFeat_identifier == null)
      jcasType.jcas.throwFeatMissing("identifier", "edu.cmu.lti.fei.type.NameEntity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NameEntity_Type)jcasType).casFeatCode_identifier);}
    
  /** setter for identifier - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIdentifier(String v) {
    if (NameEntity_Type.featOkTst && ((NameEntity_Type)jcasType).casFeat_identifier == null)
      jcasType.jcas.throwFeatMissing("identifier", "edu.cmu.lti.fei.type.NameEntity");
    jcasType.ll_cas.ll_setStringValue(addr, ((NameEntity_Type)jcasType).casFeatCode_identifier, v);}    
   
    
  //*--------------*
  //* Feature: boffset

  /** getter for boffset - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBoffset() {
    if (NameEntity_Type.featOkTst && ((NameEntity_Type)jcasType).casFeat_boffset == null)
      jcasType.jcas.throwFeatMissing("boffset", "edu.cmu.lti.fei.type.NameEntity");
    return jcasType.ll_cas.ll_getIntValue(addr, ((NameEntity_Type)jcasType).casFeatCode_boffset);}
    
  /** setter for boffset - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBoffset(int v) {
    if (NameEntity_Type.featOkTst && ((NameEntity_Type)jcasType).casFeat_boffset == null)
      jcasType.jcas.throwFeatMissing("boffset", "edu.cmu.lti.fei.type.NameEntity");
    jcasType.ll_cas.ll_setIntValue(addr, ((NameEntity_Type)jcasType).casFeatCode_boffset, v);}    
   
    
  //*--------------*
  //* Feature: eoffset

  /** getter for eoffset - gets 
   * @generated
   * @return value of the feature 
   */
  public int getEoffset() {
    if (NameEntity_Type.featOkTst && ((NameEntity_Type)jcasType).casFeat_eoffset == null)
      jcasType.jcas.throwFeatMissing("eoffset", "edu.cmu.lti.fei.type.NameEntity");
    return jcasType.ll_cas.ll_getIntValue(addr, ((NameEntity_Type)jcasType).casFeatCode_eoffset);}
    
  /** setter for eoffset - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEoffset(int v) {
    if (NameEntity_Type.featOkTst && ((NameEntity_Type)jcasType).casFeat_eoffset == null)
      jcasType.jcas.throwFeatMissing("eoffset", "edu.cmu.lti.fei.type.NameEntity");
    jcasType.ll_cas.ll_setIntValue(addr, ((NameEntity_Type)jcasType).casFeatCode_eoffset, v);}    
  }

    