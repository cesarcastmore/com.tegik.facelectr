package com.tegik.facelectr.comprobante;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.access.InvoiceLineTax;
import org.openbravo.model.common.geography.Location;
import org.openbravo.model.common.invoice.InvoiceLine;
import org.openbravo.model.common.invoice.InvoiceTax;
import org.openbravo.model.common.currency.ConversionRate;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.financialmgmt.tax.TaxRate;


import com.tegik.facelectr.utilidad.Finder;
import com.tegik.facelectr.utilidad.Util;
import com.tegik.facelectr.utilidad.Validate;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.TUbicacion;
import mx.bigdata.sat.cfdi.v32.schema.TUbicacionFiscal;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Conceptos;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Emisor;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Receptor;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Conceptos.Concepto;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Retenciones;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Traslados;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Retenciones.Retencion;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Traslados.Traslado;
import mx.bigdata.sat.cfdi.v32.schema.ObjectFactory;


public class CreateComprobanteDefecto extends CreateComprobante {
  private static final Logger log = Logger.getLogger(CreateComprobanteDefecto.class);
  
  @SuppressWarnings("unused")
  public Comprobante createComprobante() throws Exception{
    
    ObjectFactory of = new ObjectFactory();
    Comprobante comp = of.createComprobante();
    
    //Validate organizacion con datos obligatorios de la direccion fiscal
    Validate.validatLugarExpedido(invoice.getOrganization());
    Location loc = invoice.getOrganization().getOrganizationInformationList().get(0).getLocationAddress();
    
    comp.setLugarExpedicion(loc.getCityName() + ", " + loc.getRegion().getName());
    
    comp.setVersion("3.2");
    
    //El numero de factura debe de ser numerico
    //Validate.validateDocumentNo(invoice.getDocumentNo());
    
    //Obtiene el numero de serie o el numero de documento
    int i = 0;
    while (!Character.isDigit(invoice.getDocumentNo().charAt(i)))
      i++;

    if (i == invoice.getDocumentNo().length() || i == 0) {
      comp.setFolio(invoice.getDocumentNo());
    } else {
      comp.setSerie(invoice.getDocumentNo().substring(0, i));
      comp.setFolio(invoice.getDocumentNo().substring(i));
    }
    
    comp.setFecha(Util.obtenerUsoHorarioMexico());
    
    //Forma de Pago
    if (invoice.getFetFormadepago().equals("1")) {
      comp.setFormaDePago("Pago en una sola exhibición");
    } else if (invoice.getFetFormadepago().equals("2")) {
      comp.setFormaDePago("Parcialidad " + invoice.getFetParcialidad() + " de "
          + invoice.getFetParcialidadtotal());
    }
    
    
    String referencia = invoice.getBusinessPartner().getReferenceNo();
    if (referencia != null && !referencia.equals("")) {
      comp.setMetodoDePago(invoice.getPaymentMethod().getName() + " - " + referencia);
    } else {
      comp.setMetodoDePago(invoice.getPaymentMethod().getName());
    }
    
    
    BigDecimal gta = invoice.getGrandTotalAmount();
    BigDecimal sla = invoice.getSummedLineAmount();
    comp.setSubTotal(sla.abs());
    comp.setTotal(gta.abs());
    
    
    if (getTipoDoc() == "N") {
      if (gta.compareTo(new BigDecimal(0.0)) >= 0)
        comp.setTipoDeComprobante("egreso");
      else
        comp.setTipoDeComprobante("ingreso");
    } else {
      if (gta.compareTo(new BigDecimal(0.0)) >= 0)
        comp.setTipoDeComprobante("ingreso");
      else
        comp.setTipoDeComprobante("egreso");
    }
    
    comp.setMoneda(invoice.getCurrency().getISOCode());
    
    //Agregar la conversion del tipo de cambio
    List<ConversionRate> conversations = Finder.findConversionRatebyDate(invoice, new Date());  
    for (ConversionRate CurrencyRate : conversations) {
      comp.setTipoCambio(CurrencyRate.getMultipleRateBy().toString());
    }
        
    comprobanteOB.setEncabezado(invoice);
    
    return comp;
    
    
  }
  

