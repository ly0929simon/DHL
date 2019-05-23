//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.01 at 04:46:27 PM IST 
//


package com.dhl.xmlpi.labelservice.model.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Generic response header
 * 
 * <p>Java class for ErrorResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ErrorResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceHeader" type="{http://www.dhl.com/LabelService_datatypes}ServiceHeaderError"/>
 *         &lt;element name="Status" type="{http://www.dhl.com/LabelService_datatypes}Status"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorResponse", propOrder = {
    "serviceHeader",
    "status"
})
public class ErrorResponse {

    @XmlElement(name = "ServiceHeader", required = true)
    protected ServiceHeaderError serviceHeader;
    @XmlElement(name = "Status", required = true)
    protected Status status;

    /**
     * Gets the value of the serviceHeader property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceHeaderError }
     *     
     */
    public ServiceHeaderError getServiceHeader() {
        return serviceHeader;
    }

    /**
     * Sets the value of the serviceHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceHeaderError }
     *     
     */
    public void setServiceHeader(ServiceHeaderError value) {
        this.serviceHeader = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Status }
     *     
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Status }
     *     
     */
    public void setStatus(Status value) {
        this.status = value;
    }

}
