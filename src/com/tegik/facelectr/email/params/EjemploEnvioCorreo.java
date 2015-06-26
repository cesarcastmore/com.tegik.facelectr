package com.tegik.facelectr.email.params;

import java.util.ArrayList;
import java.util.List;

import com.tegik.facelectr.email.CustomizeEmail;

public class EjemploEnvioCorreo extends CustomizeEmail {

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
  public String createSubject() {
    String documentNo = invoice.getDocumentNo();
    return "Este es el asunto de ejemplo"+ documentNo;
  }

  @Override
  public String createMessage() {
    String documentNo = invoice.getDocumentNo();
    return "Es es el mensaje de ejemplo"+ documentNo;
  }

}
