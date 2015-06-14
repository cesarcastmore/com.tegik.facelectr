package com.tegik.facelectr.comprobante.updates;

import java.math.BigDecimal;
import java.util.List;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Traslados.Traslado;

import org.apache.log4j.Logger;
import org.openbravo.model.common.invoice.InvoiceTax;

import com.tegik.facelectr.comprobante.ComprobanteOpenbravo;


public class MoverIEPSAConceptos implements UpdatingComprobante {

  private static final Logger log = Logger.getLogger(MoverIEPSAConceptos.class);

  @Override
  public void UpdateComprobante(Comprobante comprobante, ComprobanteOpenbravo comprobanteOB) throws Exception {
    
    boolean desgIEPS =  ( comprobanteOB.getReceptor().isFetDesglosarieps()== null ?
        true : comprobanteOB.getEncabezado().getBusinessPartner().isFetDesglosarieps());
    
    log.info("ENTRO AQUI A CAMBIAR TODO1");

    
    //Si no esta selecionada, debe de eliminar IEPS
    if(!desgIEPS){
      
      if(comprobante.getImpuestos().getTraslados() != null){      
        
        List<Traslado> translados = comprobante.getImpuestos().getTraslados().getTraslado();
                
        for(int trans=0; trans < translados.size() ; trans++ ){
          
          Traslado translado = translados.get(trans);
          InvoiceTax trasladoOB = comprobanteOB.getTraslados().get(trans);
          boolean isieps = trasladoOB.getTax().isFetIsieps() ==null ? false : 
            trasladoOB.getTax().isFetIsieps();
          
          //Quita el IEPS de la lista
          log.info("ENTRO AQUI A CAMBIAR TODO2");
          if(translado.getImpuesto().equals("IEPS") && isieps){
            
            BigDecimal tasa = translado.getImporte();
            BigDecimal importe = translado.getTasa();
            comprobante.getImpuestos().getTraslados().getTraslado().remove(trans);
                     
            BigDecimal totalImpuestoTraslados = comprobante.getImpuestos().getTotalImpuestosTrasladados();
            comprobante.getImpuestos().setTotalImpuestosTrasladados(totalImpuestoTraslados.subtract(importe));
            
            BigDecimal porcentaje = tasa.multiply(new BigDecimal("0.01"));
            porcentaje = porcentaje.add(new BigDecimal("1.00"));
            
            for(int i=0;i<comprobante.getConceptos().getConcepto().size(); i++){
              
              BigDecimal importeCon = comprobante.getConceptos().getConcepto().get(i).getImporte();
              log.info("porcentaje: "+porcentaje.toString() );
              log.info("IMPORTE "+importeCon);

              importeCon = importeCon.multiply(porcentaje);
              
              log.info("IMPORTEPORCENTAJE "+importeCon);

              comprobante.getConceptos().getConcepto().get(i).setImporte(importeCon);
             
              
            }//Termina el for
                   
            
          }//termina el if is es ieps
          
        }//Termina el for de traslados
        
      }//termina el if de validar que no este en null 
      
      
    }
    
    
    
  }

}
