package com.tegik.facelectr.email.params;

import org.openbravo.model.ad.system.Client;

import com.tegik.facelectr.email.CustomizeSendGrid;

public class ClientSendGrid  implements CustomizeSendGrid {

  Client client;
  
  public void initialize(Client client){
    this.client=client;
  }
  
  @Override
  public boolean isSendGrid() {
    // TODO Auto-generated method stub
    return this.client.isFetEnviargrid();
  }

  @Override
  public String createUserName() {
    // TODO Auto-generated method stub
    return this.client.getFetUsuariogrid();
  }

  @Override
  public String createPassword() {
    // TODO Auto-generated method stub
    return this.client.getFetPassgrid();
  }

  @Override
  public String createFrom() {
    // TODO Auto-generated method stub
    return this.client.getFetFromgrid();
  }

  @Override
  public String createFromName() {
    // TODO Auto-generated method stub
    return this.client.getFetFromnamegrid();
  }

}
