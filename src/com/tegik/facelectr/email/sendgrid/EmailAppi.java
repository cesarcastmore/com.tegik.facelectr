package com.tegik.facelectr.email.sendgrid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class EmailAppi {
  
  List<String> to= new ArrayList<String>();
  String subject;
  String text;
  String html;
  String from;
  List<String> cc= new ArrayList<String>();
  List<String> bcc= new ArrayList<String>();
  String fromName;
  String replyTo;
  List<File> files= new ArrayList<File>();
  
  private static final Logger log = Logger.getLogger(EmailAppi.class);

  /**
   * @return the to
   */
  
  public EmailAppi(){
    subject=null;
    text=null;
    html=null;
    from=null;
    List<String> cc= new ArrayList<String>();
    List<String> bcc= new ArrayList<String>();
    fromName=null;
    replyTo=null;
    List<File> files= new ArrayList<File>();
    to= new ArrayList<String>();
    
    
  }

  /**
   * @param to the to to set
   */
  public void addTo(String to, String toName) {
    this.to.add(to);
    this.to.add(toName);
  }
  
  
  public void addTo(String to) {
    this.to.add(to);
  }
  
  
  
  public List<String> getTo() {
    return this.to;
  }
  
  /**
   * @param subject the subject to set
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }


  public String getSubject() {
    return this.subject;
  }

  
  /**
   * @param text the text to set
   */
  public void setText(String text) {
    this.text = text;
  }
  
  public String getText() {
    return this.text;
  }
  
  /**
   * @param html the html to set
   */
  public void setHtml(String html) {
    this.html = html;
  }
  
  public String getHtml() {
    return this.html;
  }
  /**
   * @param from the from to set
   */
  public void setFrom(String from) {
    this.from = from;
  }

  public String getFrom() {
    return this.from;
  }
  
  public void addCc(String cc, String ccName) {
    this.cc.add(cc);
    this.cc.add(ccName);

  }
  

  public void addCc(String cc) {
    this.cc.add(cc);
  }
 

  public List<String> getCc() {
    return this.cc;


  }
  
  
  public void setBcc(String bcc,String bccName) {;
    this.bcc.add(bcc);
    this.bcc.add(bccName);

  }
 
  
  public void setBccName(String bcc) {
    this.bcc.add(bcc);
  }


  public List<String> getBcc() {
  return this.bcc;
  }
  
  public void setFromName(String fromName) {
    this.fromName =fromName;
  }
  
  public String getFromName() {
    return this.fromName;
  }
  
  
  public void setReplyTo(String replyTo) {
    this.replyTo =replyTo;
  }
  
  public String getReplyTo() {
    return this.replyTo;
  }

  
  public void addFile(File file) {
    this.files.add(file);
  } 
  
  public List<File> getFiles() {
    return this.files;
  } 
  

}
