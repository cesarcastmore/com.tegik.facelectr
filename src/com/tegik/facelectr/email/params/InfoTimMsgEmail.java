package com.tegik.facelectr.email.params;

import org.apache.log4j.Logger;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.data.MensajeCorreo;
import com.tegik.facelectr.email.CustomizeMessages;
import com.tegik.facelectr.email.SendingEmail;
import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Parametrizacion;

public class InfoTimMsgEmail implements CustomizeMessages {
  
  private static final Logger log = Logger.getLogger(InfoTimMsgEmail.class);

  String msg;
  String subject;
  Invoice invoice;
  
  public  void initializaVars(Invoice invoice) throws Exception{
    log.info("Inisializado InforTImbMsgEmail");
    this.invoice=invoice;
    creatingMessages();
  }
  

  @Override
  public String getSubject() {
    // TODO Auto-generated method stub
    return this.subject;
  }

  @Override
  public String getMessage() {
    // TODO Auto-generated method stub
    return this.msg;
  }
  
  
  public void creatingMessages() throws Exception{
    
    
    InforTimbrado infoTimbrado = invoice.getFetOrglegal().getFetInfotimbrado();
    MensajeCorreo msgGenerico = Finder.findMensaje(invoice.getOrganization(), infoTimbrado);
    MensajeCorreo msgLegal = Finder.findMensaje(invoice.getFetOrglegal(), infoTimbrado);


    if (msgGenerico != null) {

      String msj = msgGenerico.getMensaje();
      Parametrizacion para = new Parametrizacion(invoice);
      this.msg = para.getMensajeParametros(msj);

      String asu = msgGenerico.getAsunto();
      Parametrizacion paraAsu = new Parametrizacion(invoice);
      this.subject = paraAsu.getMensajeParametros(asu);

    } else if (msgLegal != null) {
      String msj = msgLegal.getMensaje();
      Parametrizacion para = new Parametrizacion(invoice);
      this.msg = para.getMensajeParametros(msj);

      String asu = msgLegal.getAsunto();
      Parametrizacion paraAsu = new Parametrizacion(invoice);
      this.subject = paraAsu.getMensajeParametros(asu);

    } else {
      
      log.info("Mensahe por defecto");


      this.msg = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
          + invoice.getDocumentNo() + ". Gracias.";
      this.subject = "Factura " + invoice.getDocumentNo() + " correspondiente a su compra en "
          + invoice.getOrganization().getSocialName();

    }
    
    log.info("asigna mensjes " +this.subject );
    log.info("asigna mensjes " +this.msg );

    
    
  }

}
