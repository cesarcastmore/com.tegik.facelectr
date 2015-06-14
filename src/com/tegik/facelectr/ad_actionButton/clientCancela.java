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

public class clientCancela {
  private static final Logger log = Logger.getLogger(clientCancela.class);

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
    call.setSOAPActionURI("http://www.buzonfiscal.com/CorporativoWS3.0/cancelaCFDi");

    // Setting Operation Name
    call.setOperationName(new javax.xml.namespace.QName(
        "http://www.buzonfiscal.com/CorporativoWS3.0/", "cancelaCFDi"));

    // Setting return type to ANY so that it accepts document type xml
    call.setReturnType(XMLType.XSD_ANY);

  }

  private boolean initialized = false;

  public  String call(String endpointAddress, String ruta, String NumFac,
      String PasswordPAC, String archivoPac) {
    String rutaResponse;

    FileInputStream in = null;
    FileOutputStream out = null;
    try {
      if (archivoPac != null) {
        log.info(archivoPac);
        System.setProperty(CustomSSLSocketFactory.KEY_STORE, archivoPac);
      }
      if (archivoPac != null) {
        log.info(PasswordPAC);
        System.setProperty(CustomSSLSocketFactory.KEY_STORE_PASSWORD, PasswordPAC);
      }

      System.setProperty(CustomSSLSocketFactory.KEY_STORE_TYPE, "PKCS12");
      System.setProperty(CustomSSLSocketFactory.KEY_MANAGER_TYPE, "SunX509");
      System.setProperty(CustomSSLSocketFactory.SECURITY_PROVIDER_CLASS,
          "com.sun.net.ssl.internal.ssl.Provider");
      System.setProperty(CustomSSLSocketFactory.SECURITY_PROTOCOL, "SSLv3");
      System.setProperty(CustomSSLSocketFactory.PROTOCOL_HANDLER_PACKAGES,
          "com.sun.net.ssl.internal.www.protocol");

      // Creating SOAP Envelop from the xml file
      in = new FileInputStream(ruta + "Request_Cancela" + NumFac + ".xml");

      if (!initialized) {
        initialize();
        initialized = true;
      }
      SOAPEnvelope soapEnvelope = new SOAPEnvelope(in);

      // Setting End point Address
      // call.setTargetEndpointAddress("https://demonegocios.buzonfiscal.com/bfcorpcfdiws");
      call.setTargetEndpointAddress(endpointAddress);

      // Calling the webservice with the defined soap envelope
      SOAPEnvelope response = call.invoke(soapEnvelope);

      out = new FileOutputStream(ruta + "ResponseCancela" + NumFac + ".xml", false);
      out.write(response.toString().getBytes());
      out.flush();
      rutaResponse = ruta + "ResponseCancela" + NumFac + ".xml";

    } catch (AxisFault fault) {
      
      
      if (fault.detail instanceof ConnectException
          || fault.detail instanceof InterruptedIOException
          || (fault.getFaultString().indexOf("Connection timed out") != -1)
          || fault.getFaultCode().getLocalPart().equals("HTTP")) {
      }
      return fault.getLocalizedMessage();
    } catch (FileNotFoundException e) {

      return "Unable to find the request/response file. Reason + " + e.getLocalizedMessage();
    } catch (SAXException e) {

      return "Unable to parse the request. Reason + " + e.getLocalizedMessage();
    } catch (IOException e) {
 
      return "Unable to open/write request or response from/to the file. Reason + "
          + e.getLocalizedMessage();
    } catch (ServiceException e) {

      return "Unable to initialize Call. Reason + " + e.getLocalizedMessage();
    }
    catch (Exception e) {
      log.info("Error en el proceso.");
      log.info(e.getLocalizedMessage());
      return "Unable to initialize Call. Reason + " + e.getLocalizedMessage();
    }
    finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace(); // To change body of catch statement use File | Settings | File
                               // Templates.
        }
      }
      if (out != null) {
        try {
          out.close();
;
        } catch (IOException e) {

          e.printStackTrace(); // To change body of catch statement use File | Settings | File
                               // Templates.
        }
      }
    }

    return rutaResponse;
  }

}
