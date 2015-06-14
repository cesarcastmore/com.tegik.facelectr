package com.tegik.facelectr.servicios.soap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;

import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPBody;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.log4j.Logger;

import org.jfree.util.Log;
import org.w3c.dom.Document;


public abstract class ServiceSOAPAxis {
  
  private static final Logger log = Logger.getLogger(ServiceSOAPAxis.class);

  
  public abstract ConfigurationServiceSOAP createConfigurationService() throws Exception;
  
  @SuppressWarnings("resource")
  public void call(File requestFile, File responseFile) throws Exception{
    Call call = null;
    
    
    try {
      
      FileInputStream in = new FileInputStream(requestFile.getPath());
      FileOutputStream out = null;
      
      ConfigurationServiceSOAP config = createConfigurationService();

      Service service = new Service(); 
      call = (Call) service.createCall();
      
      if(config.getSoapAction() != null){
        call.setUseSOAPAction(true);
        call.setSOAPActionURI(config.getSoapAction());
      }
      call.setReturnType(XMLType.XSD_ANY);
      
      call.setTimeout(config.getConnectionTimeOut());

      SOAPEnvelope soapEnvelope = new SOAPEnvelope(in);
      call.setTargetEndpointAddress(config.getUrl());   
      SOAPEnvelope response = call.invoke(soapEnvelope);
      
      out = new FileOutputStream(responseFile.getPath(), true); 
      
      out.write(response.toString().getBytes());
      out.flush();
      
      
      
    } catch (ServiceException e) {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    } catch (AxisFault fault) { 
      if (fault.detail instanceof ConnectException){
        log.info("ConnectException");

      } else if(fault.detail instanceof java.net.SocketTimeoutException){
        throw new Exception("@FET_TimeOutError@");
        
      } else if(fault.detail instanceof InterruptedIOException){
        log.info("InterruptedIOException");
        throw new Exception("@FET_InterruptedIOException@");

      } 
      
      throw new Exception(fault.getMessage());
    } catch (FileNotFoundException e) {
      throw new Exception("Unable to find the request/response file. Reason + " + e.getLocalizedMessage());
    } catch (IOException e) {

      throw new Exception("Unable to open/write request or response from/to the file. Reason + "
          + e.getLocalizedMessage());
    } 

      catch (Exception e) {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    }
    
    
  
  }
  
  
  public String callxml(String body) throws Exception{
   
    Document doc = ConverterJAXB.converterToDocument(body);
    
  Call call = null;
    
    
    try {
      

      
      ConfigurationServiceSOAP config = createConfigurationService();

      Service service = new Service(); 
      call = (Call) service.createCall();
      
      if(config.getSoapAction() != null){
        call.setUseSOAPAction(true);
        call.setSOAPActionURI(config.getSoapAction());
      }
      call.setReturnType(XMLType.XSD_ANY);
      
      call.setTimeout(config.getConnectionTimeOut());

      SOAPEnvelope soapEnvelope = new SOAPEnvelope(); 
      SOAPBody soapBody = soapEnvelope.getBody();
      
      soapBody.addDocument(doc);        
          
      call.setTargetEndpointAddress(config.getUrl());   
      SOAPEnvelope response = call.invoke(soapEnvelope);
      
      
     return  response.getBody().toString();

      
      
    } catch (AxisFault fault) {   
      if (fault.detail instanceof ConnectException){
        log.info("ConnectException");

      } else if(fault.detail instanceof java.net.SocketTimeoutException){
        log.info("SocketTimeoutException");
        throw new Exception("@FET_TimeOutError@");

      }else if(fault.detail instanceof InterruptedIOException){
        log.info("InterruptedIOException");
        throw new Exception("@FET_InterruptedIOException@");


      } 
      
      throw new Exception(fault.getMessage());
    } catch (FileNotFoundException e) {
      throw new Exception("Unable to find the request/response file. Reason + " + e.getLocalizedMessage());
    } catch (IOException e) {
      throw new Exception("Unable to open/write request or response from/to the file. Reason + "
          + e.getLocalizedMessage());
    } 
      catch (Exception e) {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    }
    
    
  }
  
  public String callSoap(String xmlSoap) throws Exception{
    
    
  Call call = null;
    
    
    try {
      
      ByteArrayInputStream is = new ByteArrayInputStream(xmlSoap.getBytes());


      
      ConfigurationServiceSOAP config = createConfigurationService();

      Service service = new Service(); 
      call = (Call) service.createCall();
      
      if(config.getSoapAction() != null){
        call.setUseSOAPAction(true);
        call.setSOAPActionURI(config.getSoapAction());
      }
      call.setReturnType(XMLType.XSD_ANY);
      
      call.setTimeout(config.getConnectionTimeOut());

      SOAPEnvelope soapEnvelope = new SOAPEnvelope(is); 
    
          
      call.setTargetEndpointAddress(config.getUrl());   
      SOAPEnvelope response = call.invoke(soapEnvelope);
      
      
     return  response.toString();

      
      
    } catch (AxisFault fault) {     
      if (fault.detail instanceof ConnectException){
        log.info("ConnectException");

      } else if(fault.detail instanceof java.net.SocketTimeoutException){
        log.info("SocketTimeoutException");
        throw new Exception("@FET_TimeOutError@");

      }else if(fault.detail instanceof InterruptedIOException){
        log.info("InterruptedIOException");
        throw new Exception("@FET_InterruptedIOException@");


      }
      
      throw new Exception(fault.getMessage());
      } catch (FileNotFoundException e) {
        
      throw new Exception("Unable to find the request/response file. Reason + " + e.getLocalizedMessage());
      
    } catch (IOException e) {
      
      throw new Exception("Unable to open/write request or response from/to the file. Reason + "
          + e.getLocalizedMessage());
    } 
      catch (Exception e) {
        
      e.printStackTrace();
      throw new Exception(e.getMessage());
      
    }
    
    
  }


}
