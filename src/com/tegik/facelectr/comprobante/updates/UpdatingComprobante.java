package com.tegik.facelectr.comprobante.updates;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;

import org.openbravo.model.common.invoice.Invoice;

public interface UpdatingComprobante {
  
  public void UpdateComprobante(Comprobante comprobante, ComprobanteOpenbravo comprobanteOB) throws Exception;

}
