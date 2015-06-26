package com.tegik.facelectr.email.sendgrid;

import java.io.File;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;

import com.tegik.facelectr.servicios.MultipartUtility;






public class SendGrid {
  
  private static final Logger log = Logger.getLogger(SendGrid.class);

  public static String url = "https://api.sendgrid.com/api/mail.send.json";
  String charset = "UTF-8";
  
  private String api_user="";
  private String api_key="";;
  
  
  public  SendGrid(String api_user, String api_key){
    this.api_key= api_key;
    this.api_user= api_user;

    
  }
  
  
  
  public void sent(EmailAppi emailApp) throws Exception{
    
    MultipartUtility multipart = new MultipartUtility(url);


    multipart.addText("api_user", this.api_user);
    multipart.addText("api_key", this.api_key);
    
    
    if(!emailApp.getTo().isEmpty()){
      for(String correo : emailApp.getTo()){
        if(correo.contains("@")){
          multipart.addText("to[]", correo);
        }else{
          multipart.addText("toname[]", correo);
        }
        
      }
    }
    
    
    if(emailApp.getSubject() != null){
      multipart.addText("subject",emailApp.getSubject());
    }
    
    if(emailApp.getFrom() != null){
      multipart.addText("from", emailApp.getFrom());
    }
    
    if(emailApp.getFromName()!= null){
      multipart.addText("fromname", emailApp.getFromName());
    }
    
    if(emailApp.getHtml() != null){
      multipart.addText("html", emailApp.getHtml());
    }
    
    if(emailApp.getReplyTo() != null){
      multipart.addText("replyto", emailApp.getReplyTo() );
    }
    
    if(emailApp.getText() != null){
      multipart.addText("text", emailApp.getText());
    }
    
    
    if(!emailApp.getCc().isEmpty()){
      for(String correo : emailApp.getCc()){
        if(correo.contains("@")){
          multipart.addText("cc[]", correo);
        }else{
          multipart.addText("ccname[]", correo);
        }
        
      }
    }
    
    
    
    if(!emailApp.getBcc().isEmpty()){
      for(String correo : emailApp.getBcc()){
        if(correo.contains("@")){
          multipart.addText("bcc[]", correo);
        }else{
          multipart.addText("bccname[]", correo);
        }
        
      }
    }
    
    
    if(!emailApp.getFiles().isEmpty()){
      for(File file : emailApp.getFiles()){
        multipart.addFile("files["+file.getName() +"]", file);
        
      }
    }
    
    
    String result = multipart.send();
    
    JSONObject jsonMsg =new  JSONObject(result);
    String msg = jsonMsg.getString("message");
    
    if(msg.equals("success")){
      
    } else{
      JSONArray jsonErrors = jsonMsg.getJSONArray("errors");
      
      String errors="";
      for(int i=0; i<jsonErrors.length(); i++){
        errors=errors + jsonErrors.getString(i);
      }
      
      throw new Exception(errors);
      
          
    }
    
    
  
    
  }
  
  

}
