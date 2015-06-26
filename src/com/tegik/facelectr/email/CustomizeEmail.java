package com.tegik.facelectr.email;

import java.util.List;

import org.apache.log4j.Logger;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.email.params.InfoTimMsgEmail;

public abstract class CustomizeEmail {
  
  private static final Logger log = Logger.getLogger(CustomizeEmail.class);

  public Invoice invoice;
  public CustomizeMessages msg;
  public CustomizeSendGrid sendGrid;
  
  public void setInvoice(Invoice invoice){
    this.invoice = invoice;
  }
  
  public void setCustomizeMessages(CustomizeMessages customized){
    log.info("Asignando variable de customixed Messages"+  customized.getSubject());
    log.info("Asignando variable de customixed Messages"+  customized.getMessage());

    this.msg=customized;
  }
  
  
  public CustomizeMessages getCustomizeMessages(){
    return this.msg;
  }
  
  public void setCustomizeSendGrid(CustomizeSendGrid sendGrid){
    this.sendGrid=sendGrid;
  }
  
  
  public CustomizeSendGrid getCustomizeSendGrid(){
    return this.sendGrid;
  }
  
  
  
  public String getSubject(){
    
    if(createSubject() != null)
      return createSubject();
    else return this.msg.getSubject();
    
  }
  
  
  public String getMessage(){
    
    if(createMessage() != null){
      return createMessage();
    }
    else {
      return this.msg.getMessage();
    }
        
    
  }
  
  public abstract boolean enviarCorreo();
  
  public  abstract List<String> getListaCorreos();
  
  public  abstract String createSubject();
  
  public abstract String createMessage();
  

}
