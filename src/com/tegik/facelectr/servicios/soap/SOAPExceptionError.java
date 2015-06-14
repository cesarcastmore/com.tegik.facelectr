package com.tegik.facelectr.servicios.soap;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

public class SOAPExceptionError extends Exception{
  public SOAPFault fault;

  
  public SOAPExceptionError(Class className, SOAPFault fault){
    super(fault.getDetail().getTextContent());
    this.fault= fault;

    
    
  }
  public SOAPExceptionError( SOAPFault fault){
    super(fault.getDetail().getTextContent());
    this.fault= fault;;

    
  }
  
  public SOAPExceptionError( SOAPFault fault, String nombre){
    super(nombre);
    this.fault= fault;;

    
  }
  
  
  public Object createObject(Class clase){
    try {
      
      
      String stringFault = ConverterJAXB.converterToString(fault);
      Object obj = ConverterJAXB.converterToObject(clase, stringFault);
      return obj;
      
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  
  public String getSOAPFaultToString(){
    String res="";
    try {
      res = ConverterJAXB.converterToString(this.fault);
    } catch (TransformerException  e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }catch ( TransformerFactoryConfigurationError e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return res;
  }

}
