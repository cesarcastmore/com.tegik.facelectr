package com.tegik.facelectr.attributes;
/*Los pasos a seguir para colocar este java
 *-Copia este java  y muevelo a tu modulo. Cambia el package del java al nombre de tu modulo. Si gusta puede cambiar el nombre del java. 
 *-Verifica que tu modulo tenga un prefijo, sino lo tiene agregarselo en la ventana de modulo, la pestaña se llama "Prefijo de base de datos". 
 *-En la ventana de referencia, busque en el campo de nombre "Paquete de Agregado de campos" 
 *En la pestaña Lista, agregar una nueva linea con los siguientes datos:
 *-->Modulo-> escoge tu modulo de desarrollo o el modulo a donde moviste el java. 
 *-->Identificador-> debe ir primero el prefijo y un nombre por ejemplo FET_Modicacion. Esto se hace debido a que es un modulo externo y no es de factura electrinica. 
 *-->Nombre-> Coloca un nombre identificable para ti. 
 *-->Llamar Java-> Coloca el nombre de tu java con todo el paquete. */

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;

import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.attributes.AddAttributes;
import com.tegik.facelectr.attributes.ComprobanteModificado;

public class AnadirAtributos extends AddAttributes{
  
  //Metodo para agregar campos opcionales al CFDI o personalizar los atributos, usar este metodo, el CDFI es valido ante la SAT. 
  public void UpdateComprobante(Comprobante comprobante, Invoice invoice) throws Exception{
    
    comprobante.setNumCtaPago("1234567890");
    
    
  }


  //Metodo para agregar nuevos campos que no se parte del CFDI, usar este metodo puede que CDFI no sea valido ante la SAT 

  public void addAdditionalAtributes(ComprobanteModificado modificado, Invoice invoice) throws Exception {
    
    modificado.addAtribute("cfdi:Receptor", "impuesto", "12.12");   
    modificado.addAtribute("cfdi:Emisor", "documentNo", invoice.getDocumentNo());
    
  }

}
