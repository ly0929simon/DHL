package com.dhl.xmlpi.labelservice.model.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "response", "note", "airwayBillNumber", "labelPrintCommands" })
@XmlRootElement(name = "CommercialInvoiceResponse", namespace = "http://www.dhl.com")
public class CommercialInvoiceResponse {

	@XmlElement(name = "Response", required = true)
	protected Response response;
	@XmlElement(name = "Note")
	protected Note note;
	@XmlElement(name = "AirwayBillNumber", required = true)
	protected String airwayBillNumber;
	@XmlElement(name = "LabelPrintCommands")
	protected List<LabelPrintCommands> labelPrintCommands;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response value) {
		this.response = value;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note value) {
		this.note = value;
	}

	public String getAirwayBillNumber() {
		return airwayBillNumber;
	}

	public void setAirwayBillNumber(String value) {
		this.airwayBillNumber = value;
	}

	public List<LabelPrintCommands> getLabelPrintCommands() {
		if (labelPrintCommands == null) {
			labelPrintCommands = new ArrayList<LabelPrintCommands>();
		}
		return this.labelPrintCommands;
	}

}
