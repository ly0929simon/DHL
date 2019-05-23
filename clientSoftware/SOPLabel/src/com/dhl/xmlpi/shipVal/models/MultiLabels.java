package com.dhl.xmlpi.shipVal.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.dhl.xmlpi.labelservice.model.datatypes.Piece;

/**
 * MultiLabels
 * 
 * <p>Java class for MultiLabels complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiLabels">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MultiLabel" type="{http://www.dhl.com/datatypes_global}MultiLabel" minOccurs="1" maxOccurs="99"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiLabels", propOrder = {
    "multiLabel"
})
public class MultiLabels {

	@XmlElement(name = "MultiLabel", required = true)
    protected List<MultiLabel> multiLabel;

	/**
	 * @return the multiLabel
	 */
	public List<MultiLabel> getMultiLabel() {
		return multiLabel;
	}

	/**
	 * @param multiLabel the multiLabel to set
	 */
	public void setMultiLabel(List<MultiLabel> multiLabel) {
		this.multiLabel = multiLabel;
	}
	
}
