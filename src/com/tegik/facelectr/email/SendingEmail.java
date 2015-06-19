package com.tegik.facelectr.email;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.enterprise.EmailServerConfiguration;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Parametrizacion;
import com.tegik.facelectr.utilidad.Translate;
import com.tegik.facelectr.utilidad.Validate;
import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.data.MensajeCorreo;

public class SendingEmail {

  private static final Logger log = Logger.getLogger(SendingEmail.class);

  PersonalizarEmail personalizar = new DefaultPersonalizarEmail();
  Invoice invoice;
  List<File> archivos;
  InforTimbrado timbrado;

  public SendingEmail(Invoice invoice, List<File> archivos) throws Exception {

    this.invoice = invoice;
    this.archivos = archivos;
    this.timbrado = invoice.getFetOrglegal().getFetInfotimbrado();

    // Busca una clase para personalizar tus mensajes y si quieres enviar correo
    if (timbrado.getAgregado() != null) {

      String clasejavaCorreo = Finder.getJavaClass(timbrado.getListajavasenvio());
      Validate.validateJavaCorreo(clasejavaCorreo);

      this.personalizar = (PersonalizarEmail) Class.forName(clasejavaCorreo).newInstance();
      this.personalizar.setInvoice(invoice);

    } else {

      this.personalizar = new DefaultPersonalizarEmail();
      this.personalizar.setInvoice(invoice);

    }

  }

  public void sentInvoice() {
    try {

      if (this.personalizar.enviarCorreo() && this.timbrado.isEnviarcorreo()) {
        send(invoice, archivos);
        invoice.setFetStatuscorreo("Los archivos ha sido enviados correctamente");
        invoice.setFetCorreoenviado(true);
      }

    } catch (Exception e) {

      e.printStackTrace();
      log.info("Exception " + e.getMessage());

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

    String mensaje = "";
    String asunto = "";
    InforTimbrado infoTimbrado = invoice.getFetOrglegal().getFetInfotimbrado();
    MensajeCorreo msgGenerico = Finder.findMensaje(invoice.getOrganization(), infoTimbrado);
    MensajeCorreo msgLegal = Finder.findMensaje(invoice.getFetOrglegal(), infoTimbrado);

    if (this.personalizar.getMensage() != null || this.personalizar.getAsunto() != null) {
      mensaje = this.personalizar.getMensage();
      asunto = this.personalizar.getAsunto();
    }

    else if (msgGenerico != null) {

      String msj = msgGenerico.getMensaje();
      Parametrizacion para = new Parametrizacion(invoice);
      mensaje = para.getMensajeParametros(msj);

      String asu = msgGenerico.getAsunto();
      Parametrizacion paraAsu = new Parametrizacion(invoice);
      asunto = paraAsu.getMensajeParametros(asu);

    } else if (msgLegal != null) {
      String msj = msgLegal.getMensaje();
      Parametrizacion para = new Parametrizacion(invoice);
      mensaje = para.getMensajeParametros(msj);

      String asu = msgLegal.getAsunto();
      Parametrizacion paraAsu = new Parametrizacion(invoice);
      asunto = paraAsu.getMensajeParametros(asu);

    } else {

      mensaje = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
          + invoice.getDocumentNo() + ". Gracias.";
      asunto = "Factura " + invoice.getDocumentNo() + " correspondiente a su compra en "
          + invoice.getOrganization().getSocialName();

    }

    serviceEmail.setAsunto(asunto);
    serviceEmail.setMensaje(mensaje);

    for (File archivo : archivos) {
      serviceEmail.addArchivo(archivo);

    }

    if (infoTimbrado.isEnviargrid()) {
      
      serviceEmail.send(infoTimbrado);
            

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
