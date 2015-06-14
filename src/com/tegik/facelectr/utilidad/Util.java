package com.tegik.facelectr.utilidad;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Addenda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.openbravo.base.session.OBPropertiesProvider;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.enterprise.DocumentTemplate;
import org.openbravo.model.common.enterprise.DocumentType;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.invoice.Invoice;

import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.data.ServiceSOAP;

public class Util {
  static final Logger log = Logger.getLogger(Util.class);
  //Metodo para obtener el objecto Attachment
  
  public static String getPath(Attachment file) {
    String attachFolder = OBPropertiesProvider.getInstance().getOpenbravoProperties()
        .getProperty("attach.path");
    String path = attachFolder + "/" + file.getPath() + "/" + file.getName();
    return path;
  }
  
  //Metodo para obtener el archivo y lo crea si no existe
  public static File getFile(Attachment attachment) throws IOException{

    String path = getPath(attachment);
    File file = new File(path);
    
    if (!(file.exists())) {
      log.info("Crear las carpetas");
      file.getParentFile().mkdirs();
    }
    
    return file;
  }
  
  
  
  //Metodo para obtener todos los archivo de la factura 
  public static HashMap<String, Attachment> getAttachmentFileOrganization(Invoice invoice) throws Exception{
    HashMap<String, Attachment> files = new HashMap<String, Attachment>();
    
    List<Attachment> attachments = Finder.findAttarchemntOrgByInvoice(invoice);
    
    for ( Attachment attchment : attachments){
      String nombre = attchment.getName();    
      
      files.put(nombre, attchment);
      
      
    }
    
    return files;
    
  }
  
  
  public static HashMap<String, Attachment> getAttachmentFileOrganizationbyExtension(Invoice invoice) throws Exception{
    HashMap<String, Attachment> files = new HashMap<String, Attachment>();
    
    List<Attachment> attachments = Finder.findAttarchemntOrgByInvoice(invoice);
    if(attachments == null)
      return null;

    for ( Attachment attchment : attachments){
      int lenth=attchment.getName().length();
      String nombre = attchment.getName().substring(lenth-3);    
      
      files.put(nombre, attchment);
      
      
    }
    
    return files;
    
  }
  
  
  public static HashMap<String, Attachment> getAttachmentFileOrgLegalbyExtension(Invoice invoice) throws Exception{
    HashMap<String, Attachment> files = new HashMap<String, Attachment>();
    
    List<Attachment> attachments = Finder.findAttarchemntOrgLegalByInvoice(invoice);
    
    if(attachments == null)
      return null;
          
    for ( Attachment attchment : attachments){
      int lenth=attchment.getName().length();
      String nombre = attchment.getName().substring(lenth-3);    
      
      files.put(nombre, attchment);
      
      
    }
    
    return files;
    
  }
  
