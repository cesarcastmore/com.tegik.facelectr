package com.tegik.facelectr.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.openbravo.erpCommon.utility.poc.EmailManager;
import org.openbravo.model.common.enterprise.EmailServerConfiguration;
import org.openbravo.utils.FormatUtilities;

import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.email.sendgrid.EmailAppi;
import com.tegik.facelectr.email.sendgrid.SendGrid;
import com.tegik.facelectr.servicios.ConnectionException;
import com.tegik.facelectr.utilidad.Validate;


public class ServiceEmail {
  
  private static final Logger log = Logger.getLogger(ServiceEmail.class);

  
  List<String> correos = new ArrayList<String>();
  List<File> archivos;
    
  String mensaje="";
  String asunto="";

  
  public ServiceEmail(){
    archivos= new ArrayList<File>();
    correos = new ArrayList<String>();
  }
  
  public void addEmail(String correo) throws Exception{   
    if(correo != null){
      if(Validate.validateEmail(correo)){
        correos.add(correo);
      } else {
        throw new Exception("@FET_Correo@" + correo+ "@FET_EsInvalidoCorreo@"); 
      }
      
    }
    
  }


  
  public void send(EmailServerConfiguration emailConfig) throws Exception{
    
    String servidor = emailConfig.getSmtpServer();   
  
    boolean auth = emailConfig.isSMTPAuthentification();

    String cuenta = emailConfig.getSmtpServerAccount();
    String password = FormatUtilities.encryptDecrypt(emailConfig.getSmtpServerPassword(), false);
    String cuentaEnvio = emailConfig.getSmtpServerSenderAddress();
    String seguridad = emailConfig.getSmtpConnectionSecurity();
    int puerto = emailConfig.getSmtpPort().intValue(); 
    
    String correosAdjuntados="";
    if(correos.isEmpty()){
      throw new Exception("@FET_NoHayCorreos@");
    }
    
    for (String correo : correos){
      correosAdjuntados =correosAdjuntados +  correo + ",";
      
    }

    correosAdjuntados.substring(0, correosAdjuntados.length()-1);
    
    
    EmailManager emailManager = new EmailManager();
    
    
    emailManager.sendEmail(servidor, auth, cuenta, password, seguridad, puerto, cuentaEnvio, correosAdjuntados,
        "", "", cuentaEnvio, asunto, mensaje, null, archivos, new Date(), null);
    
    
    
    
  }
  
  
  public void send(InforTimbrado infoTimbrado) throws Exception{
    
    if(infoTimbrado.getUsernamegrid() == null){
      throw new Exception("@FET_NoUserNameGrid@");
    } else if(infoTimbrado.getPassgrid() == null){
      throw new Exception("@FET_NoPassGrid@");
    }else if(infoTimbrado.getFromgrid() == null){
      throw new Exception("@FET_NoFromGrid@");
    }
     
    SendGrid sendgrid = new SendGrid(infoTimbrado.getUsernamegrid(), infoTimbrado.getPassgrid() );

    EmailAppi email = new EmailAppi();

    // Parametros de configuracion de
    email.setFrom(infoTimbrado.getFromgrid());
    email.setFromName(infoTimbrado.getFromnamegrid());
    
    email.setSubject(asunto);
    email.setText(mensaje);

    // Correos
    for (String co : correos) {
      email.addTo(co);

    }

    // Archivos
    for(File file: archivos){
      email.addFile(file);
    }

    sendgrid.sent(email);
    
  }


  /**
   * @return the mensaje
   */
  public String getMensaje() {
    return mensaje;
  }


  /**
   * @param mensaje the mensaje to set
   */
  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }


  /**
   * @return the asunto
   */
  public String getAsunto() {
    return asunto;
  }


  /**
   * @param asunto the asunto to set
   */
  public void setAsunto(String asunto) {
    this.asunto = asunto;
  }
  
  public void addArchivo(File file){
    archivos.add(file);
  }
  
  
  
  
  

}
