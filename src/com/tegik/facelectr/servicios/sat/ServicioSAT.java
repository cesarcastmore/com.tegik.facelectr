package com.tegik.facelectr.servicios.sat;

import com.tegik.facelectr.servicios.soap.ConfigurationServiceSOAP;
import com.tegik.facelectr.servicios.soap.ServiceSOAPConnection;

public class ServicioSAT  extends ServiceSOAPConnection{

  @Override
  public ConfigurationServiceSOAP createConfigurationService() throws Exception {
    
    ConfigurationServiceSOAP config= new ConfigurationServiceSOAP("https://consultaqr.facturaelectronica.sat.gob.mx/ConsultaCFDIService.svc");
    config.setSoapAction("http://tempuri.org/IConsultaCFDIService/Consulta");
    config.setConnectionTimeOut(5000);
    config.setReadTimeOut(5000);
    
    // TODO Auto-generated method stub
    return config;
  }

}
