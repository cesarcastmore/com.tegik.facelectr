package com.tegik.facelectr.comprobante;

import org.apache.log4j.Logger;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.action.CreateElectronicInvoice;
import com.tegik.facelectr.addenda.CreateAddenda;
import com.tegik.facelectr.data.Adenda;
import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Validate;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Complemento;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Addenda;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Conceptos;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Emisor;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Receptor;


public abstract class CreateComprobante {
  
  private static final Logger log = Logger.getLogger(CreateElectronicInvoice.class);

  Invoice invoice;
  
  //Variable que se va llenando con respeto a los datos de Openbravo
  ComprobanteOpenbravo comprobanteOB= new ComprobanteOpenbravo();
  
  
  public String getTipoDoc(){
    String documento = invoice.getTransactionDocument().getDocumentCategory();
    String tipoDoc = documento.equals("ARC") ? "N" : "Y";
    return tipoDoc;
    
  }
  
  public Invoice getInvoice(){
    return invoice;
  }
  
  public ComprobanteOpenbravo getComprobanteOpenbravo(){
    return comprobanteOB;
  }
  
 
  public Comprobante createComprobantePersonalizado(Invoice invoice) throws Exception{
    
    this.invoice = invoice;
    
    Comprobante comp = createComprobante();
    
    
    comp.setEmisor(createEmisor());
    comp.setReceptor(createReceptor());
    comp.setConceptos(createConceptos());
    comp.setImpuestos(createImpuestos());
    comp.setComplemento(new Complemento());
    
    
    Adenda adenda = invoice.getBusinessPartner().getFetAdenda();
    if(adenda != null){
    
      String nombreClaseJava = Finder.getJavaClass(adenda.getListaopciones());
      Validate.validateJavaAddenda(nombreClaseJava);
    
      if(nombreClaseJava != null){
        comp.setAddenda(createAddenda(nombreClaseJava));
      }
      
    }
        
    
    return comp;
  }
  
  
 private Addenda createAddenda( String javaClass) throws Exception  {
    
    Addenda addenda = new Addenda();
    
    
    try {
      
      CreateAddenda createAddenda = (CreateAddenda) Class.forName(javaClass).newInstance();
      for(Object obj : createAddenda.getObjects(invoice)){
        addenda.getAny().add(obj);
      }
    return addenda;
      
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new Exception (e.getMessage());
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new Exception (e.getMessage());
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new Exception (e.getMessage());
    }catch (Exception e) {
      e.printStackTrace();
      throw new Exception (e.getMessage());
    }
  }
  
  
   public abstract Comprobante createComprobante() throws Exception;
   public abstract Receptor createReceptor()  throws Exception;
   public abstract Emisor createEmisor()  throws Exception;
   public abstract Conceptos createConceptos()throws Exception;
   public abstract Impuestos createImpuestos() throws Exception;
    

}
