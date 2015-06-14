package com.tegik.facelectr.servicios.sat;

import java.math.BigDecimal;

public class ExpresionImpresa {
  
  public String rfcEmisor;
  public String rfcReceptor;
  public BigDecimal cantidad;
  public String identificador;
  
  public ExpresionImpresa(String rfcEmisor, String rfcReceptor, BigDecimal total, String uuid){
    this.rfcEmisor=rfcEmisor;
    this.rfcReceptor=rfcReceptor;
    this.cantidad=total;
    this.identificador= uuid;
    
    
  }
  
  public ExpresionImpresa(){
;
    
    
  }
  
  
  public String getRFCEmisor() {
    return rfcEmisor;
  }
  public void setRFCEmisor(String rFCEmisor) {
    rfcEmisor = rFCEmisor;
  }
  public String getRFCReceptor() {
    return rfcReceptor;
  }
  public void setRFCReceptor(String rFCReceptor) {
    rfcReceptor = rFCReceptor;
  }
  public BigDecimal getCantidad() {
    return cantidad;
  }
  public void setCantidad(BigDecimal cantidad) {
    this.cantidad = cantidad;
  
  }
  
  public String getIdentificador() {
    return identificador;
  }
  
  public void setIdentificador(String identificador) {
    this.identificador = identificador;
  }
  
  public String toString(){
    String expresion="";
    expresion = expresion + "re=" +rfcEmisor;  
    expresion = expresion + "&rr=" +rfcReceptor; 
    expresion = expresion + "&tt=" +cantidad.toString();
    expresion = expresion + "&id=" + identificador;
    return expresion;
  }
  
  

}
