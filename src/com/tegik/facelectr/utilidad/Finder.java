package com.tegik.facelectr.utilidad;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.datamodel.Table;
import org.openbravo.model.ad.system.Client;
import org.openbravo.model.ad.ui.Message;
import org.openbravo.model.ad.utility.Attachment;
import org.openbravo.model.ad.utility.TreeNode;
import org.openbravo.model.common.currency.ConversionRate;
import org.openbravo.model.common.currency.Currency;
import org.openbravo.model.common.enterprise.EmailServerConfiguration;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.invoice.Invoice;
import org.openbravo.model.common.enterprise.OrganizationInformation;

import com.tegik.facelectr.data.InforTimbrado;
import com.tegik.facelectr.data.MensajeCorreo;
import com.tegik.facelectr.data.ServiceSOAP;

public class Finder {

  
  public static String findMessageByValue(String value){
    OBContext.setAdminMode(true);
    value = value.replaceAll("@", "");
    OBCriteria<Message> obMssg = OBDal.getInstance().createCriteria(Message.class);
    obMssg.add(Restrictions.eq(Message.PROPERTY_SEARCHKEY, value));
    List<Message> messages = obMssg.list();
    if (messages.isEmpty()) {
      return value;
    }
    String res = messages.get(0).getMessageText();
    OBContext.restorePreviousMode();

    return res;
  }
  
  
  public static List<Attachment> findAttarchemntOrgByInvoice(Invoice invoice) throws Exception{
    
    OBContext.setAdminMode(true);
    
    final OBCriteria<Attachment> attachmentList = OBDal.getInstance().createCriteria( Attachment.class);
    attachmentList.add(Expression.eq(Attachment.PROPERTY_TABLE, OBDal.getInstance().get(Table.class, "155")));
    attachmentList.add(Expression.eq(Attachment.PROPERTY_RECORD, invoice.getOrganization()
        .getId()));
    
    if(attachmentList.list().isEmpty()){
      return null;
    }
    
    List<Attachment> listaAttachment = attachmentList.list();
    
    OBContext.restorePreviousMode();
    
    return listaAttachment;
    
  }
  
  
  public static List<Attachment> findAttarchemntOrgLegalByInvoice(Invoice invoice) {
    
    OBContext.setAdminMode(true);
    
    final OBCriteria<Attachment> attachmentList = OBDal.getInstance().createCriteria( Attachment.class);
    attachmentList.add(Expression.eq(Attachment.PROPERTY_TABLE, OBDal.getInstance().get(Table.class, "155")));
    attachmentList.add(Expression.eq(Attachment.PROPERTY_RECORD, invoice.getFetOrglegal()
        .getId()));
    
    if(attachmentList.list().isEmpty()){
      return null;
    }
    
    List<Attachment> listaAttachment = attachmentList.list();
    
    OBContext.restorePreviousMode();
    
    return listaAttachment;
    
  }
  
  public static List<Attachment> findAttarchemntByInvoice(Invoice invoice) throws Exception{
    
    OBContext.setAdminMode(true);
    
    final OBCriteria<Attachment> attachmentList = OBDal.getInstance().createCriteria( Attachment.class);
    attachmentList.add(Expression.eq(Attachment.PROPERTY_TABLE, OBDal.getInstance().get(Table.class, "318")));
    attachmentList.add(Expression.eq(Attachment.PROPERTY_RECORD, invoice.getId()));
    
    if(attachmentList.list().isEmpty()){
      throw new Exception("@FET_NoHayArchivosAdjuntadosInv@");
    }
    
    List<Attachment> listaAttachment = attachmentList.list();
    
    OBContext.restorePreviousMode();
    
    return listaAttachment;
    
  }
  
  public static List<Attachment> findAttarchemntByInforTimbrado(InforTimbrado inforTimbrado) throws Exception{
    
    OBContext.setAdminMode(true);
    
    final OBCriteria<Attachment> attachmentList = OBDal.getInstance().createCriteria( Attachment.class);
    attachmentList.add(Expression.eq(Attachment.PROPERTY_TABLE, OBDal.getInstance().get(Table.class, "8E17D6E07B664332A70508CA1FFC0B5F")));
    attachmentList.add(Expression.eq(Attachment.PROPERTY_RECORD, inforTimbrado.getId()));
    
    if(attachmentList.list().isEmpty()){
      throw new Exception("@FET_NoHayArchivosAdjuntadosInfoTimbrado@");
    }
    
    List<Attachment> listaAttachment = attachmentList.list();
    
    OBContext.restorePreviousMode();
    
    return listaAttachment;
    
  }
  
  
  
  public static List<ConversionRate> findConversionRatebyDate(Invoice invoice, Date date) throws Exception{
    
    OBContext.setAdminMode(true);
    
    final OBCriteria<ConversionRate> rateList = OBDal.getInstance().createCriteria(
        ConversionRate.class);
    rateList.add(Expression.eq(ConversionRate.PROPERTY_TOCURRENCY,
        OBDal.getInstance().get(Currency.class, "130")));
    rateList.add(Expression.eq(ConversionRate.PROPERTY_CURRENCY, invoice.getCurrency()));
    rateList.add(Expression.eq(ConversionRate.PROPERTY_VALIDFROMDATE, date));
    rateList.add(Expression.eq(ConversionRate.PROPERTY_VALIDTODATE, date));
    
    return rateList.list();
    
  }
  
