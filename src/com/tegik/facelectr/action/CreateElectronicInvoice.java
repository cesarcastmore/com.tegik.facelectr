package com.tegik.facelectr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.TimbreFiscalDigital;

import org.apache.log4j.Logger;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;
import org.openbravo.model.ad.utility.Attachment;

import com.tegik.contabilidad.medioselectronicos.mexico.utilidad.Utilidad;
import com.tegik.facelectr.utilidad.Convertidor;
import com.tegik.facelectr.utilidad.Deletrear;
import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Validate;
import com.tegik.facelectr.utilidad.CreateFiles;
import com.tegik.facelectr.attributes.AddAttributes;
import com.tegik.facelectr.attributes.ComprobanteModificado;
import com.tegik.facelectr.comprobante.GenerateCDFI;
import com.tegik.facelectr.comprobante.GenerateFileXML;
import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.email.SendingEmail;
import com.tegik.facelectr.hearthbeat.ServicioTimbrado;
import com.tegik.facelectr.servicios.soap.ConverterJAXB;
import com.tegik.facelectr.utilidad.Util;


public class CreateElectronicInvoice extends DalBaseProcess {
  private static final Logger log = Logger.getLogger(CreateElectronicInvoice.class);

  public void doExecute(ProcessBundle bundle) throws Exception {
  
    OBError msg = new OBError();
    msg.setType("Success");
    msg.setMessage("@FET_SuccessFacturaAdj@");
    msg.setTitle("Factura Electronica Exitosa");
    
    
    try{
      
      OBContext.setAdminMode(true);
  
      //Get Invoice 
      String invoiceID = (String) bundle.getParams().get("C_Invoice_ID");    
      Invoice invoice = OBDal.getInstance().get(Invoice.class, invoiceID);
        
      //Obtener la informacion del timbrado
      Organization orgPadre = invoice.getFetOrglegal();
      
      InforTimbrado infoTimbrado = orgPadre.getFetInfotimbrado();
      if(infoTimbrado == null){
        infoTimbrado = Util.crearTimbradoVersionViejita(invoice);
        if(infoTimbrado == null){
          throw new Exception("@FET_NoSeHaColocadoConfigInfoTimbrado@");
        }
      }
      
      
      //Crear el comprobante con un serivicio de timbrado
      GenerateFileXML generadorCFDI = loadGeneradorFileXML(infoTimbrado);
      File fileXML = generadorCFDI.createFileXML( invoice);

      Comprobante comprobante = (Comprobante) ConverterJAXB.converterToObject(Comprobante.class, fileXML);
      
      //Obtener el timbre para colocar informacion
      TimbreFiscalDigital timbreFiscal= null;
      for(Object obj : comprobante.getComplemento().getAny()){
        if(obj instanceof TimbreFiscalDigital){
          timbreFiscal=(TimbreFiscalDigital) obj;
          break;
          }
      }
      
      
      //Guarda informacion de la factura   
      invoice.setFetSellocfd(timbreFiscal.getSelloCFD());
      invoice.setFetCadenaoriginalSat(timbreFiscal.getVersion()
          + timbreFiscal.getUUID()
          + Convertidor.toString(timbreFiscal.getFechaTimbrado(), "yyyy-MM-dd'T'HH:mm:ss")
          + timbreFiscal.getSelloSAT()
          + timbreFiscal.getNoCertificadoSAT());
      invoice.setFetFechaTimbre(Convertidor.toString(timbreFiscal.getFechaTimbrado(), "yyyy-MM-dd'T'HH:mm:ss"));
      invoice.setFetCertificadoSat(timbreFiscal.getNoCertificadoSAT());
      invoice.setFetFoliofiscal(timbreFiscal.getUUID());
      invoice.setFetSellosat(timbreFiscal.getSelloSAT());
      
      
      //Status facturado
      invoice.setFetNumcertificado(comprobante.getNoCertificado());
      invoice.setFetDocstatus("Facturado");
        
      
      //Colocar el numero en letras
      double cantidadnumero = Double.parseDouble((invoice.getGrandTotalAmount()).toString());
      String nombreMoneda = invoice.getCurrency().getFetNombre()== null ? "PESOS" : invoice.getCurrency().getFetNombre() ;
      String isoMoneda = invoice.getCurrency().getFetIso()== null ? "M.N." : invoice.getCurrency().getFetIso();
      double cantnum = Math.abs(cantidadnumero);
      String cantidadletra = Deletrear.numeroALetra(cantnum, nombreMoneda, isoMoneda);
      if (cantidadnumero < 0) {
        cantidadletra = "MENOS " + cantidadletra;
        invoice.setFetCantidadenletras(cantidadletra);
      } else {
        invoice.setFetCantidadenletras(cantidadletra);
      }
          
      
      //Llamar para añadir atributos
      if(infoTimbrado.isModificar()){
        String claseAtributos=Finder.getJavaClass(infoTimbrado.getAgregado());
        if(claseAtributos != null){
          Validate.validateJavaAddAtributos(claseAtributos);
          
          ComprobanteModificado modificado = new ComprobanteModificado(fileXML);        
          
          AddAttributes addAtributos = (AddAttributes) Class.forName(claseAtributos).newInstance();
          addAtributos.addAdditionalAtributes(modificado, invoice);
          
          modificado.printFile(fileXML);
          }
      }
      
      
      //Guardar todos los cambios    
      OBDal.getInstance().save(invoice);
      OBDal.getInstance().flush();
      OBDal.getInstance().refresh(invoice);
      
      
       
      //Lllamar para guardar informacion del timbrado
      ServicioTimbrado.LlamarServicio(invoice);
      
      //Crear el pdf 
      if(infoTimbrado.isTimbrar()){
        
        Attachment attchPDF= CreateFiles.createAttamentPDF("318", invoice.getId(), invoice);        
        
      }
      
      List<File> archivos = Util.getFilesInvoice(invoice);
  
      //Para enviar el correo
      SendingEmail sentEmail = new SendingEmail(invoice, archivos);
      sentEmail.sentInvoice();
      
          
      //mensage final
      msg.setMessage("@FET_SuccessFacturaAdj@");
      msg.setTitle("Factura Electronica Exitosa");
      
      
      OBContext.restorePreviousMode(); 
    } catch(Exception e){
      
      msg.setType("Error");
      msg.setMessage(e.getMessage());
      msg.setTitle("Factura Electronica Error");
      
      OBDal.getInstance().rollbackAndClose();
      
    }
    
    
    bundle.setResult(msg);
       
    
  }
  
  
  public GenerateFileXML loadGeneradorFileXML(InforTimbrado infoTimbrado) throws Exception{
    
    //Crear el comprobante con un serivicio de timbrado
    GenerateFileXML generadorCFDI = new GenerateCDFI();
    
    //Si no es timbrar cambiar a servicio de generado de CDFI automatico, el XML no es necesario generarlo
    if(!infoTimbrado.isTimbrar()){
      
      String nameClassTimbrado=Finder.getJavaClass(infoTimbrado.getPactimbrado());
      Validate.validateJavaGenedoAutCFDI(nameClassTimbrado);
         
      generadorCFDI = (GenerateFileXML) Class.forName(nameClassTimbrado).newInstance();
    }
    
    
    return generadorCFDI;
    
  }
    
  

}
