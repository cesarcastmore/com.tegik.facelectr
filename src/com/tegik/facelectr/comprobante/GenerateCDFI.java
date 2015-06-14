package com.tegik.facelectr.comprobante;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import mx.bigdata.sat.cfdi.CFDv32;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.TimbreFiscalDigital;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Complemento;
import mx.bigdata.sat.security.KeyLoader;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.attributes.AddAttributes;
import com.tegik.facelectr.comprobante.updates.ControlUpdates;
import com.tegik.facelectr.comprobante.updates.MoverIEPSAConceptos;
import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.timbrado.ServicioTimbradoPAC;
import com.tegik.facelectr.utilidad.CreateFiles;
import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Util;
import com.tegik.facelectr.utilidad.Validate;

public class GenerateCDFI implements GenerateFileXML {

  

  private static final Logger log = Logger.getLogger(MoverIEPSAConceptos.class);

  
  @Override
  public File createFileXML(Invoice invoice) throws Exception {

    
    
    //Crear el archivo

    Attachment attachmentComprob = CreateFiles.createAttachment("318", invoice.getId(), invoice.getDocumentNo(), "xml");
    File fileXML=Util.getFile(attachmentComprob);
    
    //Obtener la informacion del timbrado
    Organization orgPadre = invoice.getFetOrglegal();
    InforTimbrado infoTimbrado =orgPadre.getFetInfotimbrado();
    
    
    //Obtener el password de CSD
    String passwordFiel = infoTimbrado.getContrasenia();
    if(passwordFiel == null){
      throw new Exception("@FET_NoHaSidoColocadoPassowrdCSD@");
    }  
    
    //Obtener todos los archivo adjutados
    
    HashMap<String, Attachment> files =Util. getAttachmentFileInfoTimbrado(infoTimbrado);
    
    //Verificar exitencia de los archivo importantes
    String emisorKey= infoTimbrado.getNombrekey();
    String emisorCer= infoTimbrado.getNombrecer();
    if(emisorKey == null ||  emisorCer ==null ){
      throw new Exception("@FET_NoExistenArchivosCERoKEY@"); 
    }
    Validate.validateFilesImportant(files, new String []{emisorKey,emisorCer});
    
    // Verifica si es una factura o una nota de credito
    String documento = invoice.getTransactionDocument().getDocumentCategory();
    String tipoDoc = documento.equals("ARC") ? "N" : "Y";
    
    //Creamos el comprobante y inicia la addenda
    CreateComprobante createComprobate = new CreateComprobanteDefecto();
    Comprobante comp =createComprobate.createComprobantePersonalizado(invoice);
    
    //Agregamos nuevos agregados extras para evitar moverle mucho a codigo principal 
    ControlUpdates updates= new ControlUpdates();
    updates.execute(comp, createComprobate.getComprobanteOpenbravo());
    

    
    
    //Ante de realizar el sellado damos la opcion de agregar unos campos opcionales si el usuario quiere
    if(infoTimbrado.isModificar()){      
      String claseAtributos=Finder.getJavaClass(infoTimbrado.getAgregado());
     
      if(claseAtributos != null){
        Validate.validateJavaAddAtributos(claseAtributos);     
  
        AddAttributes addAtributos = (AddAttributes) Class.forName(claseAtributos).newInstance();
        addAtributos.UpdateComprobante(comp,invoice);

      }

    }
    

    //Buscar el nombre de la adenda si no esta regresa null
    String namePackageAddenda = Util.getPackageObject(comp.getAddenda());


    //Inicializa el cfd
    CFDv32 cfd = null;
    if(namePackageAddenda != null){
      cfd = new CFDv32(comp, namePackageAddenda);      
    }else {
      cfd = new CFDv32(comp);
    }
    
    

    //Carga la llave
    Attachment attachmentKey = (Attachment) files.get(emisorKey);
    File fileKey = Util.getFile(attachmentKey);  
    PrivateKey key = KeyLoader.loadPKCS8PrivateKey(new FileInputStream(fileKey), passwordFiel);
    
    //carga el certificado y lo sella

    Attachment attachmentCer = (Attachment) files.get(emisorCer);
    File fileCer = Util.getFile(attachmentCer);
    X509Certificate cert = KeyLoader.loadX509Certificate(new FileInputStream(fileCer));
    Comprobante sellado = cfd.sellarComprobante(key, cert);
    
    //Colocar el sellado
    cfd.sellar(key, cert);    
    cfd.validar();
    cfd.verificar();
    
    //Guarda el comprobante
    cfd.guardar(new FileOutputStream(fileXML));
    
    
    //Ver existencia del java de timbrado
    String nameClassTimbrado=Finder.getJavaClass(infoTimbrado.getPactimbrado());
    Validate.validateJavaTimbrado(nameClassTimbrado);
       
    //Validar el URL
    Validate.validateURL(infoTimbrado.getUrltimbrado());
        
    
    //Timbrar el archivo
    ServicioTimbradoPAC timbrar = (ServicioTimbradoPAC) Class.forName(nameClassTimbrado).newInstance();
    timbrar.setArchivoAdjuntadoOrgLegal(files);
    timbrar.setComprobante(sellado);
    timbrar.setFileXML(fileXML);
    timbrar.setInfoTimbrado(infoTimbrado);
    timbrar.setInvoice(invoice);
    
    TimbreFiscalDigital timbreFiscal = timbrar.timbrarFactura();
    
    
    //Anadir el timbrefiscal el comprobante ya sellado
    Complemento complemento = new Complemento();
    complemento.getAny().add(timbreFiscal);
    sellado.setComplemento(complemento);
    
    CFDv32 cfdTimbrado = null;
    if  (namePackageAddenda == null){
      cfdTimbrado = new CFDv32(sellado);
      }
    else{
      cfdTimbrado = new CFDv32(sellado, namePackageAddenda);
      }
    
    //Validar el archivo con el sellado
    cfdTimbrado.validar();
    cfdTimbrado.verificar();
    
    //guardar el archivo con el nuevo sellado
    cfdTimbrado.guardar(new FileOutputStream(fileXML));
        

    return fileXML;
  }

  //Metodo de cancelacion para ser utilizado a los PAC que generan el XML en automatico
  @Override
  public void cancel(Invoice invoice) throws Exception {
        
    
  }

}
