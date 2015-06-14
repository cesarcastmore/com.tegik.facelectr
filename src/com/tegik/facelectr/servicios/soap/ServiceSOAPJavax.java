package com.tegik.facelectr.servicios.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
//http://www.herongyang.com/Web-Services/Java-net-HttpURLConnection-Send-SOAP-Message.html


public class ServiceSOAPJavax {
  
  

  private static final Logger log = Logger.getLogger(ServiceSOAPJavax.class);
  
  private static ServiceSOAPJavax uniqueInstance; 
  public SOAPMessage soapRequest;
  public ConfigurationServiceSOAP conf;
  public SOAPMessage soapResponse;


  private ServiceSOAPJavax(){
    
  }
  
  public static synchronized ServiceSOAPJavax getInstance(){
    if(uniqueInstance ==  null){
      log.info("Inicializa la instancia");
      uniqueInstance = new ServiceSOAPJavax();
    }
    
    return uniqueInstance;
  }
  
  public void addConfiguratioService(ConfigurationServiceSOAP conf) {
    this.conf = conf;
  }

  
  public void addProperties(){
    HashMap<String, String> properties = this.conf.getProperties();
    for (String key : properties.keySet()) {
      System.setProperty((String) key, (String) properties.get(key));
    }
    
    
  }
  
  public SOAPMessage getSoapMessage() {
    return soapRequest;
  }
  
  public String getSoapMessagetoString() throws SOAPException, IOException {
    return ConverterJAXB.converterToString(soapRequest);
  }

  public ConfigurationServiceSOAP getConfiguration() {
    return conf;
  }


 
  
  
  public void createSOAPRequest(String soapMessage) throws SOAPException, IOException{


    this.soapRequest = ConverterJAXB.ConverterToSOAPMessage(soapMessage);
    
    //Coloca el SOAPAction
    if(conf.getSoapAction() != null){
      MimeHeaders headers = soapRequest.getMimeHeaders();
      headers.addHeader("SOAPAction",conf.getSoapAction());  
    }
          
    
  }
  
  public void createSOAPRequest(PreparedDocument preparedRequest) throws SOAPException, IOException{
    this.soapRequest = ConverterJAXB.ConverterToSOAPMessage(preparedRequest.toString());
    
    //Coloca el SOAPAction
    if(conf.getSoapAction() != null){
      MimeHeaders headers = soapRequest.getMimeHeaders();
      headers.addHeader("SOAPAction",conf.getSoapAction());  
    }
          
    
  }


  public void createSoapRequest(Object obj) throws SOAPException, ParserConfigurationException, JAXBException, TransformerException, IOException, SAXException {

    MessageFactory messageFactory = MessageFactory.newInstance();

    this.soapRequest = messageFactory.createMessage();
    SOAPPart soapPart = soapRequest.getSOAPPart();
    SOAPEnvelope envelope = soapPart.getEnvelope();
    SOAPBody soapBody = envelope.getBody();
    soapBody.addDocument(ConverterJAXB.converterToDocument(obj));
    
    //Coloca el SOAPAction
    if(conf.getSoapAction() != null){
      MimeHeaders headers = soapRequest.getMimeHeaders();
      headers.addHeader("SOAPAction",conf.getSoapAction());  
    }
    
 
    

  }
  
 public void invoke() throws UnsupportedOperationException, SOAPException, SOAPExceptionError, MalformedURLException{
    
    // Create SOAP Connection
    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
    
    URL endpoint =new URL(conf.getUrl());


    // Invocar
    this.soapResponse =  soapConnection.call(soapRequest, endpoint);
    
    //Busca el error en el codigo
    SOAPFault fault = this.soapResponse.getSOAPBody().getFault();
    if(fault != null){
      if(fault.getDetail()!= null){
        if(fault.getDetail().getTextContent() != null){    
          throw new SOAPExceptionError(fault, fault.getDetail().getTextContent());
          } 
        }else if(fault.getFaultString() != null){
          throw new SOAPExceptionError(fault, fault.getFaultString());
      } else if(fault.getFaultCode() != null){
        throw new SOAPExceptionError(fault, fault.getFaultCode());
      }

     throw new SOAPExceptionError(fault);    
    }    

  }
 

  public Object converterResponseTo(Class clase) throws javax.xml.soap.SOAPException, SOAPExceptionError, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, JAXBException, ParserConfigurationException, SAXException, IOException  {
    
    Document doc = soapResponse.getSOAPBody().extractContentAsDocument();
    return ConverterJAXB.converterToObject(clase , doc);

  }
  
  public String converterResponseToString() throws UnsupportedOperationException, SOAPException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, SOAPExceptionError{
    
    String responseString = ConverterJAXB.converterToString(this.soapResponse.getSOAPBody());
    return responseString;
  
  }
  
  public PreparedDocument getPreparedDocument() throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, SOAPException{
    String responseString = ConverterJAXB.converterToString(this.soapResponse.getSOAPBody());
    return new PreparedDocument(responseString);
  }

}
