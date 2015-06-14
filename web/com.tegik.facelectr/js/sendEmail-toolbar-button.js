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
 * All portions are Copyright (C) 2011-2012 Openbravo SLU
 * All Rights Reserved.
 * Contributor(s):  _________
 ************************************************************************
 */

// put within a function to hide local vars etc.
(function () {
  var buttonProps = {
    action: function () {
      var callback, orders = [],
          i, view = this.view,
          grid = view.viewGrid,
          selectedRecords = grid.getSelectedRecords();
      // collect the order ids
      for (i = 0; i < selectedRecords.length; i++) {
        orders.push(selectedRecords[i][OB.Constants.ID]);
      }

      // define the callback function which shows the result to the user
      callback = function (rpcResponse, data, rpcRequest) {
        isc.say(OB.I18N.getLabel('OBEXAPP_SumResult', [data.total]));
      };

      // and call the server
      OB.RemoteCallManager.call('org.openbravo.client.application.examples.SumOrderActionHandler', {
        orders: orders
      }, {}, callback);
    },
    buttonType: 'obexapp_sum',
    prompt: OB.I18N.getLabel('OBEXAPP_SumData'),
    updateState: function () {
      var view = this.view,
          form = view.viewForm,
          grid = view.viewGrid,
          selectedRecords = grid.getSelectedRecords();
      if (view.isShowingForm && form.isNew) {
        this.setDisabled(true);
      } else if (view.isEditingGrid && grid.getEditForm().isNew) {
        this.setDisabled(true);
      } else {
        this.setDisabled(selectedRecords.length === 0);
      }
    }
  };

  // register the button for the sales order tab
  OB.ToolbarRegistry.registerButton(buttonProps.buttonType, isc.OBToolbarIconButton, buttonProps, 100, '263');
}());