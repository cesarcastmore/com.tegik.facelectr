package com.tegik.facelectr.ad_actionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Created by IntelliJ IDEA. User: hari Date: 10/16/12 Time: 3:11 PM To change this template use
 * File | Settings | File Templates.
 */
public class Client {
  private static final Logger log = Logger.getLogger(Client.class);

  
  static {
    System.setProperty("VerifyHostName", "false");
    // CustomSSLSocketFactory full class path should be provided.
    System.setProperty("axis.socketSecureFactory",
        "com.tegik.facelectr.ad_actionButton.CustomSSLSocketFactory");
    System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
  }

  Call call = null;

  public void initialize() throws ServiceException {
    Service service = new Service(); // initializing a dummy service
    call = (Call) service.createCall(); // initializing a call
    
    

    // Setting Soap Action property
    call.setUseSOAPAction(true);
    call.setSOAPActionURI("http://www.buzonfiscal.com/TimbradoCFDI/timbradoCFD");

    // Setting Operation Name
    call.setOperationName(new javax.xml.namespace.QName("http://www.buzonfiscal.com/TimbradoCFDI/",
        "timbradoCFD"));

    // Setting return type to ANY so that it accepts document type xml
    call.setReturnType(XMLType.XSD_ANY);

  }

  private boolean initialized = false;

  public  String call(String endpointAddress, String ruta, String NumFac,
      String PasswordPAC, String archivoPac) throws Exception{

    FileInputStream in = null;
    FileOutputStream out = null;
    try {
      if (archivoPac != null) {
        System.setProperty(CustomSSLSocketFactory.KEY_STORE, archivoPac);
        // System.setProperty(CustomSSLSocketFactory.TRUST_STORE,archivoPac);
      }
      if (archivoPac != null) {
        System.setProperty(CustomSSLSocketFactory.KEY_STORE_PASSWORD, PasswordPAC);
        // System.setProperty(CustomSSLSocketFactory.TRUST_STORE_PASSWORD,PasswordPAC);
      }

      // System.setProperty(CustomSSLSocketFactory.TRUST_STORE_TYPE,"PKCS12");
      // System.setProperty(CustomSSLSocketFactory.TRUST_MANAGER_TYPE,"SunX509");
      System.setProperty(CustomSSLSocketFactory.KEY_STORE_TYPE, "PKCS12");
      System.setProperty(CustomSSLSocketFactory.KEY_MANAGER_TYPE, "SunX509");

      System.setProperty(CustomSSLSocketFactory.SECURITY_PROVIDER_CLASS,
          "com.sun.net.ssl.internal.ssl.Provider");
      System.setProperty(CustomSSLSocketFactory.SECURITY_PROTOCOL, "SSLv3");
      System.setProperty(CustomSSLSocketFactory.PROTOCOL_HANDLER_PACKAGES,
          "com.sun.net.ssl.internal.www.protocol");

      // Creating SOAP Envelop from the xml file

      // in = new BufferedReader(new FileReader(ruta + "requestTimbrado" + NumFac + ".xml"));
      in = new FileInputStream(ruta + "requestTimbrado" + NumFac + ".xml");

      if (!initialized) {
        initialize();
        initialized = true;
      }

      SOAPEnvelope soapEnvelope = new SOAPEnvelope(in);
      soapEnvelope.setEncodingStyle("UTF-8");
      // SOAPEnvelope soapEnvelope = new SOAPEnvelope(new
      // ByteArrayInputStream(buf.toString().trim().getBytes()));

      // Setting End point Address
      call.setTargetEndpointAddress(endpointAddress);
      call.setEncodingStyle("UTF-8");      

      // Calling the webservice with the defined soap envelope
      SOAPEnvelope response = call.invoke(soapEnvelope);

      out = new FileOutputStream(ruta + "Timbrado" + NumFac + ".xml", true); // Se crea el archivo
                                                                             // donde se guardará la
                                                                             // informacion que se
                                                                             // reciba del PAC

      out.write(response.toString().getBytes());
      out.flush();

    } catch (AxisFault fault) {
      if (fault.detail instanceof ConnectException
          || fault.detail instanceof InterruptedIOException
          || (fault.getFaultString().indexOf("Connection timed out") != -1)
          || fault.getFaultCode().getLocalPart().equals("HTTP")) {
        log.info("TIMBRE ex1");

        throw new Exception("Unable to reach the end point.");


      }
      log.info("TIMBRE ex0.1");
      
      //La hora de futuro
      //
      String message = fault.getLocalizedMessage();
      
      if(message.contains(" failed to decrypt safe contents entry:")){
          throw new Exception("Invalido Password PAC");
         
      }else  {
      throw new Exception(message);
       } 


    } catch (FileNotFoundException e) {
      log.info("TIMBRE ex2");

      throw new Exception("Unable to find the request/response file. Reason + " + e.getLocalizedMessage());
    }
        catch (SAXException e) {
      log.info("TIMBRE ex3");

      throw new Exception("Unable to parse the request. Reason + " + e.getLocalizedMessage());
    } catch (IOException e) {
      log.info("TIMBRE ex4");

      throw new Exception("Unable to open/write request or response from/to the file. Reason + "
          + e.getLocalizedMessage());
    } catch (ServiceException e) {
      log.info("TIMBRE ex5");

      throw new Exception("Unable to initialize Call. Reason + " + e.getLocalizedMessage());
    } 
    finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          log.info("TIMBRE ex6");

          e.printStackTrace(); // To change body of catch statement use File | Settings | File
                               // Templates.
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          log.info("TIMBRE ex7");

          e.printStackTrace(); // To change body of catch statement use File | Settings | File
                               // Templates.
        }
      }
    }

    return "OK";
  }


}
