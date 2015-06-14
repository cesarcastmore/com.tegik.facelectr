package com.tegik.facelectr.servicios.soap;


/*Este programa fue diseñado a la siguiente pagina 
 * http://www.herongyang.com/Web-Services/Java-net-HttpURLConnection-Send-SOAP-Message.html
 */

import java.io.IOException;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.tegik.facelectr.servicios.Connection;
import com.tegik.facelectr.servicios.ConnectionException;
import com.tegik.facelectr.servicios.Request;
import com.tegik.facelectr.servicios.Response;

public abstract class ServiceSOAPConnection {
  
  
  private static final Logger log = Logger.getLogger(ServiceSOAPConnection.class);
  
  
  private String response;
  
  
  public abstract ConfigurationServiceSOAP createConfigurationService() throws Exception;
     
  
  public String getResponse(){
    return this.response;
  }
  
  public PreparedDocument getResponsePreparedDocument(){
    try {
      return new PreparedDocument(response);
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  
  public Object getResponseTransformed(Class clase){
    SOAPMessage soapResponse;
    try {
      soapResponse = ConverterJAXB.ConverterToSOAPMessage(response);
      Document doc = soapResponse.getSOAPBody().extractContentAsDocument();
      return ConverterJAXB.converterToObject(clase , doc);
      
    } catch (SOAPException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }
  
  
  public void invokeSOAPBody(String xml) throws SOAPExceptionError, ConnectionException {
    String request;
    try {
      request = toSOAP(xml);
      invokeXMLSOAP(request);   
    } catch (SOAPException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (DOMException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
 

  }
  
  public void invokeTransformedObject(Object obj) throws ConnectionException, SOAPExceptionError {
    String request;
    try {
      request = toSOAP(obj);
      invokeXMLSOAP(request);
    } catch (SOAPException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (DOMException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 
    
  }
  
  public void invokePreparedDocument(PreparedDocument preparedRequest) throws ConnectionException, SOAPExceptionError{
    String request;
    try {
      log.info("request PreparedDocumento");
      request = toSOAP(preparedRequest);
      invokeXMLSOAP(request);
    } catch (SOAPException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (DOMException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 

    
    
  }
  
  
  public void invokeXMLSOAP(String xmlSoap) throws SOAPException, IOException, DOMException, SOAPExceptionError, 
  ConnectionException{
    
    log.info("request String  XMLSOAP");   
 
    response = call(xmlSoap);
    
    SOAPMessage soapResponse = ConverterJAXB.ConverterToSOAPMessage(xmlSoap);
    
    //Busca el error en el codigo
    SOAPFault fault = soapResponse.getSOAPBody().getFault();
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
  
  
  
  
  private String call(String xml) throws ConnectionException, IOException{
    
    log.info("ENTRO AL METODO PRIVADO DONDE HACE EL ENVIO");
    Request request= new Request();
    
    ConfigurationServiceSOAP config=null;
    try {
      config = createConfigurationService();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }    
       
    request.setBody(xml);
    request.setContentType("text/xml; charset=utf-8");
    request.setMethod(Request.POST);
    request.setUrl(config.getUrl());
    request.setSoapAction(config.getSoapAction());
    
    Connection conn= Connection.getInstance();
    conn.setConnectTimeout(config.getConnectionTimeOut());
    conn.setReadTimeout(config.getReadTimeOut());
    conn.setSslSocket(config.getSslSocket());
    conn.setHostname(config.getHostname());

    
    
    HashMap<String, String> properties = config.getProperties();
    for (String key : properties.keySet()) {
      log.info("Colocando las propiedades");
      System.setProperty((String) key, (String) properties.get(key));
    }
    
    
    
    log.info("ESTA ENVIANDO EL REQUEST");
    //Revisa si es con segurido o sin seguridad
    Response response;
    
    /*if(request.getUrl().contains("https")){
      response = conn.sendSSL(request);  

    } else {*/
      response = conn.send(request);
    //}
    
    return response.getBody();
    
         
  }
  
  
  private String toSOAP(PreparedDocument preparedRequest) throws SOAPException, IOException{
    
    SOAPMessage soapRequest = ConverterJAXB.ConverterToSOAPMessage(preparedRequest.toString());
    return ConverterJAXB.converterToString(soapRequest);
    
  }
  
  private String toSOAP(String xml) throws SOAPException, ParserConfigurationException, SAXException, IOException{
    MessageFactory messageFactory = MessageFactory.newInstance();

    SOAPMessage soapRequest = messageFactory.createMessage();
    SOAPPart soapPart = soapRequest.getSOAPPart();
    SOAPEnvelope envelope = soapPart.getEnvelope();
    SOAPBody soapBody = envelope.getBody();
    soapBody.addDocument(ConverterJAXB.converterToDocument(xml));
    
    return ConverterJAXB.converterToString(soapRequest);

    
  }
  
  
  private String toSOAP(Object obj) throws SOAPException, ParserConfigurationException, JAXBException, IOException{
    MessageFactory messageFactory = MessageFactory.newInstance();

    SOAPMessage soapRequest = messageFactory.createMessage();
    SOAPPart soapPart = soapRequest.getSOAPPart();
    SOAPEnvelope envelope = soapPart.getEnvelope();
    SOAPBody soapBody = envelope.getBody();
    soapBody.addDocument(ConverterJAXB.converterToDocument(obj));
    
    return ConverterJAXB.converterToString(soapRequest);
    
  }
  
}
