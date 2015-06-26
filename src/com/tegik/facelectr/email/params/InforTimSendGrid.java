package com.tegik.facelectr.email.params;

import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.email.CustomizeSendGrid;

public class InforTimSendGrid implements CustomizeSendGrid {
  
  InforTimbrado timbrado;
  
  public void initializeVars(InforTimbrado timbrado){
    this.timbrado = timbrado;
  }

  @Override
  public boolean isSendGrid() {
    // TODO Auto-generated method stub
    return this.timbrado.isEnviargrid();
  }

  @Override
  public String createUserName() {
    // TODO Auto-generated method stub
    return this.timbrado.getUsernamegrid();
  }

  @Override
  public String createPassword() {
    // TODO Auto-generated method stub
    return this.timbrado.getPassgrid();
  }

  @Override
  public String createFrom() {
    // TODO Auto-generated method stub
    return this.timbrado.getFromgrid();
  }

  @Override
  public String createFromName() {
    // TODO Auto-generated method stub
    return this.timbrado.getFromnamegrid();
  }

  
}
