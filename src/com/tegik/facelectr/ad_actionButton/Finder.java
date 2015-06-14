package com.tegik.facelectr.ad_actionButton;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.ui.Message;

public class Finder {

  
  public static String findMessageByValue(String value){
    OBContext.setAdminMode(true);   
    
    OBCriteria<Message> obMssg = OBDal.getInstance().createCriteria(Message.class);
    obMssg.add(Restrictions.eq(Message.PROPERTY_SEARCHKEY, value));
    List<Message> messages = obMssg.list();
    if (messages.isEmpty()) {
      return value;
    }
    String res = messages.get(0).getMessageText();
    OBContext.restorePreviousMode();

    return res;
  }
}