  //Metodo para obtener 
  public static HashMap<String, Attachment> getAttachmentFileInfoTimbrado(InforTimbrado infoTimbrado) throws Exception{
    HashMap<String, Attachment> files = new HashMap<String, Attachment>();
    
    List<Attachment> attachments = Finder.findAttarchemntByInforTimbrado(infoTimbrado);
    
    for ( Attachment attchment : attachments){
      String nombre = attchment.getName();    
      
      files.put(nombre, attchment);  
    }
    
    return files;
    
  }
  
  
  public static HashMap<String, Attachment> findAttarchemntBySOAP(ServiceSOAP testSOAP) throws Exception{
    HashMap<String, Attachment> files = new HashMap<String, Attachment>();
    
    List<Attachment> attachments = Finder.findAttarchemntBySOAP(testSOAP);
    
    for ( Attachment attchment : attachments){
      String nombre = attchment.getName();    
      
      files.put(nombre, attchment);  
    }
    
    return files;
    
  }
  
  
  public static String getPackageObject(Addenda addenda){
    try {
      if(addenda.getAny().isEmpty()){
        return null;
        } else{
          Object obj  = addenda.getAny().get(0);
          String namePackage = obj.getClass().getPackage().getName();    
          return namePackage;
          }
    } catch (Exception e){
      return null;
    }
    
    
    
  }
  
  
  public static String getPathAttachment(){
    return OBPropertiesProvider.getInstance().getOpenbravoProperties()
        .getProperty("attach.path");
  }
  
  
  public static Date obtenerUsoHorarioMexico() {

    Date dateGMT = ToGmt();

    long longMexico = dateGMT.getTime()
        + TimeZone.getTimeZone("America/Mexico_City").getRawOffset();
    Calendar calendarMexico = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));
    calendarMexico.setTimeInMillis(longMexico);

    return calendarMexico.getTime();

  }

  private static Date ToGmt() {
    TimeZone tz = TimeZone.getDefault();
    Calendar calendarTomcat = Calendar.getInstance(tz);

    Date ret = new Date(calendarTomcat.getTime().getTime() - tz.getRawOffset());

    // if we are now in DST, back off by the delta. Note that we are checking the GMT date, this is
    // the KEY.
    if (tz.inDaylightTime(ret)) {
      Date dstDate = new Date(ret.getTime() - tz.getDSTSavings());

      // check to make sure we have not crossed back into standard time
      // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
      if (tz.inDaylightTime(dstDate)) {
        ret = dstDate;
      }
    }
    return ret;
  }
  
  
  public static InforTimbrado crearTimbradoVersionViejita(Invoice invoice) throws Exception{
    
    InforTimbrado timbrado = new InforTimbrado();
    
    timbrado.setNombre("Timbrado Ya Configurado");
    timbrado.setDescription("Timbrado encontrado automaticamente por la version antigua");
    
    String urlTimbrado =invoice.getClient().getFetUrltimbrado();
    String urlCancelacion = invoice.getClient().getFetUrlwebser();
    
    if(urlTimbrado== null ){
      return null;
      
    }
    
    if(urlCancelacion == null){
      throw new Exception("@FET_ErrorConfiguracionVieja@");
      
    }
    
    timbrado.setUrltimbrado(urlTimbrado);
    timbrado.setUrlcancelacion(urlCancelacion);
    
    //Busca los archivo de la organizacion
    Organization org = null;
    org=invoice.getOrganization(); 
    HashMap<String, Attachment> fileExt = getAttachmentFileOrganizationbyExtension(invoice);
     
    if(fileExt == null){
      
      org=invoice.getFetOrglegal();
      fileExt = getAttachmentFileOrgLegalbyExtension(invoice);
      
      if(fileExt == null){
        throw new Exception("@FET_ErrorConfiguracionVieja@");
      }
      
    }
    
    Attachment attachmentKey=null;
    if(fileExt.containsKey("key")){
      attachmentKey = fileExt.get("key");
    }
    
    Attachment attachmentCer= null;
    if(fileExt.containsKey("cer")){
      attachmentCer = fileExt.get("cer");
    }
    
    
    if(attachmentCer == null || attachmentKey== null  ){
      throw new Exception("@FET_ErrorConfiguracionVieja@");
    }
       
    
    timbrado.setNombrecer(attachmentCer.getName());
    timbrado.setNombrekey(attachmentKey.getName());
    

    
    String password = org.getFetPassfiel();
  
    timbrado.setContrasenia(password);
    
    
    
    String pac = "TIMDIV_TimbradoDiverza_1";
    if(Finder.registradoDiverza()){
      throw new Exception("@FET_NoRegistradoDiverza@");

    }
    
    timbrado.setPactimbrado(pac);
    
    timbrado.setTimeout(new Long(20000));

    timbrado.setModificar(false);
    timbrado.setEnviarcorreo(false);
    timbrado.setTimbrar(true);
    
    

    
    
    
    
    OBDal.getInstance().save(timbrado);
    OBDal.getInstance().flush();
    OBDal.getInstance().refresh(timbrado);
    

    Attachment attcTimbradoCER = CreateFiles.createAttachment("8E17D6E07B664332A70508CA1FFC0B5F", 
        timbrado.getId(), getFile(attachmentCer));
    OBDal.getInstance().save(attcTimbradoCER);
    
    Attachment attcTimbradoKEY = CreateFiles.createAttachment("8E17D6E07B664332A70508CA1FFC0B5F", 
        timbrado.getId(), getFile(attachmentKey));
    OBDal.getInstance().save(attcTimbradoKEY);

    
    Organization orgLegal= invoice.getFetOrglegal();
    orgLegal.setFetInfotimbrado(timbrado);   
    OBDal.getInstance().save(orgLegal);
    
    OBDal.getInstance().flush();
    
    return timbrado;
  }
  
 
  
  public static List<File> getFilesInvoice(Invoice invoice) throws Exception{
    List<Attachment> attchs = Finder.findAttarchemntByInvoice(invoice);
    
    List<File> files = new ArrayList<File>();
    
    for(Attachment att : attchs){
      files.add(getFile(att));
    }
    
    return files;
    
    
  }
  
  
  public static DocumentTemplate obtenerDocumentTemplate(DocumentType docType){
    
    if(docType.getDocumentTemplateList().isEmpty()){
      
      DocumentTemplate template = new DocumentTemplate();
      
      template.setClient(docType.getClient());
      template.setOrganization(docType.getOrganization());
      template.setDocumentType(docType);
      
      template.setFetColocado(true);
      template.setName("Plantilla de Factura de Ventas");
      template.setReportFilename("Factura de Venta-@our_ref@");
      template.setTemplateLocation("@basedesign@/com/tegik/facelectr/ad_actionButton/reports");
      template.setTemplateFilename("EM_FET_Plantilla_Factura_VentaJR.jrxml");
      
      template.setShowlogo(true);
      template.setShowcompanydata(true);
      
      
      OBDal.getInstance().save(template);
      OBDal.getInstance().flush();
      
      
      return template;  
      
      
    } else {
      
      DocumentTemplate template = docType.getDocumentTemplateList().get(0);
      if(template.isFetColocado() != null ){        
        
        return template;
        
      } else {
        
        
        template.setName("Plantilla de Factura de Ventas");
        template.setReportFilename("Factura de Venta-@our_ref@");
        template.setTemplateLocation("@basedesign@/com/tegik/facelectr/ad_actionButton/reports");
        template.setTemplateFilename("EM_FET_Plantilla_Factura_VentaJR.jrxml");
        template.setFetColocado(true);
        
        OBDal.getInstance().save(template);
        OBDal.getInstance().flush();
        
        return template;
      }
      
      
    }
        
    
    
    
  }

}
