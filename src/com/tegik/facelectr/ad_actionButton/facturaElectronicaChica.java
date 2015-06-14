package com.tegik.facelectr.ad_actionButton;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openbravo.base.secureApp.HttpSecureAppServlet;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.dal.core.OBContext;
import org.openbravo.database.ConnectionProvider;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.erpCommon.utility.Utility;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalConnectionProvider;

/**
 * Creando una factura electrónica para México.
 * 
 * @author Tegik
 */
public class facturaElectronicaChica extends HttpSecureAppServlet {

  private static final Logger log = Logger.getLogger(facturaElectronicaChica.class);

  private static final long serialVersionUID = 1L;

  public void init(ServletConfig config) {
    super.init(config);
    boolHist = false;
  }

  // main HTTP call handler
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    VariablesSecureApp vars = new VariablesSecureApp(request);

    if (vars.commandIn("DEFAULT")) {

      String strWindow = vars.getStringParameter("inpwindowId");
      String strTab = vars.getStringParameter("inpTabId");
      String strInvoiceId = vars.getStringParameter("inpcInvoiceId");
      String strWindowPath = Utility.getTabURL(this, strTab, "R");


      // parse required Guest ID parameter to be processed
      facturaElectronica facturaProcess = new facturaElectronica();
      

      ServletConfig srvConfig = getServletConfig();


      

      OBError myMessage = facturaProcess.facturar(null, null, null, null, strInvoiceId, null);
      
      if(myMessage.getMessage().contains("FET")){
        
        String value = myMessage.getMessage();
        int inicio = value.indexOf("@") + 1;
        String nuevo = value.substring(inicio);
        int fin = inicio + nuevo.indexOf("@");
        value = value.substring(inicio, fin);
        
        String mensajeExtra="";
        if(myMessage.getMessage().length() > fin){
          mensajeExtra =myMessage.getMessage().substring(fin + 1); 

        }
        
        
        myMessage.setMessage(Finder.findMessageByValue(value) + mensajeExtra);
      }
 
      vars.setMessage(strTab, myMessage);
      printPageClosePopUp(response, vars, strWindowPath);
    }
  }

}
