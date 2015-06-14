package com.tegik.facelectr.attributes;

/*
 * Esta interfaces es para agregar los campos opcionales si el cliente los requiere o agregar nuevos campos
 * o atributos que no contiene el CFDI, en pocas palabras personalizar tu CFDI
 *
 */
import org.openbravo.model.common.invoice.Invoice;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;


public abstract class AddAttributes {
  
  //Metodo para agregar campos opcionales al CFDI o personalizar los atributos, usar este metodo, el CDFI es valido ante la SAT. 
 public abstract void UpdateComprobante(Comprobante comprobante, Invoice invoice) throws Exception;
 
 //Metodo para agregar nuevos campos que no se parte del CFDI, usar este metodo puede que CDFI no sea valido ante la SAT 
 public abstract void addAdditionalAtributes(ComprobanteModificado modificado, Invoice invoice) throws Exception;


}
