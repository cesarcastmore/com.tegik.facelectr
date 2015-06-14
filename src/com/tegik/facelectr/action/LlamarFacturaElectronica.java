package com.tegik.facelectr.action;

import java.util.HashMap;

import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.dal.core.OBContext;
import org.openbravo.database.ConnectionProvider;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalConnectionProvider;

import com.tegik.facelectr.utilidad.Translate;

public class LlamarFacturaElectronica {
  
  public static void timbrar(Invoice invoice) throws Exception{
    
    
    if(invoice.getDocumentStatus().equals("CO")){
      throw new Exception("La factura no ha sido completada");
    }
    
    String processId = "09E868881D2A4ED495D97AAE3104BF5F";
    
    VariablesSecureApp vars = null;
    vars = new VariablesSecureApp(OBContext.getOBContext().getUser().getId(), OBContext
        .getOBContext().getCurrentClient().getId(), OBContext.getOBContext()
        .getCurrentOrganization().getId(), OBContext.getOBContext().getRole().getId(), OBContext
        .getOBContext().getLanguage().getLanguage());

    
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("C_Invoice_ID", invoice.getId()); 
    
    ConnectionProvider conn = new DalConnectionProvider(true);
    ProcessBundle pb = new ProcessBundle(processId, vars).init(conn);
    
    pb.setParams(params);
    
    new CreateElectronicInvoice().doExecute(pb);
    
    OBError pbResult = (OBError) pb.getResult();
    
    
    if(pbResult.getType().equals("Error")){
      String msgError = Translate.translate(pbResult.getMessage()); 
      
      throw new Exception(msgError);
      
    }

    
    
    
  }
  
  
  public static void cancelar(Invoice invoice) throws Exception{
    
    if(invoice.getDocumentStatus().equals("VO")){
      throw new Exception("La factura no ha sido cancelada");
    }
    
    
String processId = "880A78503E2F4C0C80B3FC5BD39E39FC";
    
    VariablesSecureApp vars = null;
    vars = new VariablesSecureApp(OBContext.getOBContext().getUser().getId(), OBContext
        .getOBContext().getCurrentClient().getId(), OBContext.getOBContext()
        .getCurrentOrganization().getId(), OBContext.getOBContext().getRole().getId(), OBContext
        .getOBContext().getLanguage().getLanguage());

    
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("C_Invoice_ID", invoice.getId()); 
    
    ConnectionProvider conn = new DalConnectionProvider(true);
    ProcessBundle pb = new ProcessBundle(processId, vars).init(conn);
    
    pb.setParams(params);
    
    new CancelElectronicInvoice().doExecute(pb);
    
    OBError pbResult = (OBError) pb.getResult();
    
    
    if(pbResult.getType().equals("Error")){
      String msgError = Translate.translate(pbResult.getMessage()); 
      
      throw new Exception(msgError);
      
    }
    
    
  }

}
