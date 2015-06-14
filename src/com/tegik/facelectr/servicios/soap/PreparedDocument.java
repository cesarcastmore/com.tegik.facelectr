package com.tegik.facelectr.servicios.soap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PreparedDocument {
  
  public Document doc;
  
  public PreparedDocument(File file) throws ParserConfigurationException, SAXException, IOException{
    String text = new Scanner(file).useDelimiter("\\A").next();
    
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(text));
    doc = db.parse(is);
    
  }
  
 public PreparedDocument(String cadena) throws ParserConfigurationException, SAXException, IOException{
   
   DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
   InputSource is = new InputSource();
   is.setCharacterStream(new StringReader(cadena));
   doc = db.parse(is);
    
  }
 
 public void addAtribute(String tag, String nombre, String value) throws Exception{
   Element element = (Element) this.doc.getElementsByTagName(tag).item(0); 
   if(element == null){
     throw new Exception ("@FET_TagNoExiste@");
     
   }
   
     element.setAttribute(nombre, value);
   
 }
 
 public void addText(String tag, String texto) throws Exception{
   
   texto=texto.replace("'", "");
   
   
   Element tagElement = (Element) this.doc.getElementsByTagName(tag).item(0); 
   if(tagElement == null){
     throw new Exception ("@FET_TagNoExiste@");
     
   }
   
   tagElement.appendChild(doc.createTextNode(texto));
   
   
   
 }
 
 
 public String  getTextContent(String tag) throws Exception{
   
   
   
   Element tagElement = (Element) this.doc.getElementsByTagName(tag).item(0); 
   if(tagElement == null){
     throw new Exception ("@FET_TagNoExiste@");
     
   }
   
   return tagElement.getTextContent();
   
   
   
 }
 public String getAtribute(String tag, String nombre) throws Exception{
   Element element = (Element) this.doc.getElementsByTagName(tag).item(0); 
   if(element == null){
     throw new Exception ("El tag no existe "+ tag);
     
   }
   
     return element.getAttribute(nombre);
   
 }
 
 public String toString() {
   TransformerFactory tf = TransformerFactory.newInstance();
   Transformer transformer;
   String output="";
  try {
    transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(doc), new StreamResult(writer));
    output = writer.getBuffer().toString();
  } catch (TransformerConfigurationException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  } catch (TransformerException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
 
   return output;
   
 }
 
  
  

}