  public Receptor createReceptor() throws Exception{
    
    Receptor receptor = new Receptor();
    
    comprobanteOB.setReceptor(invoice.getBusinessPartner());
    receptor.setNombre(invoice.getBusinessPartner().getFetRazonsocial());
    receptor.setRfc(invoice.getBusinessPartner().getTaxID());
    
    Validate.validateReceptorRFC(receptor.getRfc());
    
    //La direccion es opcional 
    if(invoice.getPartnerAddress() != null){
      TUbicacion uf = new TUbicacion();
      Location loc = invoice.getPartnerAddress().getLocationAddress();
      uf.setCalle(loc.getAddressLine1());
      uf.setCodigoPostal(loc.getPostalCode());
      uf.setColonia(loc.getAddressLine2());
      if(loc.getRegion() != null){
        uf.setEstado(loc.getRegion().getName());
      }
      uf.setMunicipio(loc.getCityName());
      uf.setNoExterior(loc.getTdirmNumex());
      uf.setLocalidad(loc.getTdirmLocalidad());
      uf.setNoInterior(loc.getTdirmNumin());
      
      uf.setPais(loc.getCountry().getName());
      receptor.setDomicilio(uf);
    }
    
    return receptor;
  }

  public Emisor createEmisor() throws Exception{
    
    Emisor emisor = new Emisor();
    
    
    Organization orgPadre = invoice.getFetOrglegal();
    Validate.validateRFCInfo(orgPadre);
    
    emisor.setNombre(orgPadre.getSocialName());
    emisor.setRfc(orgPadre.getOrganizationInformationList().get(0).getTaxID());
    
    Validate.validateEmisorRFC(emisor.getRfc());    
    
    


    //El domicilio fiscal es opcional 
    if(orgPadre.getOrganizationInformationList()!= null){
      if(!invoice.getOrganization().getOrganizationInformationList().isEmpty()){
        
        //Valida algunos campos que son obligatorios
        Validate.validateDirFiscal(orgPadre);
        
        
        Location locOrgPadre = orgPadre.getOrganizationInformationList().get(0).getLocationAddress();        
        
        TUbicacionFiscal uf = new TUbicacionFiscal();
        uf.setCalle(locOrgPadre.getAddressLine1());
        uf.setCodigoPostal(locOrgPadre.getPostalCode());
        uf.setColonia(locOrgPadre.getAddressLine2());
        uf.setEstado(locOrgPadre.getRegion().getName());
        uf.setMunicipio(locOrgPadre.getCityName());
        uf.setNoExterior(locOrgPadre.getTdirmNumex());
        uf.setNoInterior(locOrgPadre.getTdirmNumin());
        uf.setPais(locOrgPadre.getCountry().getName());
        uf.setLocalidad(locOrgPadre.getTdirmLocalidad());
        emisor.setDomicilioFiscal(uf);

      }
      
    }
    
    
    //La direccion es expendido es opcional
    if( invoice.getOrganization().getOrganizationInformationList()!= null){
      if(!invoice.getOrganization().getOrganizationInformationList().isEmpty()){
        
        Location locOrgSucursal = invoice.getOrganization().getOrganizationInformationList().get(0).getLocationAddress();

        TUbicacion domicilio = new TUbicacion();
        domicilio.setCalle(locOrgSucursal.getAddressLine1());
        domicilio.setCodigoPostal(locOrgSucursal.getPostalCode());
        domicilio.setColonia(locOrgSucursal.getAddressLine2());
        domicilio.setEstado(locOrgSucursal.getRegion().getName());
        domicilio.setMunicipio(locOrgSucursal.getCityName());
        domicilio.setNoExterior(locOrgSucursal.getTdirmNumex());
        domicilio.setNoInterior(locOrgSucursal.getTdirmNumin());
        domicilio.setPais(locOrgSucursal.getCountry().getName());
        domicilio.setLocalidad(locOrgSucursal.getTdirmLocalidad());
        
        emisor.setExpedidoEn(domicilio);
      }
    }

    
    //El regimen es obligatorio
    Comprobante.Emisor.RegimenFiscal re = new Comprobante.Emisor.RegimenFiscal();
    if (orgPadre.getOrganizationInformationList().get(0).isFetPersonamoral()) {
      re.setRegimen("Persona Moral Regimen General");
    } else {
      re.setRegimen("PERSONA FISICA");
    }
    emisor.getRegimenFiscal().add(re);
    
    comprobanteOB.setEmisor(orgPadre);
    
    return emisor;
  }
  
  
  
