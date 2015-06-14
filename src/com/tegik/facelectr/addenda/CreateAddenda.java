package com.tegik.facelectr.addenda;

import java.util.List;

import org.openbravo.model.common.invoice.Invoice;

public interface CreateAddenda {
  
  public List<Object> getObjects(Invoice inv) throws Exception;

}