  public static String getJavaClass(String value) throws Exception{
    
    OBContext.setAdminMode(true);
    final OBCriteria<org.openbravo.model.ad.domain.List> rateList = OBDal.getInstance().createCriteria(
        org.openbravo.model.ad.domain.List.class);
    rateList.add(Expression.eq(org.openbravo.model.ad.domain.List.PROPERTY_SEARCHKEY, value));
    
    if(rateList.list().isEmpty()){
      throw new Exception("@FET_NoRegistradoJavaList@");
    }
    
    return rateList.list().get(0).getFetLlamarjava();
    
  }


  public static List<Attachment> findAttarchemntBySOAP(ServiceSOAP testSOAP) throws Exception {
  OBContext.setAdminMode(true);
    
    final OBCriteria<Attachment> attachmentList = OBDal.getInstance().createCriteria( Attachment.class);
    attachmentList.add(Expression.eq(Attachment.PROPERTY_TABLE, OBDal.getInstance().get(Table.class, "0B4496F55B8D4F44B6C75E3A436D4E13")));
    attachmentList.add(Expression.eq(Attachment.PROPERTY_RECORD, testSOAP.getId()));
    
    if(attachmentList.list().isEmpty()){
      throw new Exception("@FET_NoHayArchivosAdjuntadosInfoTimbrado@");
    }
    
    List<Attachment> listaAttachment = attachmentList.list();
    
    OBContext.restorePreviousMode();
    
    return listaAttachment;
  }
  
  
  public static List<OrganizationInformation> findOrgInfo()  {
  OBContext.setAdminMode(true);
    
    final OBCriteria<OrganizationInformation> infoList = OBDal.getInstance().createCriteria( OrganizationInformation.class);
    infoList.add(Expression.isNull(OrganizationInformation.PROPERTY_LOCATIONADDRESS));
    infoList.add(Expression.isNotNull(OrganizationInformation.PROPERTY_TDIRMDIRFISCAL));

    

    List<OrganizationInformation> listaInfos = infoList.list();
    
    if(listaInfos == null){
      return new ArrayList<OrganizationInformation>();
    }
    
    OBContext.restorePreviousMode();
    
    return listaInfos;
  }
  
  
  
  public static TreeNode findTreeNodeByOrganizationID(Organization org){
    OBContext.setAdminMode(true);
    
    OBCriteria<TreeNode> obTree = OBDal.getInstance().createCriteria(TreeNode.class);
    obTree.add(Restrictions.eq(TreeNode.PROPERTY_NODE, org.getId()));
    List<TreeNode> nodos = obTree.list();

    OBContext.restorePreviousMode();
    return nodos.get(0);
    
  }
  
  
  public static String findṔadre(Organization org){
    TreeNode node = findTreeNodeByOrganizationID(org);
    
    boolean entidadLegal = org.getOrganizationType().isLegalEntity();
    boolean  entidadLegalContabi = org.getOrganizationType().isLegalEntityWithAccounting();
    
    if(node.getReportSet().equals("0") ||entidadLegal ||  entidadLegalContabi ){
      return node.getNode();
    }
    
    Organization orgPadre = OBDal.getInstance().get(Organization.class, node.getReportSet());
    
    return findṔadre(orgPadre);
  }
  
  
  public static EmailServerConfiguration findConfigEmail(Client client) throws Exception{
    OBContext.setAdminMode(true);
    
    OBCriteria<EmailServerConfiguration> configList = OBDal.getInstance().createCriteria(
        EmailServerConfiguration.class);
    configList.add(Expression.eq(EmailServerConfiguration.PROPERTY_CLIENT,client));
    
    if(configList.list().isEmpty()){
      throw new Exception("@FET_NoConfigEmail@");
      
    }
    
    OBContext.restorePreviousMode();
    
   return configList.list().get(0);
    
    
  }
  
  
  public static MensajeCorreo findMensaje(Organization org, InforTimbrado infoTimbrado) throws Exception{
    OBContext.setAdminMode(true);
    
    OBCriteria<MensajeCorreo> configList = OBDal.getInstance().createCriteria(
        MensajeCorreo.class);
    configList.add(Expression.eq(MensajeCorreo.PROPERTY_FETINFOTIMBRADO,infoTimbrado));
    configList.add(Expression.eq(MensajeCorreo.PROPERTY_ORGANIZATION,org));

    
    if(configList.list().isEmpty()){
      return null;      
    }
    
    OBContext.restorePreviousMode();
    
   return configList.list().get(0);
    

    
  }
  
  //Es para saber si esta registrado diverza en la base de datos
  
  public static boolean registradoDiverza() throws Exception{
    
    OBContext.setAdminMode(true);
    final OBCriteria<org.openbravo.model.ad.domain.List> rateList = OBDal.getInstance().createCriteria(
        org.openbravo.model.ad.domain.List.class);
    rateList.add(Expression.eq(org.openbravo.model.ad.domain.List.PROPERTY_SEARCHKEY, "TIMDIV_TimbradoDiverza"));
    
    if(rateList.list().isEmpty()){
      return false;
    }
    
    return true;
    
  }
  
  
  
}
