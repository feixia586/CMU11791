

/* First created by JCasGen Mon Sep 22 12:40:01 EDT 2014 */
package edu.cmu.lti.fei.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Sep 24 22:23:40 EDT 2014
 * XML source: /home/fei/Projects/java_workspace/CMU11791/hw1-feixia/src/main/resources/descriptors/AAE.xml
 * @generated */
public class Sentence extends Entry {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentence.class);
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
  protected Sentence() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Sentence(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Sentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Sentence(JCas jcas, int begin, int end) {
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
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_identifier == null)
      jcasType.jcas.throwFeatMissing("identifier", "edu.cmu.lti.fei.type.Sentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_identifier);}
    
  /** setter for identifier - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIdentifier(String v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_identifier == null)
      jcasType.jcas.throwFeatMissing("identifier", "edu.cmu.lti.fei.type.Sentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_identifier, v);}    
  }

    