package com.tegik.facelectr.utilidad;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openbravo.model.common.invoice.Invoice;


public class Parametrizacion {
  
  public String caracterInicial;
  public String caracterFin;
  
  Map paraPersonalizados = new HashMap();

  
  

  private static final Logger log = Logger.getLogger(Parametrizacion.class);
  
  public Parametrizacion(Invoice invoice){
    paraPersonalizados = new HashMap();
    paraPersonalizados.put("NumeroFactura", invoice.getDocumentNo() );
    paraPersonalizados.put("NombreCliente", invoice.getBusinessPartner().getName());
    paraPersonalizados.put("Total", invoice.getGrandTotalAmount().toString());
    
    caracterInicial="<";
    caracterFin=">";
  
    
  }
  
  
  public String getMensajeParametros(String mensaje){
    log.info("MENSAGE QUE ENTROO" +  mensaje);
 
   try {
      List<String>  para =obtenerParametros(mensaje);
      List<String> datos=obtenerDatosParametros(para);
      return sustituirParametros(mensaje, para, datos);
      
    } catch (Exception e) {

      e.printStackTrace();
    }
    return "";
    
  }
  
  

  public  List<String> obtenerParametros(String mensaje) {

    List<String> para = new ArrayList();
    log.info("MENSAGE QUE ENTROO" +  mensaje);

    while (mensaje.indexOf(caracterInicial) > -1) {

      int inicio = mensaje.indexOf(caracterInicial) + 1;
      String nuevo = mensaje.substring(inicio);
      int fin = inicio + nuevo.indexOf(caracterFin);
      para.add(mensaje.substring(inicio, fin));
      mensaje = mensaje.substring(fin + 1);

    }
    return para;

  }

  public List<String> obtenerDatosParametros(List<String> parametros)
      throws Exception {

    List<String> datos = new ArrayList();
    

    for (String par : parametros) {
      
      if (paraPersonalizados.containsKey(par)) {
        
        datos.add((String)paraPersonalizados.get(par));
        
      } else {

        throw new Exception("No se encontro el parametro " + par);

      }

    }
    

    return datos;

  }

  public  String sustituirParametros(String mensaje, List<String> para, List<String> datoPara) {

    for (int i = 0; i < para.size(); i++) {

      mensaje = mensaje.replace(caracterInicial + para.get(i) + caracterFin, datoPara.get(i));
    }

    return mensaje;

  }


}
