package com.tegik.facelectr.ad_actionButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Expression;
import org.openbravo.base.session.OBPropertiesProvider;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
//import mx.bigdata.cfdi.CFDv3;
//import java.net.*;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.erpCommon.utility.SequenceIdData;
import org.openbravo.model.ad.datamodel.Table;
import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.invoice.Invoice;

public class cancelarFactura {

  private static final Logger log = Logger.getLogger(cancelarFactura.class);

  String cancelarFac(String ruta, String NumFac, String RFC_Emisor, String RFC_Receptor,
      String uuid, String refID, String PasswordPAC, String archivoPac, String urlWebService)
      throws IOException {
    String strTimbrar = "";

    log.info(ruta + "Request_Cancela" + NumFac + ".xml");
    try {

      // Empieza Crear el encapsualado SOAP para la conexion a WS del PAC
      OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(ruta
          + "Request_Cancela" + NumFac + ".xml", false), "UTF-8");
      BufferedWriter encabezado = new BufferedWriter(writer);
      // encabezado.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      encabezado
          .write("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.buzonfiscal.com/ns/xsd/bf/bfcorp/3\">\n");
      encabezado.write("  <soapenv:Header/>\n");
      encabezado.write("  <soapenv:Body>\n");
      // encabezado.write("      <ns:RequestCancelaCFDi RfcEmisor=\""+RFC_Emisor+"\" RfcReceptor=\""+RFC_Receptor+"\" uuid=\""+uuid+"\" refID=\""+refID+"\"/>\n");
      encabezado.write("      <ns:RequestCancelaCFDi rfcEmisor=\"" + RFC_Emisor
          + "\" rfcReceptor=\"" + RFC_Receptor + "\" uuid=\"" + uuid + "\"/>\n");
      encabezado.write("  </soapenv:Body>\n");
      encabezado.write("</soapenv:Envelope>\n");
      encabezado.close();
      // Termina crear el encapsualado SOAP

      String respuesta;
      clientCancela client = new clientCancela();
      System.setProperty("javax.net.debug", "all");
      log.info("DEMO-CANCELACION // archivoPac // " + archivoPac);
      log.info("DEMO-CANCELACION // PasswordPAC // " + PasswordPAC);
      log.info("DEMO-CANCELACION // NumFac // " + NumFac);
      log.info("DEMO-CANCELACION // ruta // " + ruta);
      respuesta = client.call(urlWebService, ruta, NumFac, PasswordPAC, archivoPac);
      return respuesta;

    }

