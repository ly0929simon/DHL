package com.dhl.xmlpi.labelservice.errorresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "response" })
@XmlRootElement(name = "CommercialInvoiceErrorResponse", namespace = "http://www.dhl.com")
public class CommercialInvoiceErrorResponse {
	@XmlElement(name = "Response", required = true)
	protected ErrorResponse response;

	public ErrorResponse getResponse() {
		return response;
	}

	public void setResponse(ErrorResponse value) {
		this.response = value;
	}
}