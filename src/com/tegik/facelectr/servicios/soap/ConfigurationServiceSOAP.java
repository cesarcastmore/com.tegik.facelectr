package com.tegik.facelectr.servicios.soap;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.io.File;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;



public class ConfigurationServiceSOAP {

  String url;
  String soapAction;
  HashMap<String, String> properties = new HashMap<String, String>();
  int connectionTimeOut=0;
  int readTimeOut=0;
  SSLSocketFactory sslSocket;
  HostnameVerifier hostname;


  
  

  /**
   * @param url
   * url del servicio web
   * @throws MalformedURLException
   * ocurre si tiene un mal formato el url
   */
  public ConfigurationServiceSOAP(String url) throws MalformedURLException {
    this.url = url;
    soapAction = null;
    this.connectionTimeOut=0;
    this.readTimeOut=0;
  }


  /**
   * @return
   * regresa el url del servicio web
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url
   * url del servicio web
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return soapAction
   * regresa el url del soapAction
   */
  public String getSoapAction() {
    return soapAction;
  }

  /**
   * @param soapAction
   * url del soapAction
   */
  public void setSoapAction(String soapAction) {
    this.soapAction = soapAction;
  }
  
  /**
   * @param property
   * propiedad de sistema
   * @param value
   * valor de la propiedad
   */
  public void addProperty(String property, String value){
    this.properties.put(property, value);
  }
  
  /**
   * @return properties
   * regresa todas las propiedades de sistema
   */
  public  HashMap<String, String> getProperties(){
    return this.properties;
  }

  /**
   * @return connectionTimeOut
   * regresa tiempo de espera para recibir el respons
   */
  public int getConnectionTimeOut() {
    return connectionTimeOut;
  }

  /**
   * @param connectionTimeOut
   * tiempo de espera para recibir el response
   */
  public void setConnectionTimeOut(int connectionTimeOut) {
    this.connectionTimeOut = connectionTimeOut;
  }

  /**
   * @return 
   * regresa el valor de la cofiguracion para leer el response
   */
  public int getReadTimeOut() {
    return readTimeOut;
  }

  /**
   * @param readTimeOut
   * time que se toma para leer un archivo 
   */
  public void setReadTimeOut(int readTimeOut) {
    this.readTimeOut = readTimeOut;
  }


  /**
   * @return the sslSocket
   */
  public SSLSocketFactory getSslSocket() {
    return sslSocket;
  }


  /**
   * @param sslSocket the sslSocket to set
   */
  public void setSslSocket(SSLSocketFactory sslSocket) {
    this.sslSocket = sslSocket;
  }


  /**
   * @return the hostname
   */
  public HostnameVerifier getHostname() {
    return hostname;
  }


  /**
   * @param hostname the hostname to set
   */
  public void setHostname(HostnameVerifier hostname) {
    this.hostname = hostname;
  }



  
  

}
