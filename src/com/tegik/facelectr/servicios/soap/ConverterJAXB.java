package com.tegik.facelectr.servicios.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Source;
import javax.xml.soap.MimeHeaders;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ConverterJAXB {

  private static final Logger log = Logger.getLogger(ConverterJAXB.class);

  

  public static Object converterToObject(Class classJava, File file) throws JAXBException,
      FileNotFoundException {
    JAXBContext jcNameClass = JAXBContext.newInstance(classJava.getPackage().getName());
    Unmarshaller u = jcNameClass.createUnmarshaller();
    

    InputStream is = new FileInputStream(file);
    Object o = u.unmarshal(is);

    return o;
  }

 

  public static Document converterToDocument(Object obj) throws FileNotFoundException,
      ParserConfigurationException, JAXBException {


    JAXBContext jcObj = JAXBContext.newInstance(obj.getClass().getPackage().getName());
    Marshaller unmObj = jcObj.createMarshaller();
    unmObj.setProperty(unmObj.JAXB_FORMATTED_OUTPUT, true);
    unmObj.setProperty(unmObj.JAXB_FRAGMENT, true);
    

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.newDocument();

    unmObj.marshal(obj, doc);

    return doc;

  }

  public static Object converterToObject(Class className, Document doc) throws JAXBException, TransformerException, ParserConfigurationException, SAXException, IOException {

    
   String text = converterToString(doc);
   Document document = converterToDocument(text);
 
    
    
    JAXBContext jc = JAXBContext.newInstance(className.getPackage().getName());
    Unmarshaller u = jc.createUnmarshaller();

    Object o = u.unmarshal(document);

    return o;

  }

  public static Document converterToDocument(String xml) throws ParserConfigurationException,
      SAXException, IOException {
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(xml));

    Document doc = db.parse(is);

    return doc;

  }

  public static String converterToString(SOAPMessage msg) throws SOAPException, IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    msg.writeTo(out);
    String strMsg = new String(out.toByteArray());

    return strMsg;

  }
  
  
  public static Document converterToDocument(SOAPMessage msg) throws SOAPException, TransformerException {
  
    Source src = msg.getSOAPPart().getContent();  
    TransformerFactory tf = TransformerFactory.newInstance();  
    Transformer transformer = tf.newTransformer();  
    DOMResult result = new DOMResult();  
    transformer.transform(src, result);  
    return (Document)result.getNode();  
    
    
    

  }

  public static String converterToString(SOAPBody body) throws TransformerConfigurationException,
      TransformerException, TransformerFactoryConfigurationError {

    DOMSource source = new DOMSource(body);
    StringWriter stringResult = new StringWriter();
    TransformerFactory.newInstance().newTransformer()
        .transform(source, new StreamResult(stringResult));
    String message = stringResult.toString();

    return message;

  }
  
  public static String converterToString(SOAPFault fault) throws TransformerConfigurationException,
  TransformerException, TransformerFactoryConfigurationError {
    
    DOMSource source = new DOMSource(fault);
    StringWriter stringResult = new StringWriter();
    TransformerFactory.newInstance().newTransformer()
    .transform(source, new StreamResult(stringResult));
    String message = stringResult.toString();
    
    return message;

}
  
  public static String converterToString(File file) throws FileNotFoundException  {
    
    String text = new Scanner(file).useDelimiter("\\A").next();
    return text;

}

  public static Object converterToObject(Class classname, String xml)
      throws ParserConfigurationException, SAXException, IOException, JAXBException, TransformerException {
    Document doc = converterToDocument(xml);
    Object obj = converterToObject(classname, doc);
    return obj;

  }
  
  public static String converterToString(Document doc) throws TransformerException{
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(doc), new StreamResult(writer));
    String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
    return output;
  }
  
  
  private static HashMap<String, String> bringPrefix(Document  doc){
    
    HashMap<String, String> bindings = new HashMap<String, String>();
    DocumentTraversal dt = (DocumentTraversal) doc;
    NodeIterator i = dt.createNodeIterator(doc, NodeFilter.SHOW_ELEMENT,
            null, false);
    Element element = (Element) i.nextNode();
    while (element != null) {
        String prefix = element.getPrefix();
        if (prefix != null) {
            String uri = element.getNamespaceURI();
            if(!bindings.containsKey(prefix) && !prefix.equals("SOAP-ENV")){
              bindings.put(prefix, uri);
              System.out.println(prefix + "---" + uri);
            }
        }
        element = (Element) i.nextNode();
    }
    
    return bindings;
  } 
  
  
  public static SOAPMessage ConverterToSOAPMessage(String soapMessage) throws SOAPException, IOException{
    MessageFactory factory = MessageFactory.newInstance();
    SOAPMessage message = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soapMessage.getBytes(Charset.forName("UTF-8"))));
    return message;
    
  }


}
