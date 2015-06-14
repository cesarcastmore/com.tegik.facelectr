package com.tegik.facelectr.email;

import java.util.ArrayList;
import java.util.List;

public class DefaultPersonalizarEmail extends PersonalizarEmail {

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
  public String getAsunto() {
    return null;
  }

  @Override
  public String getMensage() {
    return null;
  }

}
