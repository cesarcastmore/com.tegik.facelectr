package com.tegik.facelectr.utilidad;
import java.util.HashMap;

import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.enterprise.OrganizationInformation;
import org.openbravo.model.financialmgmt.tax.TaxRate;

import com.tegik.facelectr.attributes.AddAttributes;
import com.tegik.facelectr.comprobante.GenerateFileXML;
import com.tegik.facelectr.email.PersonalizarEmail;
import com.tegik.facelectr.timbrado.ServicioTimbradoPAC;

public class Validate {
  
  public static  void validateEmisorRFC(String rfc) throws Exception {
    if(rfc == null) throw new Exception("@FET_InvalidRFCEmisor@");
    rfc=rfc.toUpperCase().trim();
    if(!rfc.toUpperCase().matches("[A-Z,Ñ,&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z0-9]{3}")){
      throw new Exception("@FET_InvalidRFCEmisor@");
    }  
    
  }
  
  
  public static boolean validateEmail(String correo){
    if(correo == null){
      return false;
    }
    
    if(correo.toUpperCase().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@"
        + "((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")){
      return true;
    } 
    return false;
  }
  
  public static  void validateReceptorRFC(String rfc) throws Exception {
    if(rfc == null) throw new Exception("@FET_InvalidRFCReceptor@");
    rfc=rfc.toUpperCase().trim();
    if(!rfc.toUpperCase().matches("[A-Z,Ñ,&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z0-9]{3}")){
      throw new Exception("@FET_InvalidRFCReceptor@");
    }  
    
  }
  
  
  
  public static  void validateDocumentNo(String documentNo) throws Exception{
    
    if(!documentNo.matches("\\d+")){
      throw new Exception("@FET_DocumentNoDebeSerNumero@"); 
    }
    
  }
  
  public static boolean validate(String valor, String esperados[]){
    
    if(valor== null) return false;
        
    for(int i=0; i< esperados.length; i++){
      if(esperados[i].equals(valor)){
        return true;
      }
    }
    return false;
  }
  

  public static void validateTraslado(TaxRate tax) throws Exception{
    if(!validate(tax.getFetNombrefacturacion(),new String[]{"IVA", "IEPS"})){
      throw new Exception("@FET_InvalidImpuestoTrasladoFacturacion@");
    }
  }
  
  
  public static void validateRetencion(TaxRate tax) throws Exception{
    if(!validate(tax.getFetNombrefacturacion(),new String[]{"IVA", "ISR"})){
      throw new Exception("@FET_InvalidImpuestoRetencionFacturacion@");
    }
       

  }
  
  
  public static boolean isExento(TaxRate tax){
    
    if(tax.getRate().doubleValue() == 0 && tax.getFetNombrefacturacion().equals("EXENTO")){
      return true;
    }
    
    return false;
       

  }
  
  public static void validateDirFiscal(Organization org) throws Exception{
    
    OrganizationInformation orgInfo = org.getOrganizationInformationList().get(0);
    
    if(orgInfo.getLocationAddress().getAddressLine1() == null){
      throw new  Exception("@FET_NoCalleOrganizationFiscal@"+ " " +org.getName());      
    } else if( orgInfo.getLocationAddress().getPostalCode() == null){
      throw new Exception("@FET_NoCPOrganationInfoFiscal@" +" " +org.getName());
    } else if( orgInfo.getLocationAddress().getRegion() == null){
      throw new Exception("@FET_NoEstadoOrganationInfoFsical@"+  " " +org.getName());
    }else if( orgInfo.getLocationAddress().getRegion().getName() == null){
      throw new Exception("@FET_NoEstadoOrganationInfoFsical@"+  " " +org.getName());
    }else if( orgInfo.getLocationAddress().getCityName() == null){
      throw new Exception("@FET_NoCityOrganationInfoFiscal@"+  " " +org.getName());
    }
    
    String codigoPostal = orgInfo.getLocationAddress().getPostalCode();
    if(codigoPostal.length() < 5){
      throw new Exception("@FET_NoCPOrganationInfoFiscalCantidad@" +" " +org.getName());
    }

  }
  
  
  public static void validatLugarExpedido(Organization org) throws Exception{
    
    OrganizationInformation orgInfo;
    if(org.getOrganizationInformationList() == null){
      throw new  Exception("@FET_NoInformOrganitionExpedido@"+" " +org.getName());      
    } else  if(org.getOrganizationInformationList().isEmpty()){
      throw new  Exception("@FET_NoInformOrganitionExpedido@"+" " +org.getName());      
    } 
    
    orgInfo = org.getOrganizationInformationList().get(0);
    if(orgInfo.getLocationAddress() == null ){
      throw new  Exception("@FET_NoInformOrganitionExpedido@"+" " +org.getName());      
    }
    
    if( orgInfo.getLocationAddress().getRegion()== null){
      throw new Exception("@FET_NoEstadoOrganationInfoExpedido@"+" " +org.getName());
    }else if( orgInfo.getLocationAddress().getRegion().getName() == null){
      throw new Exception("@FET_NoEstadoOrganationInfoExpedido@"+" " +org.getName());
    } else if( orgInfo.getLocationAddress().getCityName() == null){
      throw new Exception("@FET_NoCityOrganationInfoExpedido@"+" " +org.getName());
    }

  }

  
  public static void validateFilesImportant(HashMap<String, Attachment> files, String nombres[] ) throws Exception{
    
    for (int i=0; i<nombres.length; i++){
      if(!files.containsKey(nombres[i])){
        throw new Exception("@FET_NoExisteNombreArchivo@ " + nombres[i]); 
      }     
      
    }
    
  }
  
  public static void validateInformaFiscal(Organization org) throws Exception{
    
    if(org.getOrganizationInformationList().isEmpty()){
      throw new  Exception("@FET_NoInformOrganition@");      
    } else if(org.getOrganizationInformationList().get(0).getTdirmDirfiscal() == null){
      throw new Exception("@FET_NoDirFiscalOrganationInfo@");
    } else if(org.getOrganizationInformationList().get(0)
        .getTdirmDirfiscal().getCityName() == null){
      throw new Exception("@FET_NoCityNameDirFiscalOrganationInfo@");
    }
  }
  
  public static void validateRFCInfo(Organization org) throws Exception{
    
    if(org.getOrganizationInformationList()== null){
      throw new  Exception("@FET_NoInformOrganition@");      
    }
    
    if(org.getOrganizationInformationList().isEmpty()){
      throw new  Exception("@FET_NoInformOrganition@");      
    } else if(org.getOrganizationInformationList().get(0).getTaxID() == null){
      throw new Exception("@FET_NoRFCOrg@");
    } 
  }
  
  
  public static void validateURL(String url) throws Exception {
    if(url == null ){
      throw new Exception("@FET_NoEndURLServiceTimbrado@");
    }
    
  }
  
  
  public static void validateJavaAddenda(String nameClass) throws Exception {
    
    
    if(nameClass == null){
      return;
    }
    
    try {
      Class.forName(nameClass);
      } catch (Exception e) {
        throw new Exception("@FET_AddendaNoExiste@");

     }
    
    
  }
  
  //Validar que este llenado el cambo de nameclass, existe el java en un paquete y sea herencia de una clase
  
  public static void validateJavaTimbrado(String nameClass) throws Exception {
    
    if(nameClass == null){
      throw new Exception("@FET_NoExisteModuloJavaRegistrado@");

    }
    
    try {
      Class.forName(nameClass);
      } catch (Exception e) {
        throw new Exception("@FET_TimbradoNoExiste@");

     }
    
    if(! (Class.forName(nameClass).newInstance() instanceof  ServicioTimbradoPAC)){
      throw new Exception("@FET_InstanciaServicioTimbradoPAC@");
    }
    
    
  }
  
  public static void validateJavaGenedoAutCFDI(String nameClass) throws Exception {
    
    if(nameClass == null){
      throw new Exception("@FET_NoExisteModuloJavaRegistrado@");

    }
    
    try {
      Class.forName(nameClass);
      } catch (Exception e) {
        throw new Exception("@FET_GeneradoAutCDFINoExiste@");

     }
    
    if(! (Class.forName(nameClass).newInstance() instanceof  GenerateFileXML)){
      throw new Exception("@FET_NoImplementGeradorFileXML@");
         
    }
    
    
  }
  
  
  
  
  public static void validateJavaCorreo(String nameClass) throws Exception {
    
    if(nameClass == null){
      throw new Exception("@FET_NoExisteModuloJavaRegistrado@");

    }
    
    try {
      Class.forName(nameClass);
      } catch (Exception e) {
        throw new Exception("@FET_JavaCorreoNoExiste@");

     }
    
    if(! (Class.forName(nameClass).newInstance() instanceof  PersonalizarEmail)){
      throw new Exception("@FET_JavaCorrreoNoInstancia@");
         
    }
    
    
  }
  
  
  
  public static void validateJavaAddAtributos(String nameClass) throws Exception {
    
    if(nameClass == null){
      throw new Exception("@FET_NoExisteModuloJavaRegistrado@");

    }
    
    try {
      Class.forName(nameClass);
      } catch (Exception e) {
        throw new Exception("@FET_AnadirAtributosNoExiste@");

     }
    
    if(! (Class.forName(nameClass).newInstance() instanceof  AddAttributes)){
      throw new Exception("@FET_NoImplementAddAtributos@");
         
    }
    
    
  }
  
  

}
