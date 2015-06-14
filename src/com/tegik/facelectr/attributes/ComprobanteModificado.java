package com.tegik.facelectr.attributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.tegik.facelectr.servicios.soap.*;

public class ComprobanteModificado {
  
  public Document documento;
  
  public ComprobanteModificado(File file) throws ParserConfigurationException, SAXException, IOException{
    
    DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
    DocumentBuilder bui = db.newDocumentBuilder();
    this.documento = bui.parse(file);  
  }
  
  
  public void addAtribute(String tag, String nombre, String value) throws Exception{
    Element element = (Element) this.documento.getElementsByTagName(tag).item(0); 
    if(element == null){
      throw new Exception ("@FET_TagNoExiste@");
      
    }
    
      element.setAttribute(nombre, value);
    
  }
  
  public String getAtribute(String tag, String nombre) throws Exception{
    Element element = (Element) this.documento.getElementsByTagName(tag).item(0); 
    if(element == null){
      throw new Exception ("El tag no existe "+ tag);
      
    }
    
      return element.getAttribute(nombre);
    
  }
  
  public void printFile(File file) throws TransformerException, FileNotFoundException{
    
      String xml = ConverterJAXB.converterToString(this.documento);
      PrintWriter out = new PrintWriter(file);
      out.print(xml);
      out.flush();
      out.close();
      
      
    
  }
  

}
