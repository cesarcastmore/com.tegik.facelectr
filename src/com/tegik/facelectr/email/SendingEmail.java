package com.tegik.facelectr.email;
/*
 * Esta clase es para enviar correo ya sea de la configuracion de sendgrid o la configuracion normal de Openbravo
 */
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.enterprise.EmailServerConfiguration;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Translate;
import com.tegik.facelectr.email.params.DefaultPersonalizarEmail;

public class SendingEmail {

  private static final Logger log = Logger.getLogger(SendingEmail.class);

  CustomizeEmail personalizar = new DefaultPersonalizarEmail();
  Invoice invoice;
  List<File> archivos;
  
 
  

  public SendingEmail(Invoice invoice, List<File> archivos, CustomizeEmail emailCustimize) throws Exception {

    this.invoice = invoice;
    this.archivos = archivos;
    
    this.personalizar = emailCustimize;
    this.personalizar.setInvoice(invoice);


  }

  public void sentInvoice() {
    try {

      if (this.personalizar.enviarCorreo()) {
        send(invoice, archivos);
        invoice.setFetStatuscorreo("Los archivos ha sido enviados correctamente");
        invoice.setFetCorreoenviado(true);
      }

    } catch (Exception e) {

      e.printStackTrace();

      String msg = Translate.translate(e.getMessage());
      invoice.setFetStatuscorreo(msg);
      invoice.setFetCorreoenviado(false);

    }

    OBDal.getInstance().save(invoice);
    OBDal.getInstance().flush();

  }

  private void send(Invoice invoice, List<File> archivos) throws Exception {

    String correoTercero = invoice.getBusinessPartner().getFetEmail();
    String correoElectronico = invoice.getFETEmail();
    String correoAlternativo = invoice.getFetCorreoalternativo();

    ServiceEmail serviceEmail = new ServiceEmail();

    serviceEmail.addEmail(correoAlternativo);
    serviceEmail.addEmail(correoElectronico);
    serviceEmail.addEmail(correoTercero);

    if (!this.personalizar.getListaCorreos().isEmpty()) {
      for (String correo : this.personalizar.getListaCorreos()) {
        serviceEmail.addEmail(correo);
      }
    }

    String mensaje = this.personalizar.getMessage();
    String asunto = this.personalizar.getSubject();
    
    serviceEmail.setAsunto(asunto);
    serviceEmail.setMensaje(mensaje);

    for (File archivo : archivos) {
      serviceEmail.addArchivo(archivo);

    }

    if (this.personalizar.getCustomizeSendGrid().isSendGrid()) {
      
      serviceEmail.send(this.personalizar.getCustomizeSendGrid());
            

    } else {

      EmailServerConfiguration emailConfig = Finder.findConfigEmail(invoice.getClient());

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

      serviceEmail.send(emailConfig);

    }

  }




}
