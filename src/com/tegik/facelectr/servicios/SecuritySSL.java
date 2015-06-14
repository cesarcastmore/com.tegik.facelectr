package com.tegik.facelectr.servicios;

/*
 * 
 * Documentacion del codigo   
 * 
 * http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html#SSLOverview
 * 
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html#descPhase2
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.Security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;

import com.tegik.facelectr.ad_actionButton.CustomSSLSocketFactory;




public class SecuritySSL {
  
  
  static{
    System.setProperty("VerifyHostName", "false");
    // CustomSSLSocketFactory full class path should be provided.
    System.setProperty("axis.socketSecureFactory",
      "com.tegik.facelectr.servicios.CustomSSLSocketFactory");
    System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
  }
  
  @SuppressWarnings("deprecation")
  public  SSLSocketFactory getSSLSocketFactory( File pKeyFile, String pKeyPassword ) {
    
    KeyStore keyStoreKey;
    try {
      
      //you can add the provider dynamically at runtime by calling the Security.addProvider() method at the beginning of your program.
      Security.addProvider ( new com.sun.net.ssl.internal.ssl.Provider());
      
      
      keyStoreKey = KeyStore.getInstance("PKCS12");
      InputStream keyInput = new FileInputStream(pKeyFile);
      keyStoreKey.load(keyInput, pKeyPassword.toCharArray());
      keyInput.close();

      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
      keyManagerFactory.init(keyStoreKey, pKeyPassword.toCharArray());
      
 
      
      //System property to include the new class name.
      //This action causes the specified classes to be found and loaded before the JDK default classes. 
      System.setProperty ("java.protocol.handler.pkgs",
          "com.sun.net.ssl.internal.www.protocol");
      
      //Setting this system property to true permits full (unsafe) legacy renegotiation
      System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
 
      
 //     TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//      tmf.init(keyStoreKey);

      SSLContext context = SSLContext.getInstance("SSLv3");  
      
      
      
      
      context.init(keyManagerFactory.getKeyManagers(),  new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
      
      return context.getSocketFactory();
    } catch (KeyStoreException e) { 
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (CertificateException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnrecoverableKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (KeyManagementException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
   

    return null;
  }
  
  
  public  HostnameVerifier getHostNameVerifier(){
    return new NullHostNameVerifier();
  }
  
  
  
  public void appySSLSecurity(File pKeyFile, String pKeyPassword){
    

    
    if (pKeyFile != null) {
      System.setProperty(CustomSSLSocketFactory.KEY_STORE, pKeyFile.getPath());
      // System.setProperty(CustomSSLSocketFactory.TRUST_STORE,archivoPac);
    }
    if (pKeyPassword != null) {
      System.setProperty(CustomSSLSocketFactory.KEY_STORE_PASSWORD, pKeyPassword);
      // System.setProperty(CustomSSLSocketFactory.TRUST_STORE_PASSWORD,PasswordPAC);
    }

    // System.setProperty(CustomSSLSocketFactory.TRUST_STORE_TYPE,"PKCS12");
    // System.setProperty(CustomSSLSocketFactory.TRUST_MANAGER_TYPE,"SunX509");
    System.setProperty(CustomSSLSocketFactory.KEY_STORE_TYPE, "PKCS12");
    System.setProperty(CustomSSLSocketFactory.KEY_MANAGER_TYPE, "SunX509");

    System.setProperty(CustomSSLSocketFactory.SECURITY_PROVIDER_CLASS,
        "com.sun.net.ssl.internal.ssl.Provider");
    System.setProperty(CustomSSLSocketFactory.SECURITY_PROTOCOL, "SSLv3");
    System.setProperty(CustomSSLSocketFactory.PROTOCOL_HANDLER_PACKAGES,
        "com.sun.net.ssl.internal.www.protocol");
    
    
  }
  



}
