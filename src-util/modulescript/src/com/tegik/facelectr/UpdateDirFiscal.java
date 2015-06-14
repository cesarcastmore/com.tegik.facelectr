package com.tegik.facelectr;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.openbravo.database.ConnectionProvider;
import org.openbravo.modulescript.ModuleScript;


public class UpdateDirFiscal extends ModuleScript{

  @Override
  public void execute() {
    
    
      
      try {
        ConnectionProvider cp = getConnectionProvider();

        PreparedStatement orgInfoQuery = cp.
            getPreparedStatement("SELECT Em_Tdirm_DirFiscal, Ad_Org_ID FROM Ad_OrgInfo "
                + "WHERE Em_Tdirm_DirFiscal IS NOT NULL  AND C_Location_ID IS NULL");
        orgInfoQuery.execute();
        
        ResultSet resultOrgInfo = orgInfoQuery.getResultSet();
        
        while (resultOrgInfo.next()) {
          
          String tdir_discalID = resultOrgInfo.getString("Em_Tdirm_DirFiscal");
          String orgInfoID = resultOrgInfo.getString("Ad_Org_ID");

          PreparedStatement ps = cp.getPreparedStatement("UPDATE Ad_OrgInfo SET C_Location_ID = ? WHERE Ad_Org_ID = ?");
          
          ps.setString(1, tdir_discalID);
          ps.setString(2, orgInfoID);
          ps.executeUpdate();
      }
        


        
      } catch (Exception e) {
        handleError(e);

      }
      
      
    
    
    

  }

}
