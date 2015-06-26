package com.tegik.facelectr.servicios;

 
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;


 
/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author www.codejava.net
 *
 */
public class MultipartUtility {
  
  private static final Logger log = Logger.getLogger(MultipartUtility.class);

  MultipartEntity entity;
  String url;
  
  
  public MultipartUtility(String url){
    entity=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    this.url= url;
    
  }
  
  public void addText(String para, String value){
    try {
      Charset chars = Charset.forName("UTF-8");
      entity.addPart(para, new StringBody(value,"text/plain", chars));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

  }
  
  
  public void addFile(String para, File file){
    
    try {
      ByteArrayBody body= new ByteArrayBody(toBytes(file), file.getName());
      entity.addPart(para, body);
      
    } catch (IOException e) {
      
      e.printStackTrace();
    }

    

    
  }
  
  
  
  private byte[] toBytes(File file) throws IOException{
    
    FileInputStream fis = new FileInputStream(file);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
   
        for (int readNum; (readNum = fis.read(buf)) != -1;) {
            bos.write(buf, 0, readNum);
     
        }
 
    byte[] bytes = bos.toByteArray();

    
    return bytes;

    
  }
      
      
  
  
  public String send() throws ClientProtocolException, IOException{
    
    HttpPost httpPost = new HttpPost(this.url);
    httpPost.setEntity(entity);
    HttpClient client = new DefaultHttpClient();
    HttpResponse response = client.execute(httpPost);
    HttpEntity result = response.getEntity();
    

    BufferedReader rd = new BufferedReader(new InputStreamReader(result.getContent()));
    StringBuffer res = new StringBuffer();
    String line = "";
    
    while ((line = rd.readLine()) != null) {
      res.append(line);
      }
    
    
    return  res.toString();
    
  }

  
  
    
    
    

}