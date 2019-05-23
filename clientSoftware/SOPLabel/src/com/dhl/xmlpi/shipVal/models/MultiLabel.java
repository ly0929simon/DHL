package com.dhl.xmlpi.shipVal.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * MultiLabel
 * 
 * <p>Java class for MultiLabel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiLabel">
 *         	&lt;element name="DocName" type="{http://www.dhl.com/datatypes_global}DocName"/>
 *         	&lt;element name="DocFormat" type="{http://www.dhl.com/datatypes_global}DocFormat"/>
 *         	&lt;element name="DocImage" type="{http://www.dhl.com/datatypes_global}DocImageVal"/>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiLabel", propOrder = {
    "docName",
    "docFormat",
    "docImageVal"
})
public class MultiLabel {

	@XmlElement(name = "DocName", required = true)
    protected String docName;
    @XmlElement(name = "DocFormat", required = true)
    protected String docFormat;
    @XmlElement(name = "DocImageVal", required = true)
    protected byte[] docImageVal;
	/**
	 * @return the docName
	 */
	public String getDocName() {
		return docName;
	}
	/**
	 * @param docName the docName to set
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}
	/**
	/**
	 * @return the docFormat
	 */
	public String getDocFormat() {
		return docFormat;
	}
	/**
	 * @param docFormat the docFormat to set
	 */
	public void setDocFormat(String docFormat) {
		this.docFormat = docFormat;
	}
	/**
	 * @return the docImageVal
	 */
	public byte[] getDocImageVal() {
		return docImageVal;
	}
	/**
	 * @param docImageVal the docImageVal to set
	 */
	public void setDocImageVal(byte[] docImageVal) {
		this.docImageVal = docImageVal;
	}
}
