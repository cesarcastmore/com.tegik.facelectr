package com.tegik.facelectr.servicios;

public class Response {
  String contentType;
  String body;
  int status;
  
  /**
   * @return the contentType
   */
  public String getContentType() {
    return contentType;
  }
  /**
   * @param contentType the contentType to set
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  /**
   * @return the object
   */
  public String getBody() {
    return body;
  }
  /**
   * @param object the object to set
   */
  public void setBody(String body) {
    this.body = body;
  }
  /**
   * @return the status
   */
  public int getStatus() {
    return status;
  }
  /**
   * @param status the status to set
   */
  public void setStatus(int status) {
    this.status = status;
  }
  
  
  

}