    catch (Exception e) {
      e.printStackTrace();
      return e.getLocalizedMessage();
    }
  }
  
  
  public OBError cancelarFactura(Invoice invoice){
    
    String NumFac = invoice.getDocumentNo();
    String RFC_Emisor = invoice.getOrganization().getOrganizationInformationList().get(0)
        .getTaxID();
    String RFC_Receptor = invoice.getBusinessPartner().getTaxID();
    
    String nombrePfx = "";
    String pathPfx = "";
    
    final OBCriteria<Attachment> attachmentList = OBDal.getInstance().createCriteria(
        Attachment.class);
    attachmentList.add(Expression.eq(Attachment.PROPERTY_TABLE,
        OBDal.getInstance().get(Table.class, "155")));
    attachmentList.add(Expression.eq(Attachment.PROPERTY_RECORD, invoice.getOrganization()
        .getId()));

    for (Attachment attachmentUd : attachmentList.list()) {
      if (attachmentUd.getName().indexOf(".pfx") != -1) {
        nombrePfx = attachmentUd.getName();
        pathPfx = attachmentUd.getPath();
        if (pathPfx == "" || pathPfx == null) {
            pathPfx = "155" + "-" + invoice.getOrganization().getId();
        }
      }
    }
    
    String Separador = System.getProperty("file.separator");
    String attachFolder = OBPropertiesProvider.getInstance().getOpenbravoProperties()
        .getProperty("attach.path");
    String ruta = attachFolder + "/" + "318" + "-" + invoice.getId()
        + Separador;
    
    String archivoPac = attachFolder + "/" + pathPfx + Separador.substring(0, 1) + nombrePfx;    
    String uuidFactura = invoice.getFetFoliofiscal();
    String urlCancelar = invoice.getClient().getFetUrlwebser();
    String PasswordPAC = invoice.getOrganization().getFetPasspac();
    String refId = SequenceIdData.getUUID();

    cancelarFactura cancelFact = new cancelarFactura();
    String ruta_response, canceled, mensage, fechaCanc;

    OBError msg = new OBError();
    ruta_response = ruta + "ResponseCancela" + NumFac + ".xml";

    File f = new File(ruta_response);
    if (f.exists()) {
      canceled = getValuefromXML(ruta_response, "canceled");
      fechaCanc = getValuefromXML(ruta_response, "fecha");
      if (canceled.equals("true")) {
        invoice.setFetDocstatus("Cancelado");
        invoice.setFetFechacancel(fechaCanc);
        OBDal.getInstance().save(invoice);
        OBDal.getInstance().flush();
        msg.setType("Success");
        msg.setMessage("La Factura ha sido cancelada");
        return msg;

      } else {
      try
      {
        ruta_response = cancelFact.cancelarFac(ruta, NumFac, RFC_Emisor, RFC_Receptor,
            uuidFactura, refId, PasswordPAC, archivoPac, urlCancelar);
        canceled = getValuefromXML(ruta_response, "canceled");
        mensage = getValuefromXML(ruta_response, "message");
        fechaCanc = getValuefromXML(ruta_response, "fecha");
        
        if (canceled.equals("true")) {
          
          invoice.setFetDocstatus("Cancelado");
          invoice.setFetFechacancel(fechaCanc);
          OBDal.getInstance().save(invoice);
          OBDal.getInstance().flush();
          msg.setType("Success");
          msg.setMessage("La Factura ha sido cancelada");
          return msg;
          
        } else {
          msg.setType("Error");
          msg.setMessage(mensage);
          return msg;
        }
       }catch (Exception e)
       {
          msg.setType("Error");
          msg.setMessage("Error en la comunicación con el PAC. Contacte al equipo de soporte");
          return msg;
       }
      }

    }

    else {
      try
      {
            ruta_response = cancelFact.cancelarFac(ruta, NumFac, RFC_Emisor, RFC_Receptor, uuidFactura,
                refId, PasswordPAC, archivoPac, urlCancelar);
            canceled = getValuefromXML(ruta_response, "canceled");
            mensage = getValuefromXML(ruta_response, "message");
            fechaCanc = getValuefromXML(ruta_response, "fecha");
            if (canceled.equals("true")) {
              invoice.setFetDocstatus("Cancelado");
              invoice.setFetFechacancel(fechaCanc);
              OBDal.getInstance().save(invoice);
              OBDal.getInstance().flush();
              msg.setType("Success");
              msg.setMessage("La Factura ha sido cancelada");
              return msg;
              
            } else {
              
              msg.setType("Error");
              msg.setMessage(mensage);
              return msg;
              
            }
       }
      catch (Exception e)
       {
          msg.setType("Error");
          msg.setMessage("Error en la comunicación con el PAC. Contacte al equipo de soporte");
          return msg;
       }
    }
    
    
    
    
  }
  
  
  
  String getValuefromXML(String f, String str) {
    int ch;
    String xml1;
    StringBuffer strContent = new StringBuffer("");
    FileInputStream fin = null;
    try {
      fin = new FileInputStream(f);
      while ((ch = fin.read()) != -1)
        strContent.append((char) ch);
      fin.close();
    } catch (Exception e) {
      return "";
    }
    xml1 = strContent.toString();
    try {
      String[] ret = xml1.split(str + "=\"");
      if (ret.length >= 2)
        return ret[1].substring(0, ret[1].indexOf("\""));
      else
        return "";
    } catch (Exception e) {
      return "";
    }
  }
  
}
