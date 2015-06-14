package com.tegik.facelectr.servicios.sat;



import org.apache.log4j.Logger;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.servicios.ConnectionException;
import com.tegik.facelectr.servicios.sat.request.Consulta;
import com.tegik.facelectr.servicios.sat.request.ExpresionImpresa;
import com.tegik.facelectr.servicios.soap.PreparedDocument;
import com.tegik.facelectr.servicios.soap.SOAPExceptionError;
import com.tegik.facelectr.servicios.soap.ServiceSOAPConnection;


public class LlamarServicioSAT {
  
  private static final Logger log = Logger.getLogger(LlamarServicioSAT.class);

  
  public static PreparedDocument validarUUID(Invoice  invoice){
    
    ServiceSOAPConnection service = new ServicioSAT();
    
    Consulta consulta= new Consulta();
    
    String rfcEmisor = invoice.getFetOrglegal().getOrganizationInformationList().get(0).getTaxID();
    String rfcReceptor = invoice.getBusinessPartner().getTaxID();
    String uuid = invoice.getFetFoliofiscal();
    
    ExpresionImpresa impresa = new ExpresionImpresa();
    impresa.setCantidad(invoice.getGrandTotalAmount());
    impresa.setRFCEmisor(rfcEmisor);
    impresa.setRFCReceptor(rfcReceptor);
    impresa.setIdentificador(uuid);
    
    
    consulta.setExpresionImpresa(impresa.toString()); 
    
    try {
      
      service.invokeTransformedObject(consulta);
      PreparedDocument response = service.getResponsePreparedDocument();
      
      
      
      return response;
      
    } catch (ConnectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    } catch (SOAPExceptionError e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    } 
    
    
  }

}
