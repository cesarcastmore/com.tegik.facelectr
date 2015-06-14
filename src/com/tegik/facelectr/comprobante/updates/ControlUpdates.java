package com.tegik.facelectr.comprobante.updates;

import java.util.List;
import java.util.ArrayList;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;

import org.openbravo.model.common.invoice.Invoice;


public class ControlUpdates {
  
  List<UpdatingComprobante> updates= new ArrayList<UpdatingComprobante>();
  
  public ControlUpdates(){
    
    updates.add(new MoverIEPSAConceptos());
    
  }
  
  
  public void execute(Comprobante comprobante, ComprobanteOpenbravo comprobanteOB) throws Exception{
    
    for(UpdatingComprobante update : updates){
      update.UpdateComprobante(comprobante, comprobanteOB);
    }
    
  }
  
  
  

}
