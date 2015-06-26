package com.tegik.facelectr.email.params;

import java.util.ArrayList;
import java.util.List;

import com.tegik.facelectr.email.CustomizeEmail;

public class DefaultPersonalizarEmail extends CustomizeEmail {

  @Override
  public boolean enviarCorreo() {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public List<String> getListaCorreos() {
    // TODO Auto-generated method stub
    return new ArrayList<String>();
  }

  @Override
  public String createSubject() {
    return null;
  }

  @Override
  public String createMessage() {
    return null;
  }

}
