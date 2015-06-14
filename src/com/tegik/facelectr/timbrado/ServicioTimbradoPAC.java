package com.tegik.facelectr.timbrado;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.TimbreFiscalDigital;

import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.utilidad.Convertidor;

public  abstract class ServicioTimbradoPAC {
  
  public Invoice invoice;
  public File fileXML;
  public Comprobante comp;
  public InforTimbrado infoTimbrado;
  public HashMap<String, Attachment> files;
  
  public void setInvoice(Invoice invoice){
    this.invoice=invoice;
  }
  
  public void setFileXML(File file){
    this.fileXML=file;
  }
  
  public void setComprobante(Comprobante comprobante){
    this.comp=comprobante;
  }
  
  public void setInfoTimbrado(InforTimbrado infoTimbrado){
    this.infoTimbrado=infoTimbrado;
  }
  
  public void setArchivoAdjuntadoOrgLegal(HashMap<String, Attachment> files){
    this.files=files;
  }
  
  
  public String getXMLToCadena() throws IOException{
    return Convertidor.toString(fileXML);
  }
  
  public abstract TimbreFiscalDigital timbrarFactura() throws Exception;
  public abstract void cancelarFactura() throws Exception;
  

}
