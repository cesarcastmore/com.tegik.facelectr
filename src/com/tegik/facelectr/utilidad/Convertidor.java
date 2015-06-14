package com.tegik.facelectr.utilidad;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.TimbreFiscalDigital;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Complemento;

import org.apache.log4j.Logger;
import org.openbravo.base.session.OBPropertiesProvider;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class Convertidor {
  final static Logger log = Logger.getLogger(Convertidor.class);
  
  
  private static String converterToString(Document doc) throws TransformerException{
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(doc), new StreamResult(writer));
    String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
    return output;
  }
  
  
  public static Object toObject(Class classname, String xml)
      throws ParserConfigurationException, SAXException, IOException, JAXBException, TransformerException {
    Document doc = converterToDocument(xml);
    Object obj = converterToObject(classname, doc);
    return obj;

  }
  
  private static Object converterToObject(Class className, Document doc) throws JAXBException, TransformerException, ParserConfigurationException, SAXException, IOException {

    String text = converterToString(doc);
    Document document = converterToDocument(text);
     
    JAXBContext jc = JAXBContext.newInstance(className.getPackage().getName());
    Unmarshaller u = jc.createUnmarshaller();

    Object o = u.unmarshal(document);

    return o;

   }
  
  public static Object toObject(Class classJava, File file) throws JAXBException,
  FileNotFoundException {

    JAXBContext jcNameClass = JAXBContext.newInstance(classJava.getPackage().getName());
    Unmarshaller u = jcNameClass.createUnmarshaller();
    InputStream is = new FileInputStream(file);

    Object o = u.unmarshal(is);
    return o;
}



  private static Document converterToDocument(Object obj) throws FileNotFoundException,
  
  ParserConfigurationException, JAXBException {

    JAXBContext jcObj = JAXBContext.newInstance(obj.getClass().getPackage().getName());
    Marshaller unmObj = jcObj.createMarshaller();
    unmObj.setProperty(unmObj.JAXB_ENCODING, "UTF-8");
    unmObj.setProperty(unmObj.JAXB_FORMATTED_OUTPUT, true);
    unmObj.setProperty(unmObj.JAXB_FRAGMENT, true);


    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.newDocument();
    unmObj.marshal(obj, doc);

    return doc;


  }
 
  
  
  public static String toString(Object obj) throws  Exception {

    //Crear el marshaller
    log.info("La clase del object es " +obj.getClass().getPackage().getName());
    JAXBContext jcObj = JAXBContext.newInstance(obj.getClass().getPackage().getName()); 
    Marshaller unmObj = jcObj.createMarshaller();
    
    //sobrescribe en el archivo
    String attachFolder = OBPropertiesProvider.getInstance().getOpenbravoProperties()
        .getProperty("attach.path") + "/archivo.xml";
    
    OutputStream os = new FileOutputStream( attachFolder );
    unmObj.marshal( obj, os );
    return attachFolder;
   
}

  
  public static String toString(File file ) throws IOException {
    String content = "";
    try {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String str;
        while ((str = in.readLine()) != null) {
            content =content + str;
        }
        in.close();
    } catch (IOException e) {
      
    }
    return content;
  }

  
  public static byte[] toBytes(File file) throws Exception{ 
    FileInputStream fileInputStream=null;

    byte[] bFile = new byte[(int) file.length()];
    //convert file into array of bytes
    fileInputStream = new FileInputStream(file);
    fileInputStream.read(bFile);
    fileInputStream.close();
    
    return bFile;

 
  }

 
  
  
  
 public static Short toShort(String cadenaNumero) throws Exception {
    
    if(cadenaNumero == null)
      return null;
    
    try{
    short numero= Short.parseShort(cadenaNumero);
    return numero;
    }catch (NumberFormatException e){
      throw (Exception) e;
    }
  }
  
  
  public static Integer toInteger(String cadenaNumero)throws Exception{
    
    if(cadenaNumero == null)
      return null;
    
    try{
      
    int numero= Integer.parseInt(cadenaNumero);
    return numero;
    
    }catch (NumberFormatException e){
      throw (Exception) e;
    }
    
  }
  
  public static String toString(Date date, String formato){
    SimpleDateFormat fecha_formato = new SimpleDateFormat(formato);
    return fecha_formato.format(date);
    

    
  }
  
  public static Date toDate(String cadenaFecha, String formato) throws Exception {
    
    if(cadenaFecha == null){
      return null;
    }
    
   try{

    SimpleDateFormat fecha_formato = new SimpleDateFormat(formato);
    Date date = fecha_formato.parse(cadenaFecha);
    
    return date;
    } catch(ParseException e ){
       throw (Exception) e;
    }
  }
  
  public static XMLGregorianCalendar toXMLGregorianCalendar(String cadenaFecha, String formato) throws Exception{
    
    Date fecha = toDate(cadenaFecha, formato);
    if(fecha == null){
      return null;
    }
    
 
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(fecha);
    XMLGregorianCalendar xml = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    
    
    return xml;
    
  }
  
  
  public static XMLGregorianCalendar toXMLGregorianCalendar(Date fecha) throws Exception{
     
 
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(fecha);
    XMLGregorianCalendar xml = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    
    
    return xml;
    
  }
  
  
 public static Boolean toBoolean(String cadena) {
   
   if(cadena == null) return null;
    
    cadena= cadena.toUpperCase();
    
    if(cadena.equals("SI") && cadena.equals("S") && cadena.equals("YES")
        && cadena.equals("TRUE") && cadena.equals("Y")){
      return true;
    } else if(cadena.equals("NO") && cadena.equals("N") && cadena.equals("FALSE"))  
    return false;
    
    
    return false;
    
  }
  
 
 public static BigDecimal toBigDecimal(String cadenaNumero){
   
   if(cadenaNumero == null){
     return null;
   }
   
   return new BigDecimal(cadenaNumero);
 }
 
  
 public static Document toDocument(String xml) throws ParserConfigurationException, SAXException, IOException{
 
 
 DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
 InputSource is = new InputSource();
 is.setCharacterStream(new StringReader(xml));

 Document doc = db.parse(is);
 return doc;
 }
 
 
 
 
 
 public static String toString(Complemento comp) throws Exception{
   JAXBContext jc;
   try {
     
     jc = JAXBContext.newInstance( "mx.bigdata.sat.cfdi.v32.schema" );
     Marshaller m = jc.createMarshaller();
     StringWriter stringWriter = new StringWriter();  
     PrintWriter print = new PrintWriter(stringWriter);
     m.marshal( comp, print );
     print.flush();      
     
     return stringWriter.toString();
     
   } catch (JAXBException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     throw new Exception("No tiene el formato correcto\n" +e.getMessage()+"\n");
   }
   
   
 }
 
 
 public static void addText(File file, String xml) throws FileNotFoundException{
   PrintWriter out = new PrintWriter(file);
   out.print(xml);
   out.flush();
   out.close();
   
 }
 
 public static Comprobante toComprobante(String xml) throws Exception {
   try{
     JAXBContext jc = JAXBContext.newInstance( "mx.bigdata.sat.cfdi.v32.schema" );
     Unmarshaller u = jc.createUnmarshaller();
     InputStream inputStream =  new ByteArrayInputStream(xml.getBytes());
     Comprobante o = (Comprobante) u.unmarshal(new StreamSource(inputStream));    
     return o;
   } catch(Exception e){
     e.printStackTrace();
     StringWriter errors = new StringWriter();
     e.printStackTrace(new PrintWriter(errors));
     throw new Exception("El archivo tiene un formato incorrecto"); 
   }

}

 
 public static Comprobante toComprobanteErrorDetallado(String xml) throws Exception {
   try{
     JAXBContext jc = JAXBContext.newInstance( "mx.bigdata.sat.cfdi.v32.schema" );
     Unmarshaller u = jc.createUnmarshaller();
     InputStream inputStream =  new ByteArrayInputStream(xml.getBytes());
     Comprobante o = (Comprobante) u.unmarshal(new StreamSource(inputStream));    
     return o;
   } catch(Exception e){
     e.printStackTrace();
     StringWriter errors = new StringWriter();
     e.printStackTrace(new PrintWriter(errors));
     throw new Exception(errors.toString()); 
   }

}
 
 
 
 
 public static TimbreFiscalDigital toTimbreFiscalDigital(String xml) throws Exception {
   
   JAXBContext jc;
  try {
    
    jc = JAXBContext.newInstance( "mx.bigdata.sat.cfdi.v32.schema" );
    Unmarshaller u = jc.createUnmarshaller();
    InputStream inputStream =  new ByteArrayInputStream(xml.getBytes());
    TimbreFiscalDigital o = (TimbreFiscalDigital) u.unmarshal(new StreamSource(inputStream));    
    return o;
    
  } catch (JAXBException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    
    log.info(e.getMessage());

    throw e;
  }
  


}

 
 
 

  
}
