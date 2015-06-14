package com.tegik.facelectr.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;

import org.apache.log4j.Logger;
import org.openbravo.erpCommon.utility.poc.EmailManager;
import org.openbravo.model.common.enterprise.EmailServerConfiguration;
import org.openbravo.utils.FormatUtilities;

import com.tegik.facelectr.utilidad.Validate;


public class ServiceEmail {
  
  private static final Logger log = Logger.getLogger(ServiceEmail.class);

  
  List<String> correos = new ArrayList<String>();
  List<File> archivos;
  
  EmailServerConfiguration emailConfig;
  
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


  /**
   * @return the emailConfig
   */
  public EmailServerConfiguration getEmailConfig() {
    return emailConfig;
  }


  /**
   * @param emailConfig the emailConfig to set
   */
  public void setEmailConfig(EmailServerConfiguration emailConfig) {
    this.emailConfig = emailConfig;
  }
  
  
  public void send() throws Exception{
    
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
