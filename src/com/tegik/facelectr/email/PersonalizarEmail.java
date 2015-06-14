package com.tegik.facelectr.email;

import java.util.List;

import org.openbravo.model.common.invoice.Invoice;

public abstract class PersonalizarEmail {
  
  Invoice invoice;
  
  public void setInvoice(Invoice invoice){
    this.invoice = invoice;
  }
  
  
  public abstract boolean enviarCorreo();
  
  public  abstract List<String> getListaCorreos();
  
  public  abstract String getAsunto();
  
  public abstract String getMensage();
  

}
