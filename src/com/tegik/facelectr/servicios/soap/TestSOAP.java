package com.tegik.facelectr.servicios.soap;

import java.io.File;
import java.util.HashMap;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.openbravo.dal.service.OBDal;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;
import org.openbravo.model.ad.utility.Attachment;

import com.tegik.facelectr.ad_actionButton.CustomSSLSocketFactory;
import com.tegik.facelectr.data.ServiceSOAP;
import com.tegik.facelectr.utilidad.Util;
import com.tegik.facelectr.servicios.soap.ConfigurationServiceSOAP;
import com.tegik.facelectr.servicios.soap.ServiceSOAPJavax;



public class TestSOAP extends DalBaseProcess{
  
  ConfigurationServiceSOAP config;
  private static final Logger log = Logger.getLogger(TestSOAP.class);


  @Override
  protected void doExecute(ProcessBundle bundle) throws Exception {
    
    log.info("Entro aqui");
    log.info("Los Parametros son "+ bundle.getParamsDefalated());
    
    String servicioSoapID = (String) bundle.getParams().get("FET_Serviciosoap_ID"); 
    ServiceSOAP serviceSoap = OBDal.getInstance().get(ServiceSOAP.class, servicioSoapID);
    
    ConfigurationServiceSOAP config = new ConfigurationServiceSOAP(serviceSoap.getUrl());
    config.setSoapAction(serviceSoap.getSoapaction());

    if(serviceSoap.getPfx() != null){
      
      HashMap<String, Attachment> files = Util.findAttarchemntBySOAP(serviceSoap);
      if(files.containsKey(serviceSoap.getPfx())){

        File filePFX = Util.getFile(files.get(serviceSoap.getPfx()));
        init(filePFX, serviceSoap.getContrasenia());
        
      }
 
    }

    
    ServiceSOAPJavax service = ServiceSOAPJavax.getInstance();
    service.addConfiguratioService(config);
    service.addProperties();
    
    service.createSOAPRequest(serviceSoap.getRequest());
    
    try{
      
      service.invoke();
      
      serviceSoap.setResponse(service.converterResponseToString());
      OBDal.getInstance().save(serviceSoap);
      OBDal.getInstance().flush();

      
      
    }catch (SOAPExceptionError ex1){
      
      serviceSoap.setResponse(service.converterResponseToString());
      OBDal.getInstance().save(serviceSoap);
      OBDal.getInstance().flush();
           
    } catch (UnsupportedOperationException ex2){
      log.info(ex2.getMessage());
      serviceSoap.setExcepcion(ex2.getMessage());
      OBDal.getInstance().save(serviceSoap);
      OBDal.getInstance().flush();

      
    } catch (SOAPException ex3){
      log.info(ex3.getMessage());
      serviceSoap.setExcepcion(ex3.getMessage());
      OBDal.getInstance().save(serviceSoap);
      OBDal.getInstance().flush();

    }
    
    
    
    
  }

  
  public void init(File filePFX, String passPFX ){
    
    config.addProperty("VerifyHostName", "false");    
    
    //Obtenemos el archivo pfx
    config.addProperty(CustomSSLSocketFactory.KEY_STORE, filePFX.getPath());  
    config.addProperty(CustomSSLSocketFactory.KEY_STORE_PASSWORD, passPFX); 
    config.addProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
    config.addProperty(CustomSSLSocketFactory.KEY_STORE_TYPE, "PKCS12");
    config.addProperty(CustomSSLSocketFactory.KEY_MANAGER_TYPE, "SunX509");
    config.addProperty(CustomSSLSocketFactory.SECURITY_PROVIDER_CLASS,
        "com.sun.net.ssl.internal.ssl.Provider");
    config.addProperty(CustomSSLSocketFactory.SECURITY_PROTOCOL, "SSLv3");
    config.addProperty(CustomSSLSocketFactory.PROTOCOL_HANDLER_PACKAGES,
        "com.sun.net.ssl.internal.www.protocol");
    
  }

}
