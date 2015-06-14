package com.tegik.facelectr.ad_actionButton;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.enterprise.OrganizationInformation;
import org.openbravo.model.common.businesspartner.Location;;

public class Validate {
  
  public static  boolean validateRFC(String rfc){
    if(rfc == null) return false;
    rfc=rfc.toUpperCase().trim();
    return rfc.toUpperCase().matches("[A-Z,Ñ,&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z0-9]{3}");    
    
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
  
  public static void validate(Organization org) throws Exception{
    
    OrganizationInformation orgInfo;
    if(org.getOrganizationInformationList().isEmpty()){
      throw new Exception("FET_NoOrganizationInfoOrgani@");
    } else {
      orgInfo = org.getOrganizationInformationList().get(0);
    }
    
    if(orgInfo.getLocationAddress().getAddressLine1() == null){
      throw new  Exception("@FET_NoCalleOrganization@");      
    } else if( orgInfo.getLocationAddress().getPostalCode() == null){
      throw new Exception("@FET_NoCPOrganationInfo@");
    } else if( orgInfo.getLocationAddress().getAddressLine2() == null){
      throw new Exception("@FET_NoColoniaOrganationInfo@");
    }else if( orgInfo.getLocationAddress().getRegion().getName() == null){
      throw new Exception("@FET_NoEstadoOrganationInfo@");
    }else if( orgInfo.getLocationAddress().getCityName() == null){
      throw new Exception("@FET_NoCityOrganationInfo@");
    }else if( orgInfo.getLocationAddress().getTdirmNumex() == null){
      throw new Exception("@FET_NoNumExtOrganationInfo@");
    }

  }
  
  public static void validateBussiness(Location bp) throws Exception{
    
    if(bp == null){
      throw new  Exception("@FET_NoCalleBusiness@");      
    } else if( bp.getLocationAddress().getPostalCode() == null){
      throw new Exception("@FET_NoCPBusiness@");
    } else if( bp.getLocationAddress().getAddressLine2() == null){
      throw new Exception("@FET_NoColoniaBusiness@");
    }else if( bp.getLocationAddress().getRegion().getName() == null){
      throw new Exception("@FET_NoEstadoBusiness@");
    }else if( bp.getLocationAddress().getCityName() == null){
      throw new Exception("@FET_NoCityBusiness@");
    }else if( bp.getLocationAddress().getTdirmNumex() == null){
      throw new Exception("@FET_NoNumExtBusiness@");
    }
    

  }

}
