package com.tegik.facelectr.servicios;

import java.util.HashMap;

public class Request{
  
  public static String POST ="POST";
  public static String GET ="GET";
  public static String DELETE ="DELETE";
  public static String PUT ="PUT"; 
  
  String contentType;
  String body;
  String method;
  HashMap<String, String> param = new HashMap<String, String>();
  String usuario;
  String contrasenia;
  String url;
  String soapAction;



  
  public Request(){
    param = new HashMap<String, String>();
  }
  
  /**
   * @return the contentType
   */
  public String getContentType() {
    return contentType;
  }
  /**
   * @return the method
   */
  public String getMethod() {
    return method;
  }
  /**
   * @param method the method to set
   */
  public void setMethod(String method) {
    this.method = method;
  }
  /**
   * @return the param
   */
  public HashMap<String, String> getParam() {
    return param;
  }
  /**
   * @param param the param to set
   */
  public void addParam(String key, String value) {
    param.put(key, value);
  }
  /**
   * @param contentType the contentType to set
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  /**
   * @return the body
   */
  public String getBody() {
    return body;
  }
  /**
   * @param body the body to set
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return the usuario
   */
  public String getUsuario() {
    return usuario;
  }

  /**
   * @param usuario the usuario to set
   */
  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }

  /**
   * @return the contrasenia
   */
  public String getContrasenia() {
    return contrasenia;
  }

  /**
   * @param contrasenia the contrasenia to set
   */
  public void setContrasenia(String contrasenia) {
    this.contrasenia = contrasenia;
  }

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the soapAction
   */
  public String getSoapAction() {
    return soapAction;
  }

  /**
   * @param soapAction the soapAction to set
   */
  public void setSoapAction(String soapAction) {
    this.soapAction = soapAction;
  }
  
  
  
}