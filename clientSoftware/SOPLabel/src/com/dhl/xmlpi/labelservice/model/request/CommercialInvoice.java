package com.dhl.xmlpi.labelservice.model.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.dhl.xmlpi.shipVal.models.ExportDeclaration;
import com.dhl.xmlpi.shipVal.models.ExportLineItem;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Request" type="{http://www.dhl.com/CommercialInvoice_datatypes}Request"/>
 *         &lt;element name="AirwayBillNumber" type="{http://www.dhl.com/CommercialInvoice_datatypes}AirwayBillNumber"/>
 *         &lt;element name="DHLInvoiceLanguageCode" type="{http://www.dhl.com/CommercialInvoice_datatypes}DHLInvoiceLanguageCode" minOccurs="0" default="en"/>
 *         &lt;element name="DHLInvoiceType" type="{http://www.dhl.com/CommercialInvoice_datatypes}DHLInvoiceType" default="CMI"/>
 *         &lt;element name="Origin" type="{http://www.dhl.com/CommercialInvoice_datatypes}Origin"/>
 *         &lt;element name="Destination" type="{http://www.dhl.com/CommercialInvoice_datatypes}Destination"/>
 *         &lt;element name="ExportDeclaration" type="{http://www.dhl.com/CommercialInvoice_datatypes}ExportDeclaration"/>
 *         &lt;element name="DHLLogoFlag" type="{http://www.w3.org/CommercialInvoice_datatypes}DHLLogoFlag" />
 *         &lt;element name="CustomerLogo" type="{http://www.w3.org/CommercialInvoice_datatypes}CustomerLogo" minOccurs="0" />
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request",
    "airwayBillNumber",
    "dhlInvoiceLanguageCode",
    "dhlInvoiceType",
    "termsOfTrade",
    "referenceID",
    "origin",
    "destination",
    "exportDeclaration",
    "dhlLogoFlag",
    "customerLogo",
    "declaredCurrency"
})
@XmlRootElement(name = "CommercialInvoice", namespace = "http://www.dhl.com")
public class CommercialInvoice {
	
	@XmlElement(name = "Request", required = true)
    protected Request request;
	
    @XmlElement(name = "AirwayBillNumber", required = true)
    protected String airwayBillNumber;
    
    @XmlElement(name = "DHLInvoiceLanguageCode", required = true)
    protected String dhlInvoiceLanguageCode;
    
    @XmlElement(name = "DHLInvoiceType", required = true)
    protected String dhlInvoiceType;
    
    @XmlElement(name = "TermsOfTrade")
    protected String termsOfTrade;
    
    @XmlElement(name = "ReferenceID")
    protected String referenceID;
    
    @XmlElement(name = "Origin", required = true)
    protected Origin origin;
    
    @XmlElement(name = "Destination", required = true)
    protected Destination destination;
    
    @XmlElement(name = "ExportDeclaration", required = true)
    protected ExportDeclaration exportDeclaration;
    
    @XmlElement(name = "DHLLogoFlag", required = true)
    protected DHLLogoFlag dhlLogoFlag;
    
    @XmlElement(name = "CustomerLogo")
    protected CustomerLogo customerLogo;
    
    @XmlElement(name = "DeclaredCurrency", required=true)
    protected String declaredCurrency;
	/**
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(Request request) {
		this.request = request;
	}

	/**
	 * @return the airwayBillNumber
	 */
	public String getAirwayBillNumber() {
		return airwayBillNumber;
	}

	/**
	 * @param airwayBillNumber the airwayBillNumber to set
	 */
	public void setAirwayBillNumber(String airwayBillNumber) {
		this.airwayBillNumber = airwayBillNumber;
	}

	/**
	 * @return the dhlInvoiceLanguageCode
	 */
	public String getDhlInvoiceLanguageCode() {
		return dhlInvoiceLanguageCode;
	}

	/**
	 * @param dhlInvoiceLanguageCode the dhlInvoiceLanguageCode to set
	 */
	public void setDhlInvoiceLanguageCode(String dhlInvoiceLanguageCode) {
		this.dhlInvoiceLanguageCode = dhlInvoiceLanguageCode;
	}

	/**
	 * @return the dhlInvoiceType
	 */
	public String getDhlInvoiceType() {
		return dhlInvoiceType;
	}

	/**
	 * @param dhlInvoiceType the dhlInvoiceType to set
	 */
	public void setDhlInvoiceType(String dhlInvoiceType) {
		this.dhlInvoiceType = dhlInvoiceType;
	}

	
	public String getTermsOfTrade() {
		return termsOfTrade;
	}

	public void setTermsOfTrade(String termsOfTrade) {
		this.termsOfTrade = termsOfTrade;
	}

	public String getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}
	
	/**
	 * @return the origin
	 */
	public Origin getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Origin origin) {
		this.origin = origin;
	}

	/**
	 * @return the destination
	 */
	public Destination getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * @return the exportDeclaration
	 */
	public ExportDeclaration getExportDeclaration() {
		return exportDeclaration;
	}

	/**
	 * @param exportDeclaration the exportDeclaration to set
	 */
	public void setExportDeclaration(ExportDeclaration exportDeclaration) {
		this.exportDeclaration = exportDeclaration;
	}

	/**
	 * @return the dhlLogoFlag
	 */
	public DHLLogoFlag getDhlLogoFlag() {
		return dhlLogoFlag;
	}

	/**
	 * @param dhlLogoFlag the dhlLogoFlag to set
	 */
	public void setDhlLogoFlag(DHLLogoFlag dhlLogoFlag) {
		this.dhlLogoFlag = dhlLogoFlag;
	}

	/**
	 * @return the customerLogo
	 */
	public CustomerLogo getCustomerLogo() {
		return customerLogo;
	}

	/**
	 * @param customerLogo the customerLogo to set
	 */
	public void setCustomerLogo(CustomerLogo customerLogo) {
		this.customerLogo = customerLogo;
	}

	public String getDeclaredCurrency() {
		return declaredCurrency;
	}

	public void setDeclaredCurrency(String declaredCurrency) {
		this.declaredCurrency = declaredCurrency;
	}
	
}
