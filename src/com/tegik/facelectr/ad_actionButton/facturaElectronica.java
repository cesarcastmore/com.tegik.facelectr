package com.tegik.facelectr.ad_actionButton;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mx.bigdata.sat.cfdi.CFDv32;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.ObjectFactory;
import mx.bigdata.sat.cfdi.v32.schema.TimbreFiscalDigital;
import mx.bigdata.sat.security.KeyLoader;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Expression;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.base.session.OBPropertiesProvider;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.database.ConnectionProvider;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.model.ad.datamodel.Table;
import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.geography.Location;
import org.openbravo.model.common.invoice.Invoice;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Complemento;

import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;
import org.openbravo.service.db.DalConnectionProvider;

import com.tegik.facelectr.hearthbeat.ServicioTimbrado;
import com.tegik.facelectr.utilidad.CreateFiles;
import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Util;

/**
 * Creando una factura electrónica para México.
 * 
 * @author Tegik
 */
public class facturaElectronica  extends DalBaseProcess {

  private static final Logger log = Logger.getLogger(facturaElectronica.class);
  
  
  private String rutaAttach = "";
  private String NumFac = "";
  private String ruta = "";

  private String strTimbrar = "";
  private String Statuscorreo = "OK";
  private String attachFolder = "";
  private String Separador = "/";
  private Boolean banderaSeguir = true;
  private String timbradoTestStatus = "NE";
  
  private Attachment archivoPDF=null;
  private Attachment archivoXML=null;
  


