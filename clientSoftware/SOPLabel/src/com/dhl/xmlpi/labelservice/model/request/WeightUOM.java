//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.01 at 04:46:27 PM IST 
//


package com.dhl.xmlpi.labelservice.model.request;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeightUOM.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WeightUOM">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;minLength value="1"/>
 *     &lt;maxLength value="3"/>
 *     &lt;enumeration value="Kgs"/>
 *     &lt;enumeration value="Lbs"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WeightUOM")
@XmlEnum
public enum WeightUOM {

    @XmlEnumValue("Kg")
    KGS("Kg"),
    @XmlEnumValue("Lbs")
    LBS("Lbs");
    private final String value;

    WeightUOM(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeightUOM fromValue(String v) {
        for (WeightUOM c: WeightUOM.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
