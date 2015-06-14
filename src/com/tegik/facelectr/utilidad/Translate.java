package com.tegik.facelectr.utilidad;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openbravo.base.exception.OBException;
import org.openbravo.dal.core.OBContext;
import org.openbravo.database.ConnectionProvider;
import org.openbravo.erpCommon.utility.Utility;
import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.service.db.DalConnectionProvider;


public class Translate {
  
  public static String caracterInicial="@";;
  public static String caracterFin="@";;

  private static final Logger log = Logger.getLogger(Translate.class);
  
  
  
  public static String translate(String mensaje){
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
  
  

  private  static List<String> obtenerParametros(String mensaje) {

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

  private static List<String> obtenerDatosParametros(List<String> parametros)
      throws Exception {

    List<String> datos = new ArrayList();
    

    for (String par : parametros) {
      
      String language = OBContext.getOBContext().getLanguage().getLanguage();
      ConnectionProvider conn = new DalConnectionProvider(false);
      String translate = Utility.messageBD(conn, par, language);

      datos.add(translate);  

    }
    

    return datos;

  }

  private static  String sustituirParametros(String mensaje, List<String> para, List<String> datoPara) {

    for (int i = 0; i < para.size(); i++) {

      mensaje = mensaje.replace(caracterInicial + para.get(i) + caracterFin, datoPara.get(i));
    }

    return mensaje;

  }


}
