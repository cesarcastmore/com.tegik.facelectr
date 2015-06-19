package com.tegik.facelectr.ad_actionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.poc.EmailManager;
import org.openbravo.model.common.enterprise.EmailServerConfiguration;
import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.utils.FormatUtilities;

import com.tegik.facelectr.data.MensajeCorreo;
import com.tegik.facelectr.utilidad.Validate;
import com.tegik.facelectr.email.sendgrid.EmailAppi;
import com.tegik.facelectr.email.sendgrid.SendGrid;

import java.util.Arrays;
import java.util.Iterator;

/*http://www.tutorialspoint.com/html/html_url_encoding.htm*/

public class enviadorCorreos {

  private static final Logger log = Logger.getLogger(enviadorCorreos.class);
  private boolean solicitarEnvio = true;

  public String enviaCorreo(Invoice factura, String enviarPDF, String enviarXML, File pdf, File xml)
      throws Exception {

    OBContext.setAdminMode(true);

    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("DOCUMENT_ID", factura.getId());

    String correoAlternativo = factura.getFetCorreoalternativo();
    String correoCliente = factura.getBusinessPartner().getFetEmail();
    String correoElectronico = factura.getFETEmail();

    List<String> correos = new ArrayList<String>();

    if (Validate.validateEmail(correoAlternativo)) {
      correos.add(correoAlternativo);
    }

    if (Validate.validateEmail(correoCliente)) {
      correos.add(correoCliente);
    }

    if (Validate.validateEmail(correoElectronico)) {
      correos.add(correoElectronico);
    }

    // Obteniendo el mensaje de una configuracion
    String mensaje = "";
    String asunto = "";
    if (factura.getBusinessPartner().getFetMensajecorreo() != null) {

      String msj = factura.getBusinessPartner().getFetMensajecorreo().getMensaje();
      Parametrizacion para = new Parametrizacion(factura);
      mensaje = para.getMensajeParametros(msj);

      String asu = factura.getBusinessPartner().getFetMensajecorreo().getAsunto();
      Parametrizacion paraAsu = new Parametrizacion(factura);
      asunto = paraAsu.getMensajeParametros(asu);

    } else if (!(factura.getOrganization().getFetMensajeCorreoList().isEmpty())) {

      MensajeCorreo mensageCorreo = getMensajeCorreoDefault(factura);

      if (mensageCorreo == null) {
        mensaje = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
            + factura.getDocumentNo() + ". Gracias.";
        asunto = "Factura " + factura.getDocumentNo() + " correspondiente a su compra en "
            + factura.getOrganization().getSocialName();

      } else {

        String msj = mensageCorreo.getMensaje();
        Parametrizacion para = new Parametrizacion(factura);
        mensaje = para.getMensajeParametros(msj);

        String asu = mensageCorreo.getAsunto();
        Parametrizacion paraAsu = new Parametrizacion(factura);
        asunto = paraAsu.getMensajeParametros(asu);
      }

    } else {

      mensaje = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
          + factura.getDocumentNo() + ". Gracias.";
      asunto = "Factura " + factura.getDocumentNo() + " correspondiente a su compra en "
          + factura.getOrganization().getName();

    }
    
    boolean isSendGrid =factura.getClient().isFetEnviargrid() ==null ? false :  factura.getClient().isFetEnviargrid();

    if (isSendGrid) {
      
      if(factura.getClient().getFetUsuariogrid() == null){
        throw new Exception("FET_NoUserNameGrid");
      }else  if(factura.getClient().getFetPassgrid() == null){
        throw new Exception("FET_NoPassGrid");
      }else  if(factura.getClient().getFetFromgrid() == null){
        throw new Exception("FET_NoFrimGrid");
      }
        
      SendGrid sendgrid = new SendGrid(factura.getClient().getFetUsuariogrid(),factura.getClient().getFetPassgrid() );

      EmailAppi email = new EmailAppi();
      

      // Parametros de configuracion de
      email.setFrom(factura.getClient().getFetFromgrid());
      email.setFromName(factura.getClient().getFetFromnamegrid());
      email.setSubject(asunto);
      email.setText(mensaje);
      
      // Correos
      for (String co : correos) {
        email.addTo(co);

      }

      // Archivos
      email.addFile(xml);
      email.addFile(pdf);

      sendgrid.sent(email);

      return "OK";
    } else {

      final OBCriteria<EmailServerConfiguration> configList = OBDal.getInstance().createCriteria(
          EmailServerConfiguration.class);
      configList.add(Expression.eq(EmailServerConfiguration.PROPERTY_CLIENT, factura.getClient()));

      EmailServerConfiguration emailConfig = null;

      if (configList.list().isEmpty()) {
        throw new Exception("@FET_NoConfigEmail@");

      }

      for (EmailServerConfiguration emailConfigUd : configList.list()) {
        emailConfig = emailConfigUd;
      }

      // valida errores
      if (emailConfig.getSmtpPort() == null) {
        throw new Exception("@FET_NoPuertoSMTP@");
      } else if (emailConfig.getSmtpServer() == null) {
        throw new Exception("@FET_NoServerSMTP@");
      } else if (emailConfig.getSmtpServerAccount() == null) {
        throw new Exception("@FET_NoServerAccount@");
      } else if (emailConfig.getSmtpServerPassword() == null) {
        throw new Exception("@FET_NoPasswordSMPT@");
      } else if (emailConfig.getSmtpServerSenderAddress() == null) {
        throw new Exception("@FET_NoAddressSMTPM@");
      }

      String seguridad = emailConfig.getSmtpConnectionSecurity();
      Long LongPuerto = emailConfig.getSmtpPort();
      int Puerto = LongPuerto.intValue();

      String Servidor = emailConfig.getSmtpServer();

      boolean Auth = emailConfig.isSMTPAuthentification();

      String Cuenta = emailConfig.getSmtpServerAccount();
      String Password = FormatUtilities.encryptDecrypt(emailConfig.getSmtpServerPassword(), false);
      String CuentaEnvio = emailConfig.getSmtpServerSenderAddress();

      List<File> listaArchivos = new ArrayList<File>();
      listaArchivos.add(xml);
      listaArchivos.add(pdf);

      String correosAdjuntados = "";
      for (String co : correos) {
        correosAdjuntados = correosAdjuntados + co + ",";

      }
      correosAdjuntados.substring(0, correosAdjuntados.length() - 1);

      try {

        EmailManager.sendEmail(Servidor, Auth, Cuenta, Password, seguridad, Puerto, CuentaEnvio,
            correosAdjuntados, "", "", CuentaEnvio, asunto, mensaje, null, listaArchivos,
            new Date(), null);
        OBContext.restorePreviousMode();

        return "OK";

      } catch (Exception e) {
        OBContext.restorePreviousMode();

        throw new Exception(e.getMessage());
      }

    }
  }

