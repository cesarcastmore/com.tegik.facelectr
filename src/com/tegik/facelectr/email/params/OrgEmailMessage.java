package com.tegik.facelectr.email.params;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.ad_actionButton.Parametrizacion;
import com.tegik.facelectr.data.MensajeCorreo;
import com.tegik.facelectr.email.CustomizeMessages;

public class OrgEmailMessage implements CustomizeMessages{
  
  
  Invoice invoice;
  String msg;
  String asunto;
  
  public void initialize(Invoice invoice) throws Exception{
    creatingMessages(invoice);
  }
  
  
  public void creatingMessages(Invoice invoice ) throws Exception{
    // Obteniendo el mensaje de una configuracion
    if (invoice.getBusinessPartner().getFetMensajecorreo() != null) {

      String msj = invoice.getBusinessPartner().getFetMensajecorreo().getMensaje();
      Parametrizacion para = new Parametrizacion(invoice);
      this.msg = para.getMensajeParametros(msj);

      String asu = invoice.getBusinessPartner().getFetMensajecorreo().getAsunto();
      Parametrizacion paraAsu = new Parametrizacion(invoice);
      this.asunto = paraAsu.getMensajeParametros(asu);

    } else if (!(invoice.getOrganization().getFetMensajeCorreoList().isEmpty())) {

      MensajeCorreo mensageCorreo = getMensajeCorreoDefault(invoice);

      if (mensageCorreo == null) {
        this.msg  = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
            + invoice.getDocumentNo() + ". Gracias.";
        this.asunto = "Factura " + invoice.getDocumentNo() + " correspondiente a su compra en "
            + invoice.getOrganization().getSocialName();

      } else {

        String msj = mensageCorreo.getMensaje();
        Parametrizacion para = new Parametrizacion(invoice);
        this.msg  = para.getMensajeParametros(msj);

        String asu = mensageCorreo.getAsunto();
        Parametrizacion paraAsu = new Parametrizacion(invoice);
        this.asunto = paraAsu.getMensajeParametros(asu);
      }

    } else {

      this.msg  = "Buen día. Le hacemos llegar sus archivos .xml y .pdf correspondientes a su factura #"
          + invoice.getDocumentNo() + ". Gracias.";
      this.asunto = "Factura " + invoice.getDocumentNo() + " correspondiente a su compra en "
          + invoice.getOrganization().getSocialName();

    }
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


  @Override
  public String getSubject() {
    // TODO Auto-generated method stub
    return this.msg;
  }


  @Override
  public String getMessage() {
    // TODO Auto-generated method stub
    return this.msg;
  }


  
  
}
