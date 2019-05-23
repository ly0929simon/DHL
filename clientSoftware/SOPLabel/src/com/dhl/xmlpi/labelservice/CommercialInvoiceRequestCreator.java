/*
 * Copyright (c) 2012 DHL
 * 
 * All rights reserved.
 */
package com.dhl.xmlpi.labelservice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.PropertyResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.xerces.impl.dv.util.Base64;

import com.dhl.xmlpi.labelservice.exception.LabelServiceException;
import com.dhl.xmlpi.labelservice.model.request.CommercialInvoice;
import com.dhl.xmlpi.labelservice.model.request.CustomerLogo;
import com.dhl.xmlpi.labelservice.model.request.DHLLogoFlag;
import com.dhl.xmlpi.labelservice.model.request.Destination;
import com.dhl.xmlpi.labelservice.model.request.Origin;
import com.dhl.xmlpi.labelservice.model.request.Request;
import com.dhl.xmlpi.labelservice.model.request.ServiceHeaderRequest;
import com.dhl.xmlpi.shipVal.models.ShipmentResponse;

/**
 * 
 * @author Pavan
 *
 */
public class CommercialInvoiceRequestCreator {

	CommercialInvoice commercialInvoice = new CommercialInvoice();

	public String createRequestFromShipValResp(String shipValRespXmlPath,
			ShipmentResponse shipmentResponse, boolean dhlLogoFlag, boolean companyLogoFlag) throws LabelServiceException {
		createCommercialInvoice(shipmentResponse, dhlLogoFlag, companyLogoFlag);
		return marshal();
	}

