package com.tegik.facelectr.utilidad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.log4j.Logger;
import org.openbravo.base.ConfigParameters;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.base.session.OBPropertiesProvider;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.JRFormatFactory;
import org.openbravo.erpCommon.utility.PrintJRData;
import org.openbravo.erpCommon.utility.Utility;
import org.openbravo.model.ad.datamodel.Table;
import org.openbravo.model.ad.system.Client;
import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.enterprise.DocumentTemplate;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.utils.Replace;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalConnectionProvider;
import org.openbravo.client.kernel.RequestContext;



public class CreateFiles {
  static final Logger log = Logger.getLogger(CreateFiles.class);

  
  public static Attachment createAttachment(String table_id, String record_id, String nameFile, String extension) {
    Attachment archivo = new Attachment();


    OBContext.setAdminMode(true);

    Client cliente = OBContext.getOBContext().getCurrentClient();
    Organization org = OBContext.getOBContext().getCurrentOrganization();
    archivo.setClient(cliente);
    archivo.setOrganization(org);
    nameFile = nameFile + "." +extension;
    archivo.setName(nameFile);
    String path = table_id + "/" + record_id;
    archivo.setPath(path);

    Table table = OBDal.getInstance().get(Table.class, table_id);
    archivo.setTable(table);
    archivo.setRecord(record_id);
    archivo.setSequenceNumber(10L);
    archivo.setAllowRead(true);
    archivo.setActive(true);
    
    OBDal.getInstance().save(archivo);
    OBDal.getInstance().flush();

    OBContext.restorePreviousMode();

    return archivo;
  }
  
  
  public static Attachment createAttamentPDF(String table_id, String record_id, Invoice invoice) throws Exception{
    
    Attachment archivo = createAttachment(table_id, record_id, invoice.getDocumentNo(), "pdf") ;
    
      File archivoPDF = Util.getFile(archivo); 
          
      OBContext.setAdminMode(true);
      String strLanguage = "es_MX";
      
      String rutaAttach = Util.getPathAttachment();
  
      DocumentTemplate docTemplate = Util.obtenerDocumentTemplate(invoice.getDocumentType());
      String strReportName =docTemplate.getTemplateLocation() + "/"+ docTemplate.getTemplateFilename();
          
      String invoiceID = invoice.getId();
      String strAttach = rutaAttach + "/284-" + invoiceID;
      Locale locLocale = new Locale(strLanguage.substring(0, 2), strLanguage.substring(3, 5));
  
      String strBaseDesign="";    
      ConfigParameters confParam =  ConfigParameters.retrieveFrom(RequestContext.getServletContext());
      String strNewAddBase = confParam.strDefaultDesignPath;
      String strFinal = confParam.strBaseDesignPath;
      if (!strFinal.endsWith("/" + strNewAddBase)) {
        strFinal += "/" + strNewAddBase;
        
      }
      
      strBaseDesign = confParam.prefix + strFinal;
  
      strReportName = Replace.replace(Replace.replace(strReportName, "@basedesign@", strBaseDesign),
          "@attach@", strAttach);
        
      JasperReport jasperReport = Utility.getTranslatedJasperReport(new DalConnectionProvider(false), strReportName,
            strLanguage, strBaseDesign);
      
      
      HashMap<String, Object> designParameters = new HashMap<String, Object>();
      designParameters.put("DOCUMENT_ID", invoice.getId());
      designParameters.put("IS_IGNORE_PAGINATION", false);
      designParameters.put("BASE_WEB", "http://localhost/openbravo/web");
      designParameters.put("BASE_DESIGN", strBaseDesign);
      designParameters.put("ATTACH", strAttach);
      designParameters.put("USER_CLIENT", invoice.getClient());
      designParameters.put("USER_ORG", invoice.getOrganization());
      designParameters.put("LANGUAGE", strLanguage);
      designParameters.put("LOCALE", locLocale);
      designParameters.put("REPORT_TITLE", PrintJRData.getReportTitle(new DalConnectionProvider(false), "es_MX", invoiceID));
  
      DecimalFormatSymbols dfs = new DecimalFormatSymbols();
      dfs.setDecimalSeparator(".".charAt(0));
      dfs.setGroupingSeparator(",".charAt(0));
      DecimalFormat numberFormat = new DecimalFormat("#,##0.##", dfs);
      designParameters.put("NUMBERFORMAT", numberFormat);
       
      String javaDateFormat = OBPropertiesProvider.getInstance().getOpenbravoProperties().getProperty("dateFormat.java");
      final JRFormatFactory jrFormatFactory = new JRFormatFactory();
      jrFormatFactory.setDatePattern(javaDateFormat);
        
      designParameters.put(JRParameter.REPORT_FORMAT_FACTORY, jrFormatFactory);
        
      JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport, designParameters, 
            new DalConnectionProvider(false).getConnection());
        
        
      JasperExportManager.exportReportToPdfFile(jasperPrint, archivoPDF.getPath());
      OBContext.restorePreviousMode();

    

    return archivo;
  }
  
  public  static void createFile(byte[] bytes, File file) throws IOException{
    
    FileOutputStream fos; 
    fos = new FileOutputStream(file); 
    fos.write(bytes); 
    fos.flush(); 
    fos.close(); 
    
  }
  
  public static void createFile(String cadena, File file) throws IOException{
    
    BufferedWriter bw= new BufferedWriter(new FileWriter(file));
    bw.write(cadena);
    bw.flush();
    bw.close();
     
  }
  
  
  public static void copyFileUsingFileStreams(File source, File dest) throws IOException {
    
    FileInputStream input = new FileInputStream(source);
    FileOutputStream output = new FileOutputStream(dest);
    
    byte[] buf = new byte[1024];
    int bytesRead;
      
      while ((bytesRead = input.read(buf)) > 0) {
        output.write(buf, 0, bytesRead);
        }
    
      input.close();
      output.close();
        
    }
 
  
  public static Attachment createAttachment(String table_id, String record_id, File source) throws IOException {
    Attachment archivo = new Attachment();

    OBContext.setAdminMode(true);

    Client cliente = OBContext.getOBContext().getCurrentClient();
    Organization org = OBContext.getOBContext().getCurrentOrganization();
    archivo.setClient(cliente);
    archivo.setOrganization(org);
    archivo.setName(source.getName());       
    
    String attachFolder = OBPropertiesProvider.getInstance().getOpenbravoProperties()
        .getProperty("attach.path");
    
    
    String path=table_id + "/" + record_id;
    archivo.setPath(path);

    Table table = OBDal.getInstance().get(Table.class, table_id);
    archivo.setTable(table);
    archivo.setRecord(record_id);
    archivo.setSequenceNumber(10L);
    archivo.setAllowRead(true);
    archivo.setActive(true);
    
    String pathfinal = attachFolder + "/" + path + "/" + source.getName();
    File dest = new File(pathfinal);
    
    if(!dest.exists()){
      dest.getParentFile().mkdirs();
    }
    
    copyFileUsingFileStreams(source, dest);

    
    OBDal.getInstance().save(archivo);

    OBContext.restorePreviousMode();
    
    return archivo;
  }
  

}