  // main HTTP call handler
  public OBError facturar(ProcessBundle bundle) throws Exception {
    
    String invoiceId = (String) bundle.getParams().get("C_Invoice_ID");  
    
    log.info( bundle.getParamsDeflated());


    if (1 ==1) {

      rutaAttach = "";
      NumFac = "";
      ruta = "";
      strTimbrar = "";
      Statuscorreo = "OK";
      Separador = System.getProperty("file.separator");
      banderaSeguir = true;
      timbradoTestStatus = "NE";
      // attachFolder = "/opt/OpenbravoERP-3.0/attachments";
      attachFolder = OBPropertiesProvider.getInstance().getOpenbravoProperties()
          .getProperty("attach.path");
      attachFolder = attachFolder + Separador.substring(0, 1);
      
      
      Invoice facturaTest = OBDal.getInstance().get(Invoice.class, invoiceId);
      Table table = OBDal.getInstance().get(Table.class, "318");

      

      OBError myMessage2 = new OBError();
      String rutaTimbradoTest = attachFolder + "318"+ Separador.substring(0, 1) + invoiceId + Separador.substring(0, 1)
          + "Timbrado" + facturaTest.getDocumentNo() + ".xml";
      String rutaRequestTest = attachFolder + "318"+ Separador.substring(0, 1) + invoiceId + Separador.substring(0, 1)
          + "requestTimbrado" + facturaTest.getDocumentNo() + ".xml";
      String rutaXMLTest = attachFolder + "318"+ Separador.substring(0, 1) + invoiceId + Separador.substring(0, 1)
          + facturaTest.getDocumentNo() + ".xml";

      File timbradoTest = new File(rutaTimbradoTest);
      File requestTest = new File(rutaRequestTest);
      File XMLTest = new File(rutaXMLTest);

      if (!timbradoTest.exists()) {
        banderaSeguir = true;
        timbradoTestStatus = "NE";
        if (requestTest.exists()) {
          if (requestTest.delete()) {
            Boolean bool_requestTest = requestTest.delete();
            log.info("RESULTADO DEL REQUESTTEST.DELETE" + bool_requestTest.toString());
          }

        }

        if (XMLTest.exists()) {
          if (XMLTest.delete()) {
            Boolean bool_XMLTest = XMLTest.delete();
            log.info("RESULTADO DEL XMLTest.DELETE" + bool_XMLTest.toString());
          }
        }
      } else {
        FileInputStream fis = new FileInputStream(timbradoTest);
        int b = fis.read();
        if (b == -1) {
          banderaSeguir = false;
          timbradoTestStatus = "AV";
        } else {
          banderaSeguir = false;
          timbradoTestStatus = "AT";
        }
        fis.close();
      }

      if (timbradoTestStatus == "AV") {
        String banderaTestStatus = "N";

        if (timbradoTest.exists()) {
          if (timbradoTest.delete()) {
            banderaTestStatus = "Y";
          } else {
            banderaTestStatus = "N";
          }
        } else {
          banderaTestStatus = "Y";
        }

        if (banderaTestStatus == "Y") {
          if (requestTest.exists()) {
            if (requestTest.delete()) {
              banderaTestStatus = "Y";
            } else {
              banderaTestStatus = "N";
            }
          } else {
            banderaTestStatus = "Y";
          }
        }

        if (banderaTestStatus == "Y") {
          if (XMLTest.exists()) {
            if (XMLTest.delete()) {
              banderaTestStatus = "Y";
            } else {
              banderaTestStatus = "N";
            }
          } else {
            banderaTestStatus = "Y";
          }
        }

        if (banderaTestStatus == "Y") {
          banderaSeguir = true;
          
        } else {
          myMessage2.setMessage("El archivo de timbrado se encuentra vacío. Contacte a soporte");
          myMessage2.setType("Error");
          myMessage2.setTitle("Error al leer archivo de timbrado");
          banderaSeguir = false;
          return myMessage2;
        }
      }

      if (timbradoTestStatus == "AT") {
        facturaTest.setFetSellosat(getValuefromXML(timbradoTest, "selloSAT"));
        facturaTest.setFetSellocfd(getValuefromXML(timbradoTest, "selloCFD"));
        facturaTest.setFetCadenaoriginalSat(getValuefromXML(timbradoTest, "version")
            + getValuefromXML(timbradoTest, "UUID")
            + getValuefromXML(timbradoTest, "FechaTimbrado")
            + getValuefromXML(timbradoTest, "selloSAT")
            + getValuefromXML(timbradoTest, "noCertificadoSAT"));
        facturaTest.setFetFechaTimbre(getValuefromXML(timbradoTest, "FechaTimbrado"));
        facturaTest.setFetCertificadoSat(getValuefromXML(timbradoTest, "noCertificadoSAT"));
        facturaTest.setFetFoliofiscal(getValuefromXML(timbradoTest, "UUID"));

        String rutaComprobanteTest = attachFolder + "318"+ Separador.substring(0, 1) + invoiceId
            + Separador.substring(0, 1) + facturaTest.getDocumentNo() + ".xml";
        FileInputStream fisComprobante = new FileInputStream(new File(rutaComprobanteTest));
        try {
          Comprobante comprobanteTest = CFDv32.newComprobante(fisComprobante);
          facturaTest.setFetNumcertificado(comprobanteTest.getNoCertificado());

        } catch (Exception e) {
          myMessage2
              .setMessage("No se pudo obtener la información del comprobante. Contacte al equipo de soporte");
          myMessage2.setType("Error");
          myMessage2.setTitle("Error al leear archivo de comprobante");
          return myMessage2;
        }

        fisComprobante.close();

        myMessage2.setMessage("");
        myMessage2.setType("Success");
        myMessage2.setTitle("Se creó con éxito la factura electrónica");
        return myMessage2;
      }

      if (timbradoTestStatus == "NE") {
        // myMessage2.setMessage("Se creará la nueva factura electrónica");
        // myMessage2.setType("Success");
        // myMessage2.setTitle("Cambiar bool");
        facturaTest = null;
        banderaSeguir = true;
      }

      if (!banderaSeguir) {
        /*
         * vars.setMessage(strTab, myMessage2); printPageClosePopUp(response, vars, strWindowPath);
         */
        return myMessage2;

      }
      log.info("MENSAGE 150" + strTimbrar );
      // run the calculation
      if (banderaSeguir) {
        OBError myMessage = creaFacturaElectronica( invoiceId, table);
 
        if (strTimbrar.equals("OK") && myMessage.getType() == "Success") {
          // log.info("dopost -- 5");
          HashMap<String, Object> parameters = new HashMap<String, Object>();
          parameters.put("DOCUMENT_ID", invoiceId);
 
          if (strTimbrar == "OK") {
            // log.info("dopost -- 8");

            try {
              OBContext.setAdminMode(true);
              Invoice facturaParaCorreo = OBDal.getInstance().get(Invoice.class, invoiceId);
              OBDal.getInstance().refresh(facturaParaCorreo);

              //Crear el PDF
              archivoPDF= CreateFiles.createAttamentPDF(table.getId(), facturaParaCorreo.getId(), facturaParaCorreo);        
              

              
              enviadorCorreos enviador = new enviadorCorreos(); // clase existente en el package             
              String respuestaEnvio = enviador.solicitarEnvio(facturaParaCorreo, "Y", "Y", 
                  Util.getFile(archivoPDF), Util.getFile(archivoXML));
              
              
              if (respuestaEnvio.equals("OK")) {
                // se creo la factura y se envio correctamente al correo electronico.
                myMessage.setType("Success");
                myMessage.setTitle("Se ha creado existosamente la Factura Electrónica");
                myMessage.setMessage("@FET_SuccessFacturaAdj@");
                
                
                OBContext.restorePreviousMode();                

              } 
            } catch (Exception e) {
              StringWriter w = new StringWriter();
              e.printStackTrace(new PrintWriter(w));
              String errorfactura = w.toString();
              log.info("CSM>CORREOS -- " + errorfactura);
              myMessage.setType("Warning");
              OBContext.restorePreviousMode();
              myMessage.setTitle("Se ha creado existosamente la Factura Electrónica");
              myMessage.setMessage(e.getMessage());
            }

          } else {

            myMessage
                .setMessage("Error en el timbrado. No se ha creado la Factura Electrónica: No se pudo enviar el correo el electrónico");
            myMessage.setType("Error");

            myMessage.setTitle("Titulo");
          }

        }

        return myMessage;

      }

    } 

    OBError messageNull = new OBError();
    return messageNull;
  }