	/**
	 * 
	 * @return stringWriter
	 * @throws LabelServiceException
	 */
	private String marshal() throws LabelServiceException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.dhl.xmlpi.labelservice.model.request");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setSchema(null);
			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(commercialInvoice, stringWriter);
			return stringWriter.getBuffer().toString();
		} catch (JAXBException e) {
			throw new LabelServiceException(
					"Unable to create Label Service Request for XMLPI", e);
		}
	}

	private CommercialInvoice createCommercialInvoice(ShipmentResponse shipmentResponse,
			boolean dhlLogoFlag, boolean companyLogoFlag)
					throws LabelServiceException {
		String custCompanyLogo = null;
		String imageFormat = null;
		if(companyLogoFlag) {
			PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle
					.getBundle("label");
			custCompanyLogo = new File(bundle.getString("CustCompanyLogo")).getAbsolutePath();
			imageFormat = getImageFormat(custCompanyLogo);
			custCompanyLogo = base64CompanyLogo(custCompanyLogo);
		}
				
		String decodedPwd = "";
		try{
			FileInputStream fileInputStream = new FileInputStream("config.dat");
			ObjectInputStream oInputStream = new ObjectInputStream(fileInputStream);
			Object obj = oInputStream.readObject();
			String passEncoded = (String)obj;
			byte[] bytesDecoded = Base64.decode(passEncoded);
			decodedPwd = new String(bytesDecoded);
			oInputStream.close();
		}catch (FileNotFoundException e) {
			throw new LabelServiceException(
					"Unable to create Label Service Request for XMLPI", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new LabelServiceException(
					"Unable to create Label Service Request for XMLPI", e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new LabelServiceException(
					"Unable to create Label Service Request for XMLPI", e);
		}
		
		commercialInvoice.setRequest(getRequest(shipmentResponse,decodedPwd));
		commercialInvoice.setAirwayBillNumber(shipmentResponse.getAirwayBillNumber());
		commercialInvoice.setDhlInvoiceLanguageCode(shipmentResponse.getdHLInvoiceLanguageCode());
		commercialInvoice.setDhlInvoiceType(shipmentResponse.getdHLInvoiceType());

		if (dhlLogoFlag) {
			commercialInvoice.setDhlLogoFlag(DHLLogoFlag.Y);
		} else {
			commercialInvoice.setDhlLogoFlag(DHLLogoFlag.N);
		}

		commercialInvoice.setOrigin(getOrigin(shipmentResponse));
		commercialInvoice.setDestination(getDestination(shipmentResponse));
		if (shipmentResponse.getExportDeclaration() != null) {
			commercialInvoice.setExportDeclaration(shipmentResponse.getExportDeclaration());
		}
		if (null != custCompanyLogo	&& !"".equals(custCompanyLogo)) {
			commercialInvoice.setCustomerLogo(getCustomerLogo(custCompanyLogo, imageFormat));
		}
		if (shipmentResponse.getDutiable() != null && shipmentResponse.getDutiable().getTermsOfTrade() != null) {
			commercialInvoice.setTermsOfTrade(shipmentResponse.getDutiable().getTermsOfTrade());
		}
		
		if (shipmentResponse.getReference() != null && shipmentResponse.getReference().size() > 0
				&& shipmentResponse.getReference().get(0).getReferenceID() != null) {
			commercialInvoice.setReferenceID(shipmentResponse.getReference().get(0).getReferenceID());
		}
		
		if (shipmentResponse.getDutiable() != null && shipmentResponse.getDutiable().getDeclaredCurrency() != null
				&& !"".equals(shipmentResponse.getDutiable().getDeclaredCurrency().trim())) {
			commercialInvoice.setDeclaredCurrency(shipmentResponse.getDutiable().getDeclaredCurrency());
		}
		return commercialInvoice;

	}


	/**
	 * 
	 * @param shipmentResponse
	 * @return request
	 */
	private Request getRequest(ShipmentResponse shipmentResponse, String pwd) {

		Request request = new Request();
		ServiceHeaderRequest serviceHeader = new ServiceHeaderRequest();
		request.setServiceHeader(serviceHeader);
		// serviceHeader.setMessageTime(new );
		serviceHeader.setMessageTime(getMessageTime());
		serviceHeader.setMessageReference(shipmentResponse.getResponse()
				.getServiceHeader().getMessageReference());
		serviceHeader.setSiteID(shipmentResponse.getResponse()
				.getServiceHeader().getSiteID());
		// serviceHeader.setPassword(shipmentValidateResponse.getResponse().getServiceHeader().getPassword());
		serviceHeader.setPassword(pwd);
		return request;
	}

	/**
	 * 
	 * @return messageTime
	 */
	private String getMessageTime() {
		Calendar cal = Calendar.getInstance();
		int millis = cal.getTimeZone().getRawOffset();
		String sign;
		// assign sign + or - for GMT offset
		if (millis < 0) {
			sign = "-";
			millis = millis * (-1);
		} else {
			sign = "+";
		}
		int hr = (millis / 60000) / 60;
		int min = (millis / 60000) % 60;
		String hrs = "" + hr;
		String ms = "" + min;
		if (min < 10) {
			ms = "0" + ms;
		}
		if (hr < 10) {
			hrs = "0" + hrs;
		}
		String gmtOffset = sign + hrs + ":" + ms;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String messageTime = sf.format(new java.util.Date()) + gmtOffset;
		return messageTime;
	}


	/**
	 * 
	 * @param shipmentResponse
	 * @return origin
	 */
	private Origin getOrigin(ShipmentResponse shipmentResponse) {
		Origin origin = new Origin();
		origin.setSuburb(shipmentResponse.getShipper().getSuburb());
		if (shipmentResponse.getOriginServiceArea().getServiceAreaCode() != null) {
			origin.setSvcAreaCode(shipmentResponse.getOriginServiceArea().getServiceAreaCode());
		}
		// origin.setFacilityCode("PUD");
		origin.setPostCode(shipmentResponse.getShipper().getPostalCode());
		origin.setCity(shipmentResponse.getShipper().getCity());
		origin.setCountryCode(shipmentResponse.getShipper().getCountryCode());

		String value = shipmentResponse.getShipper().getCountryName();
		if (null != value && !("".equals(value))) {
			if (value.length() > 30) {
				value = value.substring(0, 30);
			}
		}
		origin.setCountryName(value);

		origin.setAddrLine1(shipmentResponse.getShipper().getCompanyName());
		origin.setAddrLine2(shipmentResponse.getShipper().getContact().getPersonName());
		origin.setPhoneNum(shipmentResponse.getShipper().getContact().getPhoneNumber());
		List addressLineLst = shipmentResponse.getShipper().getAddressLine();
		if (addressLineLst.size() > 0) {
			for (int i = 0; i < addressLineLst.size(); i++) {
				if (i == 0) {
					origin.setAddrLine3(addressLineLst.get(i).toString());
				} else if (i == 1) {
					origin.setAddrLine4(addressLineLst.get(i).toString());
				} else if (i == 2) {
					origin.setAddrLine5(addressLineLst.get(i).toString());
				}
			}
			/*String str = "";
			if(origin.getPostCode() != null && (!origin.getPostCode().equals("") )){
				str += origin.getPostCode() + " ";
			}
			if(origin.getCity() != null && !origin.getCity().equals("")){
				str += origin.getCity() + " ";
			}
			if (null != shipmentResponse.getShipper().getDivision()) {
				str += shipmentResponse.getShipper().getDivision() + " ";
			} else if (null != shipmentResponse.getShipper().getDivisionCode()) {
				str += shipmentResponse.getShipper().getDivisionCode() + " ";
			}
			if (null != origin.getAddrLine5()
					&& (!("".equals(origin.getAddrLine5())))) {
				origin.setAddrLine6(str);
				origin.setAddrLine7(origin.getCountryName());
			} else {
				origin.setAddrLine5(str);
				origin.setAddrLine6(origin.getCountryName());
			}*/
		}

		//Division
		if (shipmentResponse.getShipper() != null && shipmentResponse.getShipper().getDivision() != null) {
			origin.setAddrLine6(shipmentResponse.getShipper().getDivision());
		}
		//ShipperID
		if (shipmentResponse.getShipper() != null && shipmentResponse.getShipper().getShipperID() != null) {
			origin.setAddrLine7(shipmentResponse.getShipper().getShipperID());
		}
		if (shipmentResponse.getShipper() != null && shipmentResponse.getShipper().getContact() != null
				&& shipmentResponse.getShipper().getContact().getPhoneExtension() != null) {
			origin.setPhoneExtension(shipmentResponse.getShipper().getContact().getPhoneExtension());
		}
		if (shipmentResponse.getShipper() != null && shipmentResponse.getShipper().getFederalTaxId() != null) {
			origin.setFederalTaxId(shipmentResponse.getShipper().getFederalTaxId());
		}
		if (shipmentResponse.getShipper() != null && shipmentResponse.getShipper().getEori_No() != null) {
			origin.setEori_No(shipmentResponse.getShipper().getEori_No());
		}
		if (shipmentResponse.getShipper() != null && shipmentResponse.getShipper().getContact() != null
				&& shipmentResponse.getShipper().getContact().getFaxNumber() != null) {
			origin.setFaxNumber(shipmentResponse.getShipper().getContact().getFaxNumber());
		}
		return origin;
	}


	/**
	 * 
	 * @param shipmentResponse
	 * @return destination
	 */
	private Destination getDestination(ShipmentResponse shipmentResponse) {
		Destination destination = new Destination();
		destination.setSuburb(shipmentResponse.getConsignee().getSuburb());
		destination.setSvcAreaCode(shipmentResponse.getDestinationServiceArea().getServiceAreaCode());
		destination.setFacilityCode(shipmentResponse.getDestinationServiceArea().getFacilityCode());
		destination.setPostCode(shipmentResponse.getConsignee().getPostalCode());
		destination.setCity(shipmentResponse.getConsignee().getCity());
		destination.setCountryCode(shipmentResponse.getConsignee().getCountryCode());

		if (null != shipmentResponse.getConsignee().getContact().getPhoneNumber()) {
			destination.setPhoneNum(shipmentResponse.getConsignee().getContact().getPhoneNumber());
		}

		String value = shipmentResponse.getConsignee().getCountryName();
		if (null != value && !("".equals(value))) {
			if (value.length() > 30) {
				value = value.substring(0, 30);
			}
		}
		destination.setCountryName(value);
		destination.setAddrLine1(shipmentResponse.getConsignee().getCompanyName());
		destination.setAddrLine2(shipmentResponse.getConsignee().getContact().getPersonName());
		List addressLineLst = shipmentResponse.getConsignee().getAddressLine();

		if (addressLineLst.size() > 0) {
			for (int i = 0; i < addressLineLst.size(); i++) {
				if (i == 0) {
					destination.setAddrLine3(addressLineLst.get(i).toString());
				} else if (i == 1) {
					destination.setAddrLine4(addressLineLst.get(i).toString());
				} else if (i == 2) {
					destination.setAddrLine5(addressLineLst.get(i).toString());
				}
			}
			/*String str = "";
			if(destination.getPostCode() != null && (!destination.getPostCode().equals("") )){
				str += destination.getPostCode() + " ";
			}
			if(destination.getCity() != null && !destination.getCity().equals("")){
				str += destination.getCity() + " ";
			}
			if (null != shipmentResponse.getConsignee().getDivision()) {
				str += shipmentResponse.getConsignee().getDivision() + " ";
			} else if (null != shipmentResponse.getConsignee()
					.getDivisionCode()) {
				str += shipmentResponse.getConsignee().getDivisionCode() + " ";
			}
			if (null != destination.getAddrLine5()
					&& (!("".equals(destination)))) {
				destination.setAddrLine6(str);
				destination.setAddrLine7(destination.getCountryName());
			} else {
				destination.setAddrLine5(str);
				destination.setAddrLine6(destination.getCountryName());
			}*/
		}
		
		if (shipmentResponse.getConsignee() != null && shipmentResponse.getConsignee().getDivision() != null) {
			destination.setAddrLine6(shipmentResponse.getConsignee().getDivision());
		}
		if (shipmentResponse.getConsignee() != null && shipmentResponse.getConsignee().getContact() != null
				&& shipmentResponse.getConsignee().getContact().getPhoneExtension() != null) {
			destination.setPhoneExtension(shipmentResponse.getConsignee().getContact().getPhoneExtension());
		}
		if (shipmentResponse.getConsignee() != null && shipmentResponse.getConsignee().getContact() != null
				&& shipmentResponse.getConsignee().getContact().getEmail() != null) {
			destination.setEmail(shipmentResponse.getConsignee().getContact().getEmail());
		}
		if (shipmentResponse.getConsignee() != null && shipmentResponse.getConsignee().getFederalTaxId() != null) {
			destination.setFederalTaxId(shipmentResponse.getConsignee().getFederalTaxId());
		}
		if (shipmentResponse.getConsignee() != null && shipmentResponse.getConsignee().getContact() != null
				&& shipmentResponse.getConsignee().getContact().getFaxNumber() != null) {
			destination.setFaxNumber(shipmentResponse.getConsignee().getContact().getFaxNumber());
		}
		return destination;
	}


	/**
	 * This method is to populate the customerLogo with logo and imageformat
	 * @param custCompanyLogo String
	 * @param logoImageFormat String
	 * @return CustomerLogo
	 */
	private CustomerLogo getCustomerLogo(String custCompanyLogo, String logoImageFormat) {
		CustomerLogo customerLogo = new CustomerLogo();
		customerLogo.setLogoImage(custCompanyLogo);
		customerLogo.setLogoImageFormat(logoImageFormat);
		return customerLogo;
	}
	
	/**
	 * Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	 * This method is to get the image format of a file (i.e., file extension. EG: PNG, JPEG.)
	 * @param custCompanyLogo String
	 * @return String
	 * @throws LabelServiceException
	 */
	private String getImageFormat(String custCompanyLogo)
			throws LabelServiceException {
		String companyLogoFileName;
		String fileExtension;
		
		if(custCompanyLogo.lastIndexOf("\\") >0){
			companyLogoFileName = custCompanyLogo.substring(custCompanyLogo.lastIndexOf("\\")+1);
		}
		else if(custCompanyLogo.lastIndexOf("/")>0) {
			companyLogoFileName = custCompanyLogo.substring(custCompanyLogo.lastIndexOf("/")+1);	
		}else{
			companyLogoFileName = custCompanyLogo;
		}
		
		int index = companyLogoFileName.indexOf(".");
		int length = companyLogoFileName.length();
		
		fileExtension = companyLogoFileName.substring(index+1, length);
		
		return fileExtension.toUpperCase();
	}

	/**
	 * Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	 * The below method is to transfer from company logo image into base64 format.
	 * 
	 * @throws LabelServiceException
	 */
	private String base64CompanyLogo(String custCompanyLogo) throws LabelServiceException {
		
		byte[] buffer = null;
		String decodedCompanyLogo = null;
		
		try {
			File file = new File(custCompanyLogo);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			buffer = new byte[bytes];
			bis.read(buffer);
			bis.close();
		} catch (IOException e) {
			throw new LabelServiceException(
					"CompanyLogo image file is not exist in the images folder. Please check the image file exist.", e);
		}
		
		if(buffer != null) {
			decodedCompanyLogo = Base64.encode(buffer);
		}
		return decodedCompanyLogo;
	}
	

}