package com.tegik.facelectr.servicios;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;




public class Security {
  
  public static SSLSocketFactory getFactory( File pKeyFile, String pKeyPassword ) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException, KeyManagementException{
    
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    
    InputStream keyInput = new FileInputStream(pKeyFile);
    keyStore.load(keyInput, pKeyPassword.toCharArray());
    keyInput.close();

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());
    
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(keyStore);
    


    SSLContext context = SSLContext.getInstance("SSLv3");
    
    context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

    return context.getSocketFactory();
  }
  



}