  public OBError creaFacturaElectronica(String strInvoiceId, Table table) throws IOException, ServletException {
    OBError myMessage = new OBError();
    
    try {

      // log.info("cfe -- 1");
      // Cargas la factura del DAL de Openbravo
      OBContext.setAdminMode(true);
      Invoice factura = OBDal.getInstance().get(Invoice.class, strInvoiceId);
      
      
      

      
      myMessage.setMessage("Mi mensaje de prueba");
      myMessage.setType("Error");
      myMessage.setTitle("Error en la creación de la factura electrónica");
      // log.info("cfe -- 2");
      String tipoDoc = "";
      String nombreCer = "", nombreKey = "", nombrePfx = ""; // Inicializacion archivo .cer .key
                                                             // .pfx
      String pathCer = "", pathKey = "", pathPfx = ""; // Inicializacion archivo .cer .key .pfx
      String archivoPac = "";
      
            
      String orgRoot=Finder.findṔadre(factura.getOrganization()); 
      Organization orgPadre = OBDal.getInstance().get(Organization.class, orgRoot);      
            
      String PasswordFiel = orgPadre.getFetPassfiel(); // password de fiel
      String PasswordPAC = orgPadre.getFetPasspac(); // password de pac
      File archivo = new File(attachFolder + table.getId() + Separador.substring(0, 1)+ strInvoiceId
          + Separador.substring(0, 1) + factura.getDocumentNo() + ".xml"); // Liga del archivo en
                                                                           // tipo File
 
      File path = new File(attachFolder + table.getId() + Separador.substring(0, 1)+ strInvoiceId
          + Separador.substring(0, 1)); // Ruta de la factura en tipo File

      boolean exists = path.exists();
      if (!exists) {
        // Si no existe, creamos el directorio.
        path.mkdirs();
      }


      ruta = attachFolder + table.getId() + Separador.substring(0, 1)+ strInvoiceId
          + Separador.substring(0, 1); // Ruta de la factura en tipo String
      NumFac = factura.getDocumentNo();
      String Documento = factura.getTransactionDocument().getDocumentCategory(); // Tipo de
                                                                                 // documento de
                                                                                 // openbravo
      rutaAttach = table.getId() + Separador.substring(0, 1)+ strInvoiceId;
      // log.info("cfe -- 6");
      File archivoTimbrado = new File(attachFolder + table.getId() + Separador.substring(0, 1)
          + strInvoiceId + Separador.substring(0, 1) + "Timbrado" + factura.getDocumentNo()
          + ".xml"); // Liga del archivo en tipo File

      Statuscorreo = "OK";
      if (Statuscorreo == "OK") {
        Date Fecha_factura = factura.getInvoiceDate();
        int Ano_factura = Fecha_factura.getYear() + 1900;
        Date Fecha_Creacion = factura.getCreationDate();
        int Ano_creacion = Fecha_Creacion.getYear() + 1900;

        if (Ano_factura == Ano_creacion) {

          String RFC_Emisor = orgPadre.getOrganizationInformationList().get(0)
              .getTaxID();
          String RFC_Receptor = factura.getBusinessPartner().getTaxID();

          final OBCriteria<Attachment> attachmentList = OBDal.getInstance().createCriteria(
              Attachment.class);
          attachmentList.add(Expression.eq(Attachment.PROPERTY_TABLE,
              OBDal.getInstance().get(Table.class, "155")));
          attachmentList.add(Expression.eq(Attachment.PROPERTY_RECORD, orgPadre.getId()));

          for (Attachment attachmentUd : attachmentList.list()) {
            // log.info("CSM > Entro a la lista -- ");
            if (attachmentUd.getName().indexOf(".cer") != -1) {
              nombreCer = attachmentUd.getName();
              pathCer = attachmentUd.getPath();
              if (pathCer == "" || pathCer == null) {
                pathCer = "155" + "-" + orgPadre.getId();
              }
            }

            if (attachmentUd.getName().indexOf(".key") != -1) {
              nombreKey = attachmentUd.getName();
              pathKey = attachmentUd.getPath();
              if (pathKey == "" || pathKey == null) {
                pathKey = "155" + "-" + orgPadre.getId();
              }
            }

            if (attachmentUd.getName().indexOf(".pfx") != -1) {
              nombrePfx = attachmentUd.getName();
              pathPfx = attachmentUd.getPath();
              if (pathPfx == "" || pathPfx == null) {
                pathPfx = "155" + "-" + orgPadre.getId();
              }
            }

          }


          // Busca el archivo .cer y .key

          File archivoCer = new File(attachFolder + pathCer + Separador.substring(0, 1) + nombreCer);

          if (!archivoCer.exists() || nombreCer == "") {
            throw new Exception("@FET_NoArchivoCER@");
          }


          File archivoKey = new File(attachFolder + pathKey + Separador.substring(0, 1) + nombreKey);          
          if (!archivoKey.exists() || nombreKey == "") {
            throw new Exception("@FET_NoArchivoKEY@");
          }

          // log.info("cfe -- 16");
          // Se cierra Busca el archivo .cer y .key

          // Busca el archivo .pfx para timbrar
          if (nombrePfx != "") {
            archivoPac = attachFolder + pathPfx + Separador.substring(0, 1) + nombrePfx;
          } else {
            throw new Exception("@FET_NoArchivoPFX@");
          }
          // Se cierra Busca el archivo .pfx para timbrar

          // log.info("cfe -- 17");

          // Verifica si es una factura o una nota de credito
          if (Documento.equals("ARC"))
            tipoDoc = "N";
          else
            tipoDoc = "F";
          // log.info("cfe -- 18");

          // log.info("CSM // tipoDoc // " + tipoDoc);

          // termina verifica si es una factura o una nota de credito
          creadorFacturas comprobante = new creadorFacturas(strInvoiceId, tipoDoc); // Crea la
                                                                                    // Cadena
                                                                                    // Original de
                                                                                    // la Factura
          Comprobante comp = null;
          if (comprobante.getHayError()) {
            log.info("Error en la creación del comprobante");
            myMessage.setMessage(comprobante.getMensajeError());
            myMessage.setType("Error");
            myMessage.setTitle("Error en la creación de la factura electrónica");
            return myMessage;
          } else {
            comp = comprobante.getComprobante();
          }

          log.info("FACTURAELECTRONICA.JAVA");

          CFDv32 cfd = null;

          String paqueteJavaAddenda = null;

          if (addendasInstaladas()) {
            log.info("El módulo de addendas está instfalado");

            Class claseAddenda = Class.forName("com.tegik.addenda.module.proc.manejadorAddenda");
            Constructor<Object> ctor = claseAddenda.getDeclaredConstructor(String.class,
                Boolean.class);
            Object instance = ctor.newInstance(strInvoiceId, false);
            paqueteJavaAddenda = (String) claseAddenda.getMethod("getPaqueteJavaAddenda").invoke(
                instance);

            log.info("paqueteJava Objeto ADDENDA " + paqueteJavaAddenda);

            if (paqueteJavaAddenda != null) {
              cfd = new CFDv32(comp, paqueteJavaAddenda);
            } else {
              cfd = new CFDv32(comp);
            }

          } else {
            log.info("El módulo de addendas NO está instalado");
            cfd = new CFDv32(comp);
          }

          log.info("CFD");

          // log.info("cfe -- 19");

          PrivateKey key = KeyLoader.loadPKCS8PrivateKey(new FileInputStream(archivoKey),
              PasswordFiel); // Carga la llave privada
          X509Certificate cert = KeyLoader.loadX509Certificate(new FileInputStream(archivoCer)); // Carga
                                                                                                 // el
                                                                                                 // certificado
          Comprobante sellado = cfd.sellarComprobante(key, cert); // Sella la cadena original

          cfd.validar(); // Valida la factura
          cfd.verificar(); // verifica la factura
          cfd.guardar(System.out); // guarda la factura

          factura.setFetCadenaoriginal(cfd.getCadenaOriginal()); // Pone la cadena original en el
                                                                 // campo descripción de la factura.
                  
          
          cfd.guardar(new FileOutputStream(archivo)); // Guarda el archivo
          String codigo = convertXMLFileToString(ruta + NumFac + ".xml");
          
          String FacturaString = Base64Coder.encodeString(codigo);
          NumFac = cambiarCaracteres(NumFac);
          RFC_Emisor = cambiarCaracteres(RFC_Emisor);
          RFC_Receptor = cambiarCaracteres(RFC_Receptor);
          
           File archivoRequest = new File(attachFolder + table.getId() + Separador.substring(0, 1)
          + strInvoiceId + Separador.substring(0, 1) + "requestTimbrado" + factura.getDocumentNo()
          + ".xml");
          
          log.info("NOMBRE DEL ARCHIVO  " + archivoRequest.getName());
          System.setProperty("file.encoding", "UTF-8");
          PrintWriter encabezado = new PrintWriter(archivoRequest, "UTF-8");
          
          StringBuilder sb = new StringBuilder();
          sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
          sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tim=\"http://www.buzonfiscal.com/ns/xsd/bf/TimbradoCFD\" xmlns:req=\"http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI\"> \n");
          sb.append("   <soapenv:Header/>\n");
          sb.append("   <soapenv:Body>\n");
          sb.append("    <tim:RequestTimbradoCFD req:RefID=\"" + factura.getId() + "\"> \n");
          sb.append("       <req:Documento Archivo=\"" + FacturaString+ "\" NombreArchivo=\"" + NumFac + ".xml\" Tipo=\"XML\" Version=\"32\"/>  \n");
          sb.append("      <req:InfoBasica RfcEmisor=\"" + RFC_Emisor + "\" RfcReceptor=\""+ RFC_Receptor + "\"/> \n");
          sb.append("    </tim:RequestTimbradoCFD>\n");
          sb.append("   </soapenv:Body>\n");
          sb.append("</soapenv:Envelope>\n");
          encabezado.write(sb.toString());
          encabezado.flush();
          encabezado.close();
          
          
          String strSubirArchivo = subirArchivo(table, factura);
          log.info("CSM // factura.getClient().getFetUrltimbrado() // "
              + factura.getClient().getFetUrltimbrado());
          
          if(factura.getClient().getFetUrltimbrado() == null ){
            throw new Exception("@FET_NoEndURLServiceTimbrado@");
          } else if(PasswordPAC == null){
            throw new Exception("@FET_PassowrdPacNull@");
          }

          strTimbrar = timbrar(factura.getClient().getFetUrltimbrado(), ruta, NumFac, PasswordPAC,
              archivoPac);

          // strTimbrar = "OK";
          String mensaje = strSubirArchivo + " -- " + strTimbrar;
          // log.info("CSM // strSubirArchivo // " + strSubirArchivo);
          // log.info("cfe -- 26");
          if (strTimbrar == "OK") {

            // Se manda a llamar a la funcion extraeTimbrado para que extraiga del archivo Timbrado
            // los attributos
            // extraeTimbrado.timbra(ruta,NumFac);

            extraeTimbrado timbrado = new extraeTimbrado(new File(attachFolder + "318"+ Separador.substring(0, 1)
                + strInvoiceId +Separador.substring(0, 1)+ "Timbrado" + factura.getDocumentNo()
                + ".xml"));
            
            log.info("El lugar donde esta extraendo la factura es"+ attachFolder + "318"+ Separador.substring(0, 1)
                + strInvoiceId + Separador.substring(0, 1)+ "Timbrado" + factura.getDocumentNo()
                + ".xml");

            ObjectFactory of = new ObjectFactory();

            TimbreFiscalDigital timbre = of.createTimbreFiscalDigital();

            java.util.Date dateTimbrado = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .parse(timbrado.get_Fechatimbrado());
            
            timbre.setFechaTimbrado(dateTimbrado);
            timbre.setNoCertificadoSAT(timbrado.get_noCertificadoSAT());
            timbre.setSelloCFD(timbrado.get_selloCFD());
            timbre.setSelloSAT(timbrado.get_selloSAT());
            timbre.setUUID(timbrado.get_uuid());
            timbre.setVersion(timbrado.get_version());

            Complemento complemento = of.createComprobanteComplemento();
	      complemento.getAny().add(timbre);
	      //cfd.getComprobante().getComplementoGetAny().add(timbre);
	      sellado.setComplemento(complemento);
	      CFDv32 cfdTimbrado = null;
	      if  (paqueteJavaAddenda == null)
	      {
		    cfdTimbrado = new CFDv32(sellado);
	      }
	      else
	      {
		    cfdTimbrado = new CFDv32(sellado, paqueteJavaAddenda);
	      }
	      
	      //Comprobante sellado2 = cfdTimbrado.sellarComprobante(key, cert);
	      cfdTimbrado.validar();
	      cfdTimbrado.verificar();
	            cfdTimbrado.guardar(new FileOutputStream(new File(attachFolder + table.getId() + Separador.substring(0, 1)+ strInvoiceId + Separador.substring(0,1) + factura.getDocumentNo() + ".xml")));
	      
	      String facturaStringCarlos = convertXMLFileToString(ruta + NumFac + ".xml");
	      
	     	      
	      String v_headerXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	      String v_antesNamespace = facturaStringCarlos.substring(facturaStringCarlos.indexOf("<cfdi:Comprobante"), facturaStringCarlos.indexOf("<tfd:TimbreFiscalDigital") + "<tfd:TimbreFiscalDigital".length());
	      String v_namespace = " xmlns:tfd=\"http://www.sat.gob.mx/TimbreFiscalDigital\" xsi:schemaLocation=\"http://www.sat.gob.mx/TimbreFiscalDigital http://www.sat.gob.mx/TimbreFiscalDigital/TimbreFiscalDigital.xsd\" ";
	      
	      String v_fecha = "FechaTimbrado=\"" + timbrado.get_Fechatimbrado() + "\"";
	      String v_certificadosat = "noCertificadoSAT=\"" + timbrado.get_noCertificadoSAT() + "\"";
	      String v_sellocfd = "selloCFD=\"" + timbrado.get_selloCFD() + "\"";
	      String v_sellosat = "selloSAT=\"" + timbrado.get_selloSAT() + "\"";
	      String v_uuid = "UUID=\"" + timbrado.get_uuid() + "\"";
	      String v_version = "version=\"" + timbrado.get_version() + "\"";
	      
	      String v_timbrado = v_version + " " + v_uuid + " " + v_fecha + " " + v_sellocfd + " " + v_certificadosat + " " + v_sellosat + "/>";
	      log.info("v_timbrado: " + v_timbrado);
	      
	      String v_despuesNamespace = facturaStringCarlos.substring(facturaStringCarlos.indexOf("</cfdi:Complemento>"), facturaStringCarlos.length());
	      log.info("v_despuesNamespace: " + v_despuesNamespace);
	      
	      String xmlFinal = v_headerXML + v_antesNamespace + v_namespace + v_timbrado + "    " + v_despuesNamespace;
	      log.info("xmlFinal: " + xmlFinal);

            File renombrarXML = new File(ruta + NumFac + ".xml");
            File renombrarAXML = new File(ruta + NumFac + "-respaldo.xml");
            renombrarXML.renameTo(renombrarAXML);
            
            File borrarXML = new File(ruta + NumFac + ".xml");
            borrarXML.delete();

            OutputStreamWriter writerTimbrado = new OutputStreamWriter(new FileOutputStream(ruta
                + NumFac + ".xml", true), "UTF-8");
            BufferedWriter bufferTimbrado = new BufferedWriter(writerTimbrado);

            bufferTimbrado.write(xmlFinal);
            bufferTimbrado.close();

            // Se manda a llamar a la funcion extraeCER para que extraiga del archivo Original los
            // attributos
            extraeCER.extrae(ruta, NumFac);
            // log.info("cfe -- 27");
            String rutaArchivoTimbrado = attachFolder + "318"+ Separador.substring(0, 1) + strInvoiceId
                + Separador.substring(0, 1)+ "Timbrado" + factura.getDocumentNo() + ".xml";
            File archivoTimbradoNuevo = new File(rutaArchivoTimbrado);
            // Guarda el selloSAT en un Campo de Openbravo
            factura.setFetSellosat(getValuefromXML(archivoTimbradoNuevo, "selloSAT"));
            // Guarda el selloCFD en un Campo de Openbravo
            factura.setFetSellocfd(getValuefromXML(archivoTimbradoNuevo, "selloCFD"));
            // Guarda la cadena Original del SAT en un Campo de Openbravo
            factura.setFetCadenaoriginalSat(getValuefromXML(archivoTimbradoNuevo, "version")
                + getValuefromXML(archivoTimbradoNuevo, "UUID")
                + getValuefromXML(archivoTimbradoNuevo, "FechaTimbrado")
                + getValuefromXML(archivoTimbradoNuevo, "selloSAT")
                + getValuefromXML(archivoTimbradoNuevo, "noCertificadoSAT"));
            factura.setFetFechaTimbre(getValuefromXML(archivoTimbradoNuevo, "FechaTimbrado"));
            factura.setFetCertificadoSat(getValuefromXML(archivoTimbradoNuevo, "noCertificadoSAT"));
            factura.setFetFoliofiscal(getValuefromXML(archivoTimbradoNuevo, "UUID"));
            
            
            String rutaComprobanteNueva = attachFolder + "318"+ Separador.substring(0, 1) + strInvoiceId
                + Separador.substring(0, 1) + factura.getDocumentNo() + ".xml";
            FileInputStream fisComprobanteNuevo = new FileInputStream(
                new File(rutaComprobanteNueva));

            Comprobante nuevoComprobanteTest = CFDv32.newComprobante(fisComprobanteNuevo);
            factura.setFetNumcertificado(nuevoComprobanteTest.getNoCertificado());
  
            double cantidadnumero = Double.parseDouble((factura.getGrandTotalAmount()).toString());
            String nombreMoneda = factura.getCurrency().getFetNombre()== null ? "PESOS" : factura.getCurrency().getFetNombre() ;
            String isoMoneda = factura.getCurrency().getFetIso()== null ? "M.N." : factura.getCurrency().getFetIso();

            factura.setFetDocstatus("Facturado");
            Location locOrg = factura.getOrganization().getOrganizationInformationList().get(0).getLocationAddress();
            factura.setFetDirfiscal(locOrg);
            factura.setFetOrglegal(orgPadre);

            
            double cantnum = Math.abs(cantidadnumero);
            String cantidadletra = convertir.Numeroaletra(cantnum, nombreMoneda, isoMoneda);
            if (cantidadnumero < 0) {
              cantidadletra = "MENOS " + cantidadletra;
              factura.setFetCantidadenletras(cantidadletra);
            } else {
              factura.setFetCantidadenletras(cantidadletra);
            }
            
            
            OBDal.getInstance().save(factura);
            OBDal.getInstance().flush();
            OBDal.getInstance().refresh(factura);

            
            ServicioTimbrado.LlamarServicio(factura); 


          } else {
          }

          OBContext.restorePreviousMode();
          

          if (strSubirArchivo.equals("OK") && strTimbrar.equals("OK")) {

            myMessage.setMessage("Se ha creado existosamente la Factura Electrónica");
            myMessage.setType("Success");
            myMessage.setTitle("Titulo");
            return myMessage;
          } else {
            if (strSubirArchivo.equals("OK")){
              myMessage.setMessage("Error timbrado: " + strTimbrar);
              log.info("MENSAGE200" + strTimbrar );
            }
            else if (strTimbrar.equals("OK"))
              myMessage.setMessage("Error Subir archivo: " + strSubirArchivo);
            else
              myMessage.setMessage(mensaje);
            myMessage.setType("Error");
            myMessage.setTitle("Error en la creación de la factura electrónica: ");
            return myMessage;
          }
        }

        else {
          myMessage
              .setMessage("Error en la creación de la factura electrónica: La fecha de la factura no es válida");
          myMessage.setType("Error");
          myMessage.setTitle("Titulo");
          return myMessage;
        }

      } else {
        myMessage
            .setMessage("Error en la creación de la factura electrónica: El correo electronico no es válido");
        myMessage.setType("Error");
        myMessage.setTitle("Titulo");
        return myMessage;
      }

    } catch (javax.crypto.BadPaddingException e){
      myMessage.setType("Error");
      myMessage.setTitle("Error en la creación de la factura electrónica");
      myMessage.setMessage("@FET_PasswordFielIncorrecto@");
      return myMessage;
      
    }catch (InvalidKeySpecException e){
    
          
      myMessage.setType("Error");
      myMessage.setTitle("Error en la creación de la factura electrónica");
      myMessage.setMessage("@FET_PasswordFielIncorrecto@");
      return myMessage;
      
    } catch (Exception e) {
      
      StringWriter w = new StringWriter();
      e.printStackTrace(new PrintWriter(w));
      String errorfactura = w.toString();      
      
      myMessage.setType("Error");
      myMessage.setTitle("Error en la creación de la factura electrónica");
      
      if(e.getMessage() != null ){
        myMessage.setMessage(e.getMessage());
        log.info(errorfactura);
        return myMessage;          
        
      }else if(errorfactura != null) {
          myMessage.setMessage(errorfactura);
          return myMessage;
          
      } else {
        myMessage.setMessage("Null Pointer Exception");
        return myMessage;
        
      }     

    }
    



  }

  public String subirArchivo(Table table, Invoice factura) throws IOException, ServletException {
    try {
      OBContext.setAdminMode(true); // Para poder crear el Attachment

      Attachment archivoDAL = OBProvider.getInstance().get(Attachment.class);
      // Se agregan las propiedades del attachment
      archivoDAL.setClient(factura.getClient());
      archivoDAL.setOrganization(factura.getOrganization());
      archivoDAL.setActive(true);
      String path = table.getId() + Separador.substring(0, 1)+ factura.getId();
      archivoDAL.setPath(path);
      archivoDAL.setCreationDate(new Date());           
      archivoDAL.setUpdated(new Date());
      archivoDAL.setCreatedBy(factura.getCreatedBy());
      archivoDAL.setUpdatedBy(factura.getUpdatedBy());
      archivoDAL.setName(factura.getDocumentNo() + ".xml");
      // archivoDAL.setDataType("103");
      long secuencia = 10; // Falta saber como sacar la secuencia correcta, siempre se están
                           // subiendo con la secuencia 10.
      archivoDAL.setSequenceNumber(secuencia);
      archivoDAL.setText("Factura electrónica validada correctamente");
      archivoDAL.setTable(table);
      archivoDAL.setRecord(factura.getId());
      // Se guarda el attachment
      OBDal.getInstance().save(archivoDAL); // Guarda el attachment
      OBDal.getInstance().flush();
      
      archivoXML=archivoDAL;

      

      return "OK";
    } catch (Exception e) {
      StringWriter w = new StringWriter();
      e.printStackTrace(new PrintWriter(w));
      return w.toString();
    } finally {
      OBContext.restorePreviousMode();
    }
  }

  // Web Service Call -- Carlos Salinas
  public String timbrar(String endpointAddress, String ruta, String NumFac,
      String PasswordPAC, String archivoPac) throws Exception  {


      Client client = new Client();
      System.setProperty("javax.net.debug", "all");
      String retorno = client.call(endpointAddress, ruta, NumFac, PasswordPAC, archivoPac);
      return retorno;

  }

   public static String convertXMLFileToString(String fileName) {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      InputStream inputStream = new FileInputStream(new File(fileName));
      org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
      StringWriter stw = new StringWriter();
      Transformer serializer = TransformerFactory.newInstance().newTransformer();
      serializer.transform(new DOMSource(doc), new StreamResult(stw));
      return stw.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String cambiarCaracteres(String cadena) {
    cadena = cadena.replace("&", "&amp;");
    cadena = cadena.replace("<", "&lt;");
    cadena = cadena.replace(">", "&gt;");
    cadena = cadena.replace("\"", "&quot;");
    cadena = cadena.replace("'", "&#39;");
    return cadena;
  }

  public static String fileToString(String file) {
    String result = null;
    DataInputStream in = null;

    try {
      File f = new File(file);
      byte[] buffer = new byte[(int) f.length()];
      in = new DataInputStream(new FileInputStream(f));
      in.readFully(buffer);
      result = new String(buffer);
    } catch (IOException e) {
      throw new RuntimeException("IO problem in fileToString", e);
    } finally {
      try {
        in.close();
      } catch (IOException e) { /* ignore it */
      }
    }
    return result;
  }

  public static String getValuefromXML(File f, String str) {
    int ch;
    String xml1;
    StringBuffer strContent = new StringBuffer("");
    FileInputStream fin = null;
    try {
      fin = new FileInputStream(f);
      while ((ch = fin.read()) != -1)
        strContent.append((char) ch);
      fin.close();
    } catch (Exception e) {
      return "";
    }
    xml1 = strContent.toString();
    try {
      String[] ret = xml1.split(str + "=\"");
      if (ret.length >= 2)
        return ret[1].substring(0, ret[1].indexOf("\""));
      else
        return "";
    } catch (Exception e) {
      return "";
    }
  }

  public  Boolean addendasInstaladas() {
    try {
      Class.forName("com.tegik.addenda.module.proc.manejadorAddenda");
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  protected void doExecute(ProcessBundle bundle) throws Exception {
      
    OBError msg = facturar(bundle);        
    bundle.setResult(msg);
    
  }
  
  
  

  public OBError facturar(HttpServletRequest request, HttpServletResponse response, 
      ServletConfig srvConfig, VariablesSecureApp vars2, 
      String invoiceId, ProcessBundle bundle) {
    
    String processId = "51F2A08B30E79AD20130E79F4A6E0003";
    
    VariablesSecureApp vars = null;
    vars = new VariablesSecureApp(OBContext.getOBContext().getUser().getId(), OBContext
        .getOBContext().getCurrentClient().getId(), OBContext.getOBContext()
        .getCurrentOrganization().getId(), OBContext.getOBContext().getRole().getId(), OBContext
        .getOBContext().getLanguage().getLanguage());

    
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("C_Invoice_ID", invoiceId); 
    ProcessBundle pb=null;
    try{
      ConnectionProvider conn = new DalConnectionProvider(true);
      pb = new ProcessBundle(processId, vars).init(conn);
      pb.setParams(params);
      
      doExecute(pb);
    } catch(Exception e){
      
      OBError pbResult= new OBError();
      pbResult.setType("Error");
      pbResult.setMessage(e.getMessage());
      
    }
        
    
    OBError pbResult = (OBError) pb.getResult();
    
    return  pbResult;
    
    
  }




 



}
