package com.tegik.facelectr.comprobante;

import java.io.File;

import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.scheduling.ProcessBundle;

import mx.bigdata.sat.cfdi.v32.schema.TimbreFiscalDigital;


public interface GenerateFileXML {
  
  //Metodo para generar el XML desde una factura, es utilizado tanto para lo que timbran y tanto para los que generan el XML 
  public File createFileXML(Invoice invoice) throws Exception;
  
  //Metodo de cancelacion para ser utilizado a los PAC que generan el XML en automatico
  public void cancel(Invoice invoice) throws Exception; 

}
