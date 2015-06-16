package com.tegik.facelectr.ad_actionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.tegik.facelectr.email.SendGrid;
import com.tegik.facelectr.email.SendGrid.Email;

import java.util.Arrays;
import java.util.Iterator;


public class enviadorCorreos  {

 

  private static final Logger log = Logger.getLogger(enviadorCorreos.class);
  private boolean solicitarEnvio=true;

  public String enviaCorreo( Invoice factura, String enviarPDF,
      String enviarXML, File pdf, File xml)  throws Exception {
    
    
      OBContext.setAdminMode(true);      

      HashMap<String, Object> parameters = new HashMap<String, Object>();
      parameters.put("DOCUMENT_ID", factura.getId());
      
    

      String correoAlternativo = factura.getFetCorreoalternativo();
      String correoCliente = factura.getBusinessPartner().getFetEmail();
      String correoElectronico = factura.getFETEmail();
      
      if(correoAlternativo == null && correoCliente ==null && correoElectronico == null ){
        solicitarEnvio=false;
        return "@FET_NoCorreos@";
      }
      
      
      String Correo = "";

      if (correoCliente == null) {
        correoCliente = "";
      }

      if (correoAlternativo == null) {
        correoAlternativo = "";
      }

      // revisa que el correo no este vacio
      if (correoElectronico == null) {
        correoElectronico = "";
      }

      if (correoCliente != null || !correoCliente.equals("")) {
          Correo = prepararCorreos(correoCliente);
      }

      if (correoAlternativo != null || !correoAlternativo.equals("")) {

          if (Correo == null || Correo.equals("")) {
            Correo = prepararCorreos(correoAlternativo);
          } 
          else 
          {
            Correo = Correo + ", " + prepararCorreos(correoAlternativo);
          }
      }

      // revisa formato de correo
      if (correoElectronico != null || !correoElectronico.equals("")) {
      
          if (Correo == null || Correo.equals("")) 
          {
            Correo = prepararCorreos(correoElectronico);
          } 
          else 
          {

            Correo = Correo + ", " + prepararCorreos(correoElectronico);
          }
          
      }

      Correo = prepararCorreos(Correo);

      final OBCriteria<EmailServerConfiguration> configList = OBDal.getInstance().createCriteria(
          EmailServerConfiguration.class);
      configList.add(Expression.eq(EmailServerConfiguration.PROPERTY_CLIENT, factura.getClient()));
      

      EmailServerConfiguration emailConfig = null;
      
      if(configList.list().isEmpty()){
        throw new Exception("@FET_NoConfigEmail@");
        
      }

      for (EmailServerConfiguration emailConfigUd : configList.list()) {
        emailConfig = emailConfigUd;
      }
      
      //valida errores

      if(emailConfig.getSmtpPort() == null){
        throw new Exception("@FET_NoPuertoSMTP@");
      } else if(emailConfig.getSmtpServer() == null){
        throw new Exception("@FET_NoServerSMTP@");
      } else if(emailConfig.getSmtpServerAccount() == null){
        throw new Exception("@FET_NoServerAccount@");
      } else if(emailConfig.getSmtpServerPassword() == null){
        throw new Exception("@FET_NoPasswordSMPT@");
      } else if(emailConfig.getSmtpServerSenderAddress() == null){
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

      
      //Obteniendo el mensaje de una configuracion 
      String mensaje="";
      String asunto="";
      if(factura.getBusinessPartner().getFetMensajecorreo() != null ){
        
        String msj= factura.getBusinessPartner().getFetMensajecorreo().getMensaje();
        Parametrizacion para = new  Parametrizacion(factura);
        mensaje = para.getMensajeParametros(msj);
        
        String asu= factura.getBusinessPartner().getFetMensajecorreo().getAsunto();
        Parametrizacion paraAsu = new  Parametrizacion(factura);
        asunto = paraAsu.getMensajeParametros(asu);
        
      }
      else if(!(factura.getOrganization().getFetMensajeCorreoList().isEmpty())){
        
        
       MensajeCorreo mensageCorreo= getMensajeCorreoDefault(factura);
       
       if(mensageCorreo == null){
         mensaje = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
             + factura.getDocumentNo() + ". Gracias.";
         asunto = "Factura " + factura.getDocumentNo() + " correspondiente a su compra en "
             + factura.getOrganization().getSocialName();
         
       } else {
       
       String msj= mensageCorreo.getMensaje();
       Parametrizacion para = new  Parametrizacion(factura);
       mensaje = para.getMensajeParametros(msj);
       
       String asu= mensageCorreo.getAsunto();
       Parametrizacion paraAsu = new  Parametrizacion(factura);
       asunto = paraAsu.getMensajeParametros(asu);
       }
       
      }else {

      mensaje = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
          + factura.getDocumentNo() + ". Gracias.";
      asunto = "Factura " + factura.getDocumentNo() + " correspondiente a su compra en "
          + factura.getOrganization().getSocialName();
      
    }
      
 
      List<File> listaArchivos = new ArrayList<File>();
      listaArchivos.add(xml);
      listaArchivos.add(pdf);
      
      if (Correo.equals("")) {
        return "El correo electrónico del receptor no es válido";
      }
      
      /*
      EmailManager correo = new EmailManager();

      // correo.sendEmail(Servidor, Auth, Cuenta, Password, seguridad,Puerto, CuentaEnvio, Correo,
      // "", "csalinas@tegik.com", CuentaEnvio, Asunto, Mensaje, null, listaArchivos, new Date(),
      // null);
      try{
        correo.sendEmail(Servidor, Auth, Cuenta, Password, seguridad, Puerto, CuentaEnvio, Correo,
          "", "", CuentaEnvio, asunto, mensaje, null, listaArchivos, new Date(), null);
      } catch(Exception e){
        log.info("password "+ Password + " usuario "+ CuentaEnvio);        
        throw new Exception(e.getMessage());
      }*/

      SendGrid sendgrid = new SendGrid("ccastillo", "Kopoyeba00");
      
      Email email = new Email();
      
      
      String[] correos = new String[3];
      correos[0]=correoAlternativo;
      correos[1]=correoCliente;
      correos[2]=correoElectronico;
      
      email.addTo(correos);
      email.setFrom(Cuenta);
      email.setSubject(asunto);
      email.setText(mensaje);
      
      email.addAttachment(xml.getName(), xml);
      email.addAttachment(pdf.getName(), pdf);
      
      sendgrid.send(email);


      
      
      
      
      OBContext.restorePreviousMode();
      return "OK";
  
  }

  public String solicitarEnvio(Invoice factura, String enviarPDF, String enviarXML, File pdf, File xml) throws Exception {
      log.info("ENTRO A SOLICITAR ENVIO");
      OBDal.getInstance().refresh(factura);
      String statusEnvio = enviaCorreo( factura, enviarPDF, enviarXML,pdf, xml);
      log.info(statusEnvio);
      
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
      }else if(statusEnvio.equals("@FET_NoCorreos@")){
        log.info("ENTROOO AQUI");
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

  
    public String prepararCorreos(String correos)
    {
	String regresar = "";
	log.info("PREPARARCORREOS - ENTRADA - " + correos);
	List<String> listaCorreos = Arrays.asList(correos.split(","));
	// Obtenemos un Iterador y recorremos la lista.
	Iterator iter = listaCorreos.iterator();
	while (iter.hasNext())
	{
	    String correoUnit = (String) iter.next();
	    correoUnit = correoUnit.trim().replaceAll("\\s+","");
	    
	    if (checaFormatoEmail(correoUnit).equals("OK"))
	    {
		regresar = regresar + correoUnit + ", ";
	    }
	}
	
	log.info("PREPARARCORREOS - SALIDA - " + regresar);
	return regresar;
    }
    
    public MensajeCorreo getMensajeCorreoDefault(Invoice inv) throws Exception{

      OBContext.setAdminMode(true);
      OBCriteria<MensajeCorreo> obMen = OBDal.getInstance().createCriteria(MensajeCorreo.class);
      obMen.add(Restrictions.eq(MensajeCorreo.PROPERTY_PORDEFECTO, true));
      obMen.add(Restrictions.eq(MensajeCorreo.PROPERTY_ORGANIZATION, inv.getOrganization()));
      List<MensajeCorreo> mensajes = obMen.list();
   
      
      if(mensajes.isEmpty()){
        return null;
      }
      OBContext.restorePreviousMode();

      return mensajes.get(0);
      
      
    }


}