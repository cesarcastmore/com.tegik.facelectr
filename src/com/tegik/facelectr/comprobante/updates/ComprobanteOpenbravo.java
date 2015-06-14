package com.tegik.facelectr.comprobante.updates;

import java.util.ArrayList;
import java.util.List;

import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.model.common.invoice.InvoiceLine;
import org.openbravo.model.common.invoice.InvoiceTax;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.enterprise.Organization;



public class ComprobanteOpenbravo {
  
  BusinessPartner receptor;
  Organization emisor;
  List<InvoiceLine> conceptos= new ArrayList<InvoiceLine>();
  List<InvoiceTax> retenciones= new ArrayList<InvoiceTax>();
  List<InvoiceTax> traslados= new ArrayList<InvoiceTax>();
  Invoice encabezado;
 
  
  /**
   * @return the receptor
   */
  public BusinessPartner getReceptor() {
    return receptor;
  }
  /**
   * @param receptor the receptor to set
   */
  public void setReceptor(BusinessPartner receptor) {
    this.receptor = receptor;
  }
  /**
   * @return the emisor
   */
  public Organization getEmisor() {
    return emisor;
  }
  /**
   * @param emisor the emisor to set
   */
  public void setEmisor(Organization emisor) {
    this.emisor = emisor;
  }
  /**
   * @return the conceptos
   */
  public List<InvoiceLine> getConceptos() {
    return conceptos;
  }
  /**
   * @param conceptos the conceptos to set
   */
  public void addConceptos(InvoiceLine concepto) {
    this.conceptos.add(concepto);
  }
  /**
   * @return the retenciones
   */
  public List<InvoiceTax> getRetenciones() {
    return retenciones;
  }
  /**
   * @param retenciones the retenciones to set
   */
  public void addRetenciones(InvoiceTax retencion) {
    this.retenciones.add(retencion);;
  }
  /**
   * @return the traslados
   */
  public List<InvoiceTax> getTraslados() {
    return traslados;
  }
  /**
   * @param traslados the traslados to set
   */
  public void addTraslados(InvoiceTax traslado) {
    this.traslados.add(traslado);
  }
  /**
   * @return the encabezado
   */
  public Invoice getEncabezado() {
    return encabezado;
  }
  /**
   * @param encabezado the encabezado to set
   */
  public void setEncabezado(Invoice encabezado) {
    this.encabezado = encabezado;
  }
  
  
}
