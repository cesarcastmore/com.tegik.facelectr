/*
 *************************************************************************
 * The contents of this file are subject to the Openbravo  Public  License
 * Version  1.1  (the  "License"),  being   the  Mozilla   Public  License
 * Version 1.1  with a permitted attribution clause; you may not  use this
 * file except in compliance with the License. You  may  obtain  a copy of
 * the License at http://www.openbravo.com/legal/license.html 
 * Software distributed under the License  is  distributed  on  an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific  language  governing  rights  and  limitations
 * under the License. 
 * The Original Code is Openbravo ERP. 
 * The Initial Developer of the Original Code is Openbravo SLU 
 * All portions are Copyright (C) 2011 Openbravo SLU 
 * All Rights Reserved. 
 * Contributor(s):  __________
 ************************************************************************
 */
package com.tegik.facelectr;

import java.math.BigDecimal;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.exception.OBException;
import org.openbravo.client.kernel.BaseActionHandler;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.order.Order;

/**
 * Sums the orders passed in through a json array and returns the result.
 * 
 * This class is used as an example in howtos in the Openbravo Developers Guide:
 * http://wiki.openbravo.com/wiki/Category:Developers_Guide
 * 
 * @author mtaal
 */
public class SumOrderActionHandler extends BaseActionHandler {

  protected JSONObject execute(Map<String, Object> parameters, String data) {
    try {

      // get the data as json
      final JSONObject jsonData = new JSONObject(data);
      final JSONArray orderIds = jsonData.getJSONArray("orders");

      // start with zero
      BigDecimal total = new BigDecimal("0");

      // iterate over the orderids
      for (int i = 0; i < orderIds.length(); i++) {
        final String orderId = orderIds.getString(i);

        // get the order
        final Order order = OBDal.getInstance().get(Order.class, orderId);

        // and add its grand total
        total = total.add(order.getGrandTotalAmount());
      }

      // create the result
      JSONObject json = new JSONObject();
      json.put("total", total.doubleValue());

      // and return it
      return json;
    } catch (Exception e) {
      throw new OBException(e);
    }
  }
}
