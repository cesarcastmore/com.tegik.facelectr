package com.tegik.facelectr.hearthbeat;

import java.io.StringWriter;

import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.module.Module;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.servicios.Request;
import com.tegik.facelectr.servicios.Connection;
import com.tegik.facelectr.servicios.Response;



public class ServicioTimbrado {
  
  public static void LlamarServicio(Invoice invoice){
    final Logger log = Logger.getLogger(ServicioTimbrado.class);
    
    OBContext.setAdminMode(true);


    ObjectFactory factory = new ObjectFactory();
    
    
    SERTEGINFOTIMBRADO infoTimbrado = factory.createSERTEGINFOTIMBRADO();
    
    infoTimbrado.setFoliofiscal(invoice.getFetFoliofiscal());
    infoTimbrado.setRfcemisor(invoice.getFetOrglegal().getOrganizationInformationList().get(0).getTaxID());
    infoTimbrado.setRfcreceptor(invoice.getBusinessPartner().getTaxID());
    Module mod= OBDal.getInstance().get(Module.class, "51F2A08B30D2AC560130D2B967FB000B");
    
    String  urlString =invoice.getFetOrglegal().getClient().getFetUrltimbrado();
    if(urlString == null){
      urlString = invoice.getFetOrglegal().getFetInfotimbrado().getUrltimbrado();
    } 
    
    infoTimbrado.setUrltimbrado(urlString+ " -version "+ mod.getVersion());
    
    try {
      String info = toInfoTimbrado(infoTimbrado);
      
      Request request = new Request();
      request.setUsuario("Timbrado");
      request.setContrasenia("timbrado");
      request.setUrl("http://23.21.222.162/openbravo/ws/dal");
      request.setContentType("pplication/xml");
      request.setMethod(Request.POST);
      request.setBody(info);
      
      Connection conn = new Connection();
      conn.setConnectTimeout(2000);
      conn.setReadTimeout(2000);
      
      Response response = conn.send(request);
      
     
      
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
    
    
    
  }
  public static String toInfoTimbrado(SERTEGINFOTIMBRADO info) throws  Exception {
    
    JAXBContext jc = JAXBContext.newInstance("com.tegik.facelectr.hearthbeat");
    Marshaller marshaller = jc.createMarshaller();
    
    StringWriter stringWriter = new StringWriter();
    marshaller.marshal(info , stringWriter);
    
    String inicio="<ob:Openbravo xmlns:ob=\"http://www.openbravo.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String fin ="</ob:Openbravo>";
    String medio = stringWriter.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
    
    return inicio+ medio+fin;

}

}
