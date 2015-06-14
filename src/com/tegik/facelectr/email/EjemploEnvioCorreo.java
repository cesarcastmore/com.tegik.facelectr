package com.tegik.facelectr.email;

import java.util.ArrayList;
import java.util.List;

public class EjemploEnvioCorreo extends PersonalizarEmail {

  @Override
  public boolean enviarCorreo() {
    return true;
  }

  @Override
  public List<String> getListaCorreos() {
    List<String> correos = new ArrayList<String>();
    return new ArrayList<String>();
  }

  @Override
  public String getAsunto() {
    String documentNo = invoice.getDocumentNo();
    return "Este es el asunto de ejemplo"+ documentNo;
  }

  @Override
  public String getMensage() {
    String documentNo = invoice.getDocumentNo();
    return "Es es el mensaje de ejemplo"+ documentNo;
  }

}
