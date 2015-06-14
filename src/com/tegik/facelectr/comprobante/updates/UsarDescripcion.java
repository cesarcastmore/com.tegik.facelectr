package com.tegik.facelectr.comprobante.updates;

import java.math.BigDecimal;
import java.util.List;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Traslados.Traslado;

import org.apache.log4j.Logger;
import org.openbravo.model.common.invoice.InvoiceLine;
import org.openbravo.model.common.invoice.InvoiceTax;

import com.tegik.facelectr.comprobante.ComprobanteOpenbravo;


public class UsarDescripcion implements UpdatingComprobante {

  private static final Logger log = Logger.getLogger(UsarDescripcion.class);

  @Override
  public void UpdateComprobante(Comprobante comprobante, ComprobanteOpenbravo comprobanteOB) throws Exception {
    
    List<InvoiceLine> lineas =comprobanteOB.getConceptos();
    
    for(int i=0; i<lineas.size(); i++ ){
      
      InvoiceLine linea =lineas.get(i); 
      
      String descripcionExtra = "";
      if (linea.getDescription() != null ) {
        if (!lineas.get(i).getDescription().equals("CARGO POR USADO")) {
          descripcionExtra = lineas.get(i).getDescription();
        }

      }

      if (linea.getProduct() != null) {
        
        String description = comprobante.getConceptos().getConcepto().get(i).getDescripcion();
        if (linea.getClient().isFetUsardescripcion()) {
          
          comprobante.getConceptos().getConcepto().get(i).setDescripcion(description + " " + descripcionExtra);
          
        } else {
          comprobante.getConceptos().getConcepto().get(i).setDescripcion(linea.getProduct().getName() + " " + descripcionExtra);
        }


        if (linea.getClient().isFetUsaridentificador()) {
          comprobante.getConceptos().getConcepto().get(i).setNoIdentificacion(linea.getProduct().getSearchKey());
        }
      } else {
        comprobante.getConceptos().getConcepto().get(i).setDescripcion(linea.getAccount().getName());
      }
      
    }

    

    
 
      
    
    
    
    
  }

}
