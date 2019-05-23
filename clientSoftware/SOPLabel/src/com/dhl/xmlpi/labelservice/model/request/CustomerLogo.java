package com.dhl.xmlpi.labelservice.model.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * CustomerLogo
 * 
 * <p>Java class for CustomerLogo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerLogo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LogoImage" type="{http://www.dhl.com/LabelService_datatypes}LogoImage"/>
 *         &lt;element name="LogoImageFormat" type="{http://www.dhl.com/LabelService_datatypes}LogoImageFormat"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerLogo", propOrder = {
    "logoImage",
    "logoImageFormat",
})
public class CustomerLogo {
	
	@XmlElement(name = "LogoImage", required = true)
	protected String logoImage;
	
	@XmlElement(name = "LogoImageFormat", required = true)
	protected String logoImageFormat;


	/**
	 * @return the logoImage
	 */
	public String getLogoImage() {
		return logoImage;
	}

	/**
	 * @param logoImage the logoImage to set
	 */
	public void setLogoImage(String logoImage) {
		this.logoImage = logoImage;
	}

	/**
	 * @return the logoImageFormat
	 */
	public String getLogoImageFormat() {
		return logoImageFormat;
	}

	/**
	 * @param logoImageFormat the logoImageFormat to set
	 */
	public void setLogoImageFormat(String logoImageFormat) {
		this.logoImageFormat = logoImageFormat;
	}

}