  public String solicitarEnvio(Invoice factura, String enviarPDF, String enviarXML, File pdf,
      File xml) throws Exception {
    OBDal.getInstance().refresh(factura);
    String statusEnvio = enviaCorreo(factura, enviarPDF, enviarXML, pdf, xml);

    if (statusEnvio == "OK") {
      OBContext.setAdminMode(true);
      factura.setFetCorreoenviado(true);
      factura.setFetStatuscorreo("Correo enviado exitosamente");
      long intento = 0;
      factura.setFetIntento(intento);
      OBDal.getInstance().save(factura);
      OBDal.getInstance().flush();
      OBContext.restorePreviousMode();
      return "OK";
    } else if (statusEnvio.equals("@FET_NoCorreos@")) {
      return "OK";
    } else {
      OBContext.setAdminMode(true);
      factura.setFetCorreoenviado(false);
      factura.setFetStatuscorreo(statusEnvio);
      Long intento = factura.getFetIntento();
      long uno = 1;
      if (intento != null) {
        factura.setFetIntento(intento + uno);
      } else {
        factura.setFetIntento(uno);
      }
      OBDal.getInstance().save(factura);
      OBDal.getInstance().flush();
      OBContext.restorePreviousMode();
      return statusEnvio;
    }

  }

  public String checaFormatoEmail(String correo) {
    Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
    Matcher m = p.matcher(correo);
    boolean matchFound = m.matches();

    if (matchFound) {
      return "OK";
    } else {
      return "ERROR";
    }
  }

  public String prepararCorreos(String correos) {
    String regresar = "";
    List<String> listaCorreos = Arrays.asList(correos.split(","));
    // Obtenemos un Iterador y recorremos la lista.
    Iterator iter = listaCorreos.iterator();
    while (iter.hasNext()) {
      String correoUnit = (String) iter.next();
      correoUnit = correoUnit.trim().replaceAll("\\s+", "");

      if (checaFormatoEmail(correoUnit).equals("OK")) {
        regresar = regresar + correoUnit + ", ";
      }
    }

    return regresar;
  }

  public MensajeCorreo getMensajeCorreoDefault(Invoice inv) throws Exception {

    OBContext.setAdminMode(true);
    OBCriteria<MensajeCorreo> obMen = OBDal.getInstance().createCriteria(MensajeCorreo.class);
    obMen.add(Restrictions.eq(MensajeCorreo.PROPERTY_PORDEFECTO, true));
    obMen.add(Restrictions.eq(MensajeCorreo.PROPERTY_ORGANIZATION, inv.getOrganization()));
    List<MensajeCorreo> mensajes = obMen.list();

    if (mensajes.isEmpty()) {
      return null;
    }
    OBContext.restorePreviousMode();

    return mensajes.get(0);

  }


}