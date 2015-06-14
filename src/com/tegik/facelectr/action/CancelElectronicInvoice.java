package com.tegik.facelectr.action;



import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;

import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Util;
import com.tegik.facelectr.utilidad.Validate;
import com.tegik.facelectr.comprobante.GenerateCDFI;
import com.tegik.facelectr.comprobante.GenerateFileXML;
import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.servicios.sat.LlamarServicioSAT;
import com.tegik.facelectr.servicios.soap.PreparedDocument;
import com.tegik.facelectr.timbrado.ServicioTimbradoPAC;


public class CancelElectronicInvoice extends DalBaseProcess {
  private static final Logger log = Logger.getLogger(CancelElectronicInvoice.class);

  public void doExecute(ProcessBundle bundle) throws Exception {
   
    OBError msg = new OBError();
    msg.setType("Success");
    

    try{
      //Get Invoice 
      String invoiceID = (String) bundle.getParams().get("C_Invoice_ID");
      
      
      String tableID = "318";
      Invoice invoice = OBDal.getInstance().get(Invoice.class, invoiceID);
      
      //Obtener la informacion del timbrado
      InforTimbrado infoTimbrado = invoice.getFetOrglegal().getFetInfotimbrado();
      
      if(infoTimbrado == null){
        throw new Exception("@FET_NoSeHaColodoInforacionTimbradoOrg@");
      }
  
      //Obtener todos los archivo adjutados
      HashMap<String, Attachment> files =Util. getAttachmentFileInfoTimbrado(infoTimbrado);
         
      //Validar el URL
      if(infoTimbrado.getUrlcancelacion() == null){
        throw new Exception("@FET_NoSeColocoURLServicioCancelacion@");
      }
      
      
      //Verifica UUID ante la SAT para cancelarlo
      PreparedDocument resultado = LlamarServicioSAT.validarUUID(invoice);
      if(resultado != null){
        String status = resultado.getTextContent("a:Estado");
        if(status.equals("No Encontrado")){
          String codigoStatus = resultado.getTextContent("a:CodigoEstatus");
          throw new Exception("@FET_ValidarAnteSAT@ " + codigoStatus+ " Estado: "+status);
          }
      }
      
      
      
      
      //Cancelar
      String nameClassTCancelacion=Finder.getJavaClass(infoTimbrado.getPactimbrado());
  
      if(infoTimbrado.isTimbrar()){
        
        Validate.validateJavaTimbrado(nameClassTCancelacion);
        
        //Preparando las variables
        ServicioTimbradoPAC cancelar = (ServicioTimbradoPAC) Class.forName(nameClassTCancelacion).newInstance();
        cancelar.setArchivoAdjuntadoOrgLegal(files);
        cancelar.setInfoTimbrado(infoTimbrado);
        cancelar.setInvoice(invoice);
        
        //Llamar el servicio
        cancelar.cancelarFactura();
      } else {
        
        Validate.validateJavaGenedoAutCFDI(nameClassTCancelacion);
        GenerateFileXML generadorCFDI =(GenerateFileXML) Class.forName(nameClassTCancelacion).newInstance();
        generadorCFDI.cancel(invoice);
  
        
      }
         
      
      //mensage final
      msg.setMessage("@FET_SuccessFacturaCancelacion@");
      msg.setTitle("Factura Electronica fue Exitosamente Cancelada");
    
    }catch (Exception e){
      
      msg.setMessage(e.getMessage());
      msg.setTitle("Factura Electronica Error");
      msg.setMessage(e.getMessage());
      
      OBDal.getInstance().rollbackAndClose();
    }
    
    
    bundle.setResult(msg);
    

  
  
    
    
    
  }

}