  public Conceptos createConceptos() {
    
    Conceptos cps =  new Conceptos();
    
    List<Concepto> listaConceptos = cps.getConcepto();
    
    for (InvoiceLine linea : invoice.getInvoiceLineList()) {
      
      //Obtiene los productos 
      if (linea.getProduct() != null || linea.getAccount() != null) {
        
        Concepto concepto = new Concepto();
        concepto.setUnidad(linea.getUOM().getName());
        concepto.setDescripcion(linea.getProduct().getDescription().trim());
        
        BigDecimal importeNeto = linea.getLineNetAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal cantidadFacturada = linea.getInvoicedQuantity().setScale(2, RoundingMode.HALF_UP);


        if (invoice.getGrandTotalAmount().compareTo(new BigDecimal(0.0)) >= 0) {
          
          concepto.setImporte(importeNeto);
          concepto.setCantidad(cantidadFacturada);
          concepto.setValorUnitario(linea.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
          
        } else {
          
          concepto.setImporte(importeNeto.negate());
          
          if (importeNeto.negate().compareTo(new BigDecimal(0.0)) >= 0) {
            
            concepto.setCantidad(cantidadFacturada.abs());
            concepto.setValorUnitario(linea.getUnitPrice().abs().setScale(2, RoundingMode.HALF_UP));
            
          } else {
            
            concepto.setCantidad(cantidadFacturada.abs().negate());
            concepto.setValorUnitario(linea.getUnitPrice().abs().setScale(2, RoundingMode.HALF_UP));
            
          }
          

        }
        
        
        /*
        String descripcionExtra = "";
        if (linea.getDescription() != null ) {
          if (!linea.getDescription().equals("CARGO POR USADO")) {
            descripcionExtra = linea.getDescription();
          }

        }

        if (linea.getProduct() != null) {
          
          if (linea.getClient().isFetUsardescripcion()) {
            concepto.setDescripcion(linea.getProduct().getDescription() + " " + descripcionExtra);
          } else {
            concepto.setDescripcion(linea.getProduct().getName() + " " + descripcionExtra);
          }

          concepto.setDescripcion(concepto.getDescripcion().trim());

          if (linea.getClient().isFetUsaridentificador()) {
            concepto.setNoIdentificacion(linea.getProduct().getSearchKey());
          }
        } else {
          concepto.setDescripcion(linea.getAccount().getName());
        }*/

        
        comprobanteOB.addConceptos(linea);
        
        listaConceptos.add(concepto);
      }
    }
    
    return cps;
    
    
  }
  
 public Impuestos createImpuestos() throws Exception {
    
    Impuestos imps = new Impuestos();
    
    Traslados traslados = new Traslados();
    BigDecimal totalTraslados = BigDecimal.ZERO;
    
    Retenciones retenciones = new Retenciones();
    BigDecimal totalRetenciones = BigDecimal.ZERO;



    for (InvoiceTax lineaImpuesto : invoice.getInvoiceTaxList()) {
      
      TaxRate tax = lineaImpuesto.getTax();
      BigDecimal taxAmount = lineaImpuesto.getTaxAmount();
      BigDecimal summedAmount =invoice.getSummedLineAmount(); 
      
      boolean trasladoPositivo = taxAmount.doubleValue() >= 0 && summedAmount.doubleValue() > 0;
      boolean transladoNegativo = taxAmount.doubleValue() <= 0 && summedAmount.doubleValue() < 0;
      
      if( !Validate.isExento(tax) && (transladoNegativo || trasladoPositivo)){
        
        Traslado t1 = new Traslado();
        Validate.validateTraslado(tax);
        
        t1.setImpuesto(tax.getFetNombrefacturacion());
        t1.setTasa(tax.getRate());
        t1.setImporte(taxAmount.abs());        
        
        totalTraslados.add(t1.getImporte());
        traslados.getTraslado().add(t1);
        
        comprobanteOB.addTraslados(lineaImpuesto);
        }
      
      boolean retencionNegativa = taxAmount.doubleValue() < 0 && summedAmount.doubleValue() > 0;
      boolean retencionPostiva = taxAmount.doubleValue() > 0 && summedAmount.doubleValue() < 0;
      
      if(!Validate.isExento(tax) && (retencionNegativa || retencionPostiva)){
        
        Retencion retencion=  new Retencion();
        Validate.validateRetencion(tax);
        
        retencion.setImporte(taxAmount.abs());
        retencion.setImpuesto(tax.getFetNombrefacturacion());
        
        totalRetenciones.add(retencion.getImporte());
        retenciones.getRetencion().add(retencion);
        
        comprobanteOB.addRetenciones(lineaImpuesto);

      }
      
    }

    if(!traslados.getTraslado().isEmpty()){      
      imps.setTraslados(traslados); 
      imps.setTotalImpuestosTrasladados(totalTraslados);
      
    } 
    
    if(!retenciones.getRetencion().isEmpty()){
      imps.setRetenciones(retenciones);
      imps.setTotalImpuestosRetenidos(totalRetenciones);
    }
    

    return imps;

  }


 


}
