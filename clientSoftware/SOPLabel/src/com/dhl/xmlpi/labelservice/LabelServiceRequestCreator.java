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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.PropertyResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.xerces.impl.dv.util.Base64;

import com.dhl.xmlpi.labelservice.exception.LabelServiceException;
import com.dhl.xmlpi.labelservice.model.request.AddrMapFlag;
import com.dhl.xmlpi.labelservice.model.request.ArchiveLabelTemplate;
import com.dhl.xmlpi.labelservice.model.request.CustomerLogo;
import com.dhl.xmlpi.labelservice.model.request.DHLLogoFlag;
import com.dhl.xmlpi.labelservice.model.request.Destination;
import com.dhl.xmlpi.labelservice.model.request.Filing;
import com.dhl.xmlpi.labelservice.model.request.InverseVideoInd;
import com.dhl.xmlpi.labelservice.model.request.Label;
import com.dhl.xmlpi.labelservice.model.request.LabelLayout;
import com.dhl.xmlpi.labelservice.model.request.LabelService;
import com.dhl.xmlpi.labelservice.model.request.LabelTemplate;
import com.dhl.xmlpi.labelservice.model.request.LabelType;
import com.dhl.xmlpi.labelservice.model.request.Labels;
import com.dhl.xmlpi.labelservice.model.request.LookUpFlag;
import com.dhl.xmlpi.labelservice.model.request.Origin;
import com.dhl.xmlpi.labelservice.model.request.PaymentCode;
import com.dhl.xmlpi.labelservice.model.request.Piece;
import com.dhl.xmlpi.labelservice.model.request.Pieces;
import com.dhl.xmlpi.labelservice.model.request.Request;
import com.dhl.xmlpi.labelservice.model.request.Service;
import com.dhl.xmlpi.labelservice.model.request.ServiceHeaderRequest;
import com.dhl.xmlpi.labelservice.model.request.Services;
import com.dhl.xmlpi.labelservice.model.request.Shipment;
import com.dhl.xmlpi.labelservice.model.request.WeightUOM;
import com.dhl.xmlpi.shipVal.models.Reference;
import com.dhl.xmlpi.shipVal.models.ShipValResponsePiece;
import com.dhl.xmlpi.shipVal.models.ShipmentPaymentType;
import com.dhl.xmlpi.shipVal.models.ShipmentResponse;
import com.dhl.xmlpi.shipVal.models.ShipmentValidateResponse;
import com.dhl.xmlpi.shipVal.models.SpecialService;

/**
 * @author vaibhav_puniani
 * 
 */
public class LabelServiceRequestCreator {

	LabelService labelService = new LabelService();
	
	//Updated the below with labelTemplate, dhlLogoFlag and companyLogoFlag parameters
	//Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	public String createRequestFromShipValResp(String shipValRespXmlPath,
			String labelFormat, List<LabelLayout> labelLayouts,
			ShipmentValidateResponse shipmentValidateResponse, String resolution, String labelTemplate, 
			boolean dhlLogoFlag, boolean companyLogoFlag, String hideAccountFlag)
					throws LabelServiceException {
		createLabelService(shipmentValidateResponse, labelFormat, labelLayouts, resolution, labelTemplate, dhlLogoFlag, companyLogoFlag, hideAccountFlag);
		return marshal();
	}

	/**
	 * Updated the below with labelTemplate, dhlLogoFlag and companyLogoFlag parameters
	 * Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	 * @param shipValRespXmlPath
	 * @param labelFormat
	 * @param labelLayouts
	 * @param shipmentResponse
	 * @return labelService
	 * @throws LabelServiceException
	 */
	public String createRequestFromShipValResp(String shipValRespXmlPath,
			String labelFormat, List<LabelLayout> labelLayouts,
			ShipmentResponse shipmentResponse,String resolution, String labelTemplate, 
			boolean dhlLogoFlag, boolean companyLogoFlag, String hideAccountFlag) throws LabelServiceException {
		createLabelService(shipmentResponse, labelFormat, labelLayouts, resolution, labelTemplate, dhlLogoFlag, companyLogoFlag, hideAccountFlag);
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
			 //BEGIN :: Ravi Rastogi (TechMahindra) | 22-AUG-2013 | XMLPI 4.7 | UTF-8 changes
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			 //END
			marshaller.setSchema(null);
			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(labelService, stringWriter);
			return stringWriter.getBuffer().toString();
		} catch (JAXBException e) {
			throw new LabelServiceException(
					"Unable to create Label Service Request for XMLPI", e);
		}
	}

	/**
	 * 
	 * @param shipmentValidateResponse
	 * @param labelFormat
	 * @param labelLayouts
	 * @return labelService
	 * @throws LabelServiceException
	 */
	private LabelService createLabelService(
			ShipmentValidateResponse shipmentValidateResponse,
			String labelFormat, List<LabelLayout> labelLayouts,String resolution, String labelTemplate, 
			boolean dhlLogoFlag, boolean companyLogoFlag, String hideAccountFlag)
					throws LabelServiceException {
		
		//BEGIN :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
		String custCompanyLogo = null;
		String imageFormat = null;
		if(companyLogoFlag) {
			
			PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle
					.getBundle("label");
			custCompanyLogo = new File(bundle.getString("CustCompanyLogo")).getAbsolutePath();
			imageFormat = getImageFormat(custCompanyLogo);
			custCompanyLogo = base64CompanyLogo(custCompanyLogo);
		}
		// END
		
		//String encodedPwd = bundle.getString("CONFIG_FILE_LOC");
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
		// create the labelservice
		labelService.setRequest(getRequest(shipmentValidateResponse,decodedPwd));
		labelService.setLabelType(LabelType.fromValue(labelFormat));
		labelService.setAirwayBillNumber(shipmentValidateResponse
				.getAirwayBillNumber());
		
		if ("PDF".equals(labelFormat)) {
			labelService.setDHLLogoFlag(DHLLogoFlag.Y);
		} else {
			//BEGIN :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
			//Updated the below dhlLogoFlag condition getting the dhlLogoFlag from UI instead of property driven
			if (dhlLogoFlag) {
				labelService.setDHLLogoFlag(DHLLogoFlag.Y);
			} else {
				labelService.setDHLLogoFlag(DHLLogoFlag.N);
			}
		}

		labelService.setLookUpFlag(LookUpFlag.Y);
		labelService.setAddrMapFlag(AddrMapFlag.N);
		labelService.setLabels(getLabel(labelFormat, labelLayouts, labelTemplate, null));
		labelService.setOrigin(getOrigin(shipmentValidateResponse));
		labelService.setDestination(getDestination(shipmentValidateResponse));
		labelService.setShipment(getShipment(shipmentValidateResponse, hideAccountFlag));
		labelService.setPieces(getPieces(shipmentValidateResponse));
		labelService.setServices(getServices(shipmentValidateResponse));

		// Filing is not required for Region specific response
		//		labelService.setFiling(getFiling(shipmentValidateResponse));
		if(null!=shipmentValidateResponse.getInsuredAmount() 
				&& !"".equals(shipmentValidateResponse.getInsuredAmount())){
			labelService.setInsuredAmount(shipmentValidateResponse.getInsuredAmount() +" "+ shipmentValidateResponse.getCurrencyCode());	
		}
		 //BEGIN :: Ravi Rastogi (MSAT) | 24-JUL-2013 | XMLPI 4.7 | Resolution option
		labelService.setResolution(resolution);
		//END
		
		//Added below for customer logo :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
		if (null != custCompanyLogo	&& !"".equals(custCompanyLogo)) {
			labelService.setCustomerLogo(getCustomerLogo(custCompanyLogo, imageFormat));
		}
		
		labelService.setLabelTemplateType(labelTemplate);
		
		return labelService;

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

	/**
	 * 
	 * @param shipmentResponse
	 * @param labelFormat
	 * @param labelLayouts
	 * @return labelService
	 * @throws LabelServiceException
	 */
	private LabelService createLabelService(ShipmentResponse shipmentResponse,
			String labelFormat, List<LabelLayout> labelLayouts,String resolution, String labelTemplate, 
			boolean dhlLogoFlag, boolean companyLogoFlag, String hideAccountFlag)
					throws LabelServiceException {
		
		//BEGIN :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
		String custCompanyLogo = null;
		String imageFormat = null;
		if(companyLogoFlag) {
			
			PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle
					.getBundle("label");
			custCompanyLogo = new File(bundle.getString("CustCompanyLogo")).getAbsolutePath();
			imageFormat = getImageFormat(custCompanyLogo);
			custCompanyLogo = base64CompanyLogo(custCompanyLogo);
		}
		// END
				
		//String encodedPwd = bundle.getString("CONFIG_FILE_LOC");
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
		// create the labelservice
		labelService.setRequest(getRequest(shipmentResponse,decodedPwd));
		labelService.setLabelType(LabelType.fromValue(labelFormat));
		labelService
		.setAirwayBillNumber(shipmentResponse.getAirwayBillNumber());

		if ("PDF".equals(labelFormat)) {
			labelService.setDHLLogoFlag(DHLLogoFlag.Y);
		} else {
			//BEGIN :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
			//Updated the below dhlLogoFlag condition getting the dhlLogoFlag from UI instead of property driven
			if (dhlLogoFlag) {
				labelService.setDHLLogoFlag(DHLLogoFlag.Y);
			} else {
				labelService.setDHLLogoFlag(DHLLogoFlag.N);
			}
		}

		labelService.setLookUpFlag(LookUpFlag.Y);
		labelService.setAddrMapFlag(AddrMapFlag.N);
		// labelService.setCertID("CertID");
		// labelService.setShipment();
		labelService.setLabels(getLabel(labelFormat, labelLayouts, labelTemplate, shipmentResponse));
		labelService.setOrigin(getOrigin(shipmentResponse));
		labelService.setDestination(getDestination(shipmentResponse));
		labelService.setShipment(getShipment(shipmentResponse, hideAccountFlag));
		labelService.setPieces(getPieces(shipmentResponse));
		labelService.setServices(getServices(shipmentResponse));
		labelService.setDangerousGoods(shipmentResponse.getDangerousGoods());

		if(null!=shipmentResponse.getShipper().getCountryCode() 
				&& "US".equalsIgnoreCase(shipmentResponse.getShipper().getCountryCode()))
		{
			labelService.setFiling(getFiling(shipmentResponse));
		}
		if(null!=shipmentResponse.getInsuredAmount() 
				&& !"".equals(shipmentResponse.getInsuredAmount())){
			labelService.setInsuredAmount(shipmentResponse.getInsuredAmount() +" "+ shipmentResponse.getCurrencyCode());	
		}

		// labelService.setHandlings(getHandlings(shipmentValidateResponse));
		
		//BEGIN :: Ravi Rastogi (MSAT) | 24-JUL-2013 | XMLPI 4.7 | Resolution option
		labelService.setResolution(resolution);
		//END
		
		//Added below for customer logo :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
		if (null != custCompanyLogo	&& !"".equals(custCompanyLogo)) {
			labelService.setCustomerLogo(getCustomerLogo(custCompanyLogo, imageFormat));
		}
		
		labelService.setLabelTemplateType(labelTemplate);
				
		return labelService;

	}

	private Filing getFiling(ShipmentResponse shipmentResponse) {

		Filing filing= new Filing();
		if(shipmentResponse.getDutiable() != null && shipmentResponse.getDutiable().getFiling() != null )
		{
			filing.setFTSR(shipmentResponse.getDutiable().getFiling().getFTSR());
			filing.setITN(shipmentResponse.getDutiable().getFiling().getITN());
			
			if ((shipmentResponse.getDutiable().getFiling().getFilingType() != null) && 
					(!"".equals(shipmentResponse.getDutiable().getFiling().getFilingType())) && ("AES4".equalsIgnoreCase(shipmentResponse.getDutiable().getFiling().getFilingType().value())) && 
			        (shipmentResponse.getDutiable().getFiling().getAES4EIN() != null) && (!"".equals(shipmentResponse.getDutiable().getFiling().getAES4EIN()))) {
				filing.setFilingType(com.dhl.xmlpi.labelservice.model.request.FilingType.AES_4);
			    filing.setAES4EIN(shipmentResponse.getDutiable().getFiling().getAES4EIN());
			}
		}

		return filing;
	}

	//Commented below code as it is not required to getFiling for Region specific response
//	private Filing getFiling(ShipmentValidateResponse shipmentValidateResponse) {
//
//		Filing filing= new Filing();
//		if(shipmentValidateResponse.getDutiable().getFiling() != null )
//		{
//			filing.setFTSR(shipmentValidateResponse.getDutiable().getFiling().getFTSR());
//			filing.setITN(shipmentValidateResponse.getDutiable().getFiling().getITN());
//		}
//
//		return filing;
//	}

	/**
	 * 
	 * @param shipmentValidateResponse
	 * @return services
	 */
	private Services getServices(
			ShipmentValidateResponse shipmentValidateResponse) {

		Services services = new Services();
		List serviceLst = shipmentValidateResponse.getSpecialService();
		if (serviceLst.size() > 0) {
			for (int i = 0; i < serviceLst.size(); ++i) {
				Service service = new Service();
				SpecialService obj = (SpecialService) serviceLst.get(i);
				service.setLocalSvcCode("");
				service.setGlobalSvcCode(obj.getSpecialServiceType());
				service.setGlobalSvcName(obj.getSpecialServiceDesc());
				services.getService().add(service);

			}
		}
		return services;
	}

	/**
	 * 
	 * @param shipmentResponse
	 * @return services
	 */
	private Services getServices(ShipmentResponse shipmentResponse) {

		Services services = new Services();
		List serviceLst = shipmentResponse.getSpecialService();
		if (serviceLst.size() > 0) {
			for (int i = 0; i < serviceLst.size(); ++i) {
				Service service = new Service();
				SpecialService obj = (SpecialService) serviceLst.get(i);
				//if ("WY".equals(obj.getSpecialServiceType())) {

				service.setLocalSvcCode("");
				service.setGlobalSvcCode(obj.getSpecialServiceType());
				service.setGlobalSvcName(obj.getSpecialServiceDesc());
				services.getService().add(service);
				//} 
			}
		}
		return services;
	}


	/**
	 * 
	 * @param labelFormat
	 *            labelFormat
	 * @param labelLayouts
	 * @param shipmentResponse TODO
	 * @return Labels
	 */
	private Labels getLabel(String labelFormat, List<LabelLayout> labelLayouts, String labelTemplate, ShipmentResponse shipmentResponse) {

		//Commented for this release as suggested by Mary - 24thOct,2012
		// PropertyResourceBundle bundle = (PropertyResourceBundle)
		// PropertyResourceBundle .getBundle("label"); 
		//String labelTemplate = bundle.getString("LABEL_TEMPLATE");

		Labels labels = new Labels();
		for (LabelLayout labelLayout : labelLayouts) {
			Label label = new Label();
			label.setLabelLayout(labelLayout);
			if (shipmentResponse != null && shipmentResponse.getLabel() != null) {
				if (shipmentResponse.getLabel().getCustomerBarcodeType() != null) {
					label.setCustomerBarcodeType(shipmentResponse.getLabel().getCustomerBarcodeType());
				}
				if (shipmentResponse.getLabel().getCustomerBarcodeCode() != null) {
					label.setCustomerBarcodeCode(shipmentResponse.getLabel().getCustomerBarcodeCode());
				}
				if (shipmentResponse.getLabel().getCustomerBarcodeText() != null) {
					label.setCustomerBarcodeText(shipmentResponse.getLabel().getCustomerBarcodeText());
				}
			}

			if (LabelLayout.ECOM.equals(labelLayout)) {
				label.setLabelTemplate(LabelTemplate.valueOf("ECOM_"+labelTemplate));
			} else if (LabelLayout.ARCH.equals(labelLayout)) {
				label.setArchiveLabelTemplate(ArchiveLabelTemplate.valueOf("ARCH_"+labelTemplate));
			}
			labels.getLabel().add(label);
		}

		return labels;
	}

	/**
	 * 
	 * @param shipmentValidateResponse
	 * @return request
	 */
	private Request getRequest(ShipmentValidateResponse shipmentValidateResponse, String pwd) {

		Request request = new Request();
		ServiceHeaderRequest serviceHeader = new ServiceHeaderRequest();
		request.setServiceHeader(serviceHeader);
		// serviceHeader.setMessageTime(new );
		serviceHeader.setMessageTime(getMessageTime());
		serviceHeader.setMessageReference(shipmentValidateResponse
				.getResponse().getServiceHeader().getMessageReference());
		serviceHeader.setSiteID(shipmentValidateResponse.getResponse()
				.getServiceHeader().getSiteID());
		// serviceHeader.setPassword(shipmentValidateResponse.getResponse().getServiceHeader().getPassword());
		serviceHeader.setPassword(pwd);
		return request;
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
	 * @param shipmentValidateResponse
	 * @return origin
	 */
	private Origin getOrigin(ShipmentValidateResponse shipmentValidateResponse) {
		Origin origin = new Origin();
		origin.setSuburb(shipmentValidateResponse.getShipper().getDivision());
		if (shipmentValidateResponse.getOriginServiceArea()
				.getServiceAreaCode() != null) {
			origin.setSvcAreaCode(shipmentValidateResponse
					.getOriginServiceArea().getServiceAreaCode());
		}
		// origin.setFacilityCode("PUD");
		origin.setPostCode(shipmentValidateResponse.getShipper()
				.getPostalCode());
		origin.setCity(shipmentValidateResponse.getShipper().getCity());
		origin.setCountryCode(shipmentValidateResponse.getShipper()
				.getCountryCode());
		String value = shipmentValidateResponse.getShipper().getCountryName();
		if (null != value && !("".equals(value))) {
			if (value.length() > 30) {
				value = value.substring(0, 30);
			}
		}
		origin.setCountryName(value);

		origin.setAddrLine1(shipmentValidateResponse.getShipper()
				.getCompanyName());
		origin.setAddrLine2(shipmentValidateResponse.getShipper().getContact()
				.getPersonName());
		
		//Pavan Kumar (TechMahindra) | 02-SEP-2013 | XMLPI 4.7 | Label Enhancement
		origin.setPhoneNum(shipmentValidateResponse.getShipper().getContact()
				.getPhoneNumber());
		//Pavan Kumar (TechMahindra) | 02-SEP-2013 | XMLPI 4.7 | Label Enhancement
		
		List addressLineLst = shipmentValidateResponse.getShipper()
				.getAddressLine();
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
			String str = "";
			if(origin.getPostCode() != null && (!origin.getPostCode().equals("") )){
				str += origin.getPostCode()+ " ";
			}
			if(origin.getCity() != null && !origin.getCity().equals("")){
				str += origin.getCity() + " ";
			}
			if (null != shipmentValidateResponse.getShipper().getDivision()) {
				str += shipmentValidateResponse.getShipper().getDivision()
						+ " ";
			} else if (null != shipmentValidateResponse.getShipper()
					.getDivisionCode()) {
				str += shipmentValidateResponse.getShipper().getDivisionCode()
						+ " ";
			}
			if (null != origin.getAddrLine5()
					&& (!("".equals(origin.getAddrLine5())))) {
				origin.setAddrLine6(str);
				origin.setAddrLine7(origin.getCountryName());
			} else {
				origin.setAddrLine5(str);
				origin.setAddrLine6(origin.getCountryName());
			}
		}
		return origin;
	}

	/**
	 * 
	 * @param shipmentResponse
	 * @return origin
	 */
	private Origin getOrigin(ShipmentResponse shipmentResponse) {
		Origin origin = new Origin();
		origin.setSuburb(shipmentResponse.getShipper().getDivision());
		if (shipmentResponse.getOriginServiceArea().getServiceAreaCode() != null) {
			origin.setSvcAreaCode(shipmentResponse.getOriginServiceArea()
					.getServiceAreaCode());
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
		origin.setAddrLine2(shipmentResponse.getShipper().getContact()
				.getPersonName());
		//Pavan Kumar (TechMahindra) | 02-SEP-2013 | XMLPI 4.7 | Label Enhancement
		origin.setPhoneNum(shipmentResponse.getShipper().getContact()
				.getPhoneNumber());
		//Pavan Kumar (TechMahindra) | 02-SEP-2013 | XMLPI 4.7 | Label Enhancement
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
			String str = "";
			if(origin.getPostCode() != null && (!origin.getPostCode().equals("") )){
				str += origin.getPostCode() + " ";
			}
			if(origin.getCity() != null && !origin.getCity().equals("")){
				str += origin.getCity() + " ";
			}
			if(shipmentResponse.getShipper().getSuburb() != null && !shipmentResponse.getShipper().getSuburb().equals("")){
				str += shipmentResponse.getShipper().getSuburb() + " ";
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
			}
		}
		return origin;
	}

	/**
	 * 
	 * @param shipmentValidateResponse
	 * @return destination
	 */
	private Destination getDestination(
			ShipmentValidateResponse shipmentValidateResponse) {
		Destination destination = new Destination();
		destination.setSuburb(shipmentValidateResponse.getConsignee()
				.getDivision());
		destination.setSvcAreaCode(shipmentValidateResponse
				.getDestinationServiceArea().getServiceAreaCode());
		destination.setFacilityCode(shipmentValidateResponse
				.getDestinationServiceArea().getFacilityCode());
		destination.setPostCode(shipmentValidateResponse.getConsignee()
				.getPostalCode());
		destination.setCity(shipmentValidateResponse.getConsignee().getCity());
		destination.setCountryCode(shipmentValidateResponse.getConsignee()
				.getCountryCode());

		if (null != shipmentValidateResponse.getConsignee().getContact()
				.getPhoneNumber()) {
			destination.setPhoneNum(shipmentValidateResponse.getConsignee().getContact()
					.getPhoneNumber());
		}

		String value = shipmentValidateResponse.getConsignee().getCountryName();
		if (null != value && !("".equals(value))) {
			if (value.length() > 30) {
				value = value.substring(0, 30);
			}
		}
		destination.setCountryName(value);

		destination.setAddrLine1(shipmentValidateResponse.getConsignee()
				.getCompanyName());
		destination.setAddrLine2(shipmentValidateResponse.getConsignee()
				.getContact().getPersonName());
		List addressLineLst = shipmentValidateResponse.getConsignee()
				.getAddressLine();
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
			String str = "";
			if(destination.getPostCode() != null && (!destination.getPostCode().equals("") )){
				str += destination.getPostCode() + " ";
			}
			if(destination.getCity() != null && !destination.getCity().equals("")){
				str += destination.getCity() + " ";
			}
			if (null != shipmentValidateResponse.getConsignee().getDivision()) {
				str += shipmentValidateResponse.getConsignee().getDivision()
						+ " ";
			} else if (null != shipmentValidateResponse.getConsignee()
					.getDivisionCode()) {
				str += shipmentValidateResponse.getConsignee()
						.getDivisionCode() + " ";
			}


			if (null != destination.getAddrLine5()
					&& (!("".equals(destination)))) {
				destination.setAddrLine6(str);
				destination.setAddrLine7(destination.getCountryName());
			} else {
				destination.setAddrLine5(str);
				destination.setAddrLine6(destination.getCountryName());
			}
		}
		return destination;
	}

	/**
	 * 
	 * @param shipmentResponse
	 * @return destination
	 */
	private Destination getDestination(ShipmentResponse shipmentResponse) {
		Destination destination = new Destination();
		destination.setSuburb(shipmentResponse.getConsignee().getDivision());
		destination.setSvcAreaCode(shipmentResponse.getDestinationServiceArea()
				.getServiceAreaCode());
		destination.setFacilityCode(shipmentResponse
				.getDestinationServiceArea().getFacilityCode());
		destination
		.setPostCode(shipmentResponse.getConsignee().getPostalCode());
		destination.setCity(shipmentResponse.getConsignee().getCity());
		destination.setCountryCode(shipmentResponse.getConsignee()
				.getCountryCode());

		if (null != shipmentResponse.getConsignee().getContact()
				.getPhoneNumber()) {
			destination.setPhoneNum(shipmentResponse.getConsignee().getContact()
					.getPhoneNumber());
		}

		String value = shipmentResponse.getConsignee().getCountryName();
		if (null != value && !("".equals(value))) {
			if (value.length() > 30) {
				value = value.substring(0, 30);
			}
		}
		destination.setCountryName(value);

		destination.setAddrLine1(shipmentResponse.getConsignee()
				.getCompanyName());
		destination.setAddrLine2(shipmentResponse.getConsignee().getContact()
				.getPersonName());
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
			String str = "";
			if(destination.getPostCode() != null && (!destination.getPostCode().equals("") )){
				str += destination.getPostCode() + " ";
			}
			if(destination.getCity() != null && !destination.getCity().equals("")){
				str += destination.getCity() + " ";
			}
			if(shipmentResponse.getConsignee().getSuburb() != null && !shipmentResponse.getConsignee().getSuburb().equals("")){
				str += shipmentResponse.getConsignee().getSuburb() + " ";
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
			}
		}
		return destination;
	}

	/**
	 * 
	 * @param shipmentValidateResponse
	 * @return shipment
	 * @throws LabelServiceException
	 */
	private Shipment getShipment(
			ShipmentValidateResponse shipmentValidateResponse, String hideAccountFlag)
					throws LabelServiceException {
		String refId1 = null;
		String refId2 = null;
		Shipment shipment = new Shipment();
		if (shipmentValidateResponse.getProductContentCode() != null) {
			shipment.setProductContentCode((shipmentValidateResponse
					.getProductContentCode()));
		}
		if (shipmentValidateResponse.getGlobalProductCode() != null) {
			shipment.setGlobalProductCode(shipmentValidateResponse
					.getGlobalProductCode());

		}
		if (shipmentValidateResponse.getProductShortName() != null) {
			shipment.setProductName((shipmentValidateResponse
					.getProductShortName()));
		}

		shipment.setShptCalendarDate(shipmentValidateResponse.getShipmentDate());
		//shipment.setWeight(new BigDecimal(0.0));
		// routing code
		shipment.setRoutingTextCode(shipmentValidateResponse
				.getDHLRoutingCode());
		shipment.setPickupDate(shipmentValidateResponse.getShipmentDate());
		if (null != shipmentValidateResponse.getBilling()) {
			if (null != shipmentValidateResponse.getBilling()
					.getBillingAccountNumber()) {
				//				shipment.setFreightAcctNum(shipmentValidateResponse.getBilling().getBillingAccountNumber());
				String registeredAccountObj = shipmentValidateResponse.getBilling()
						.getShipperAccountNumber();
				String dutyAccountNumber = shipmentValidateResponse.getBilling()
						.getDutyAccountNumber();

				String dutyPaymentType = "";
				if (null != shipmentValidateResponse.getBilling().getDutyPaymentType()) {
					dutyPaymentType = shipmentValidateResponse.getBilling().getDutyPaymentType().value();
				}

				String dutyAcctNo = "";
				String dutyAccFinal = "";
				if (dutyAccountNumber == null) {
					dutyAcctNo = "";
				} else {
					dutyAcctNo = dutyAccountNumber.toString().trim();
				}
				Services services = new Services();
				List serviceLst = shipmentValidateResponse.getSpecialService();
				if (serviceLst.size() > 0) {
					for (int i = 0; i < serviceLst.size(); ++i) {
						Service service = new Service();
						SpecialService obj = (SpecialService) serviceLst.get(i);
						if ("D".equals(obj.getSpecialServiceType())
								|| "DD".equals(obj.getSpecialServiceType())) {
							//						service.setLocalSvcCode("");
							//						service.setGlobalSvcCode("DD");
							//						service.setGlobalSvcName("Door to Door");
							dutyAccFinal = dutyAcctNo;
							services.getService().add(service);
						}
					}
				}
				
				if ("T".equalsIgnoreCase(dutyPaymentType)
						|| "O".equalsIgnoreCase(dutyPaymentType) || "R".equalsIgnoreCase(dutyPaymentType)) {
					dutyAccFinal = dutyAcctNo;
				}

				if ("S".equalsIgnoreCase(dutyPaymentType)) {
					if (dutyAcctNo.equals("")) {
						dutyAccFinal = registeredAccountObj;
					} else {
						dutyAccFinal = dutyAcctNo;
					}
				}
				if(null != dutyAccFinal){
					shipment.setDutyAcctNum("DTP A/C: " + dutyAccFinal);
				} else{
					shipment.setDutyAcctNum("DTP A/C: ");
				}
	

		}
			
			
			/**  XML PI 4.7 - To fix existing 4.6 bug - 29/10/2013 - change to send ShipperAcccountNumebr when payment type is S */
			ShipmentPaymentType shipPaymentType = shipmentValidateResponse.getBilling().getShippingPaymentType();
			if (shipPaymentType != null ){
				String paymentType = shipPaymentType.value();
//				System.out.println("paymentType :"+paymentType+":");
				if ("S".equalsIgnoreCase(paymentType)){
					String billingAccountNumber = shipmentValidateResponse.getBilling().getBillingAccountNumber();
					if (billingAccountNumber!= null && !"".equalsIgnoreCase(billingAccountNumber)){
//						System.out.println("Setting BillingAccountNumber to FRT ACC Number :"+billingAccountNumber);
						shipment.setFreightAcctNum(billingAccountNumber);
					}else{
						String shipperAccountNumber = shipmentValidateResponse.getBilling().getShipperAccountNumber();
//						System.out.println("Setting shipperAccountNumber to FRT ACC Number :"+shipperAccountNumber);
						shipment.setFreightAcctNum(shipperAccountNumber);
					}
				}else{
					String billingAccountNumber = shipmentValidateResponse.getBilling().getBillingAccountNumber();
//					System.out.println("Setting BillingAccountNumber to FRT ACC Number :"+billingAccountNumber);
					if (billingAccountNumber!= null && !"".equalsIgnoreCase(billingAccountNumber)){
						shipment.setFreightAcctNum(billingAccountNumber);
					}
				}
			}
			/**  XML PI 4.7 - To fix existing 4.6 bug - 29/10/2013 - change to send ShipperAcccountNumebr when payment type is S */

		}
		
		if ((hideAccountFlag != null) && (hideAccountFlag.equalsIgnoreCase("Y"))) {
			shipment.setFreightAcctNum(null);
		    shipment.setDutyAcctNum(null);
		}

		if (null != shipmentValidateResponse.getDutiable()) {
			shipment.setShptInfo3(shipmentValidateResponse.getDutiable()
					.getDeclaredValue()
					+ " "
					+ shipmentValidateResponse.getDutiable()
					.getDeclaredCurrency());
		}

		if (null != shipmentValidateResponse.getReference()
				&& shipmentValidateResponse.getReference().size() > 0) {
			Reference valRef = shipmentValidateResponse.getReference().get(0);
			String refId = valRef.getReferenceID();
			String ref1 = "";
			String ref2 = "";
			if (refId.length() > 20) {
				ref1 = refId.substring(0, 20);
				ref2 = refId.substring(20, refId.length());
				shipment.setShptInfo2(ref1);
				shipment.setShptInfo1(ref2);
			} else {
				ref1 = refId;
				shipment.setShptInfo2(ref1);
			}
		}
		if (null != shipmentValidateResponse.getDutiable()
				&& null != shipmentValidateResponse.getDutiable()
				.getTermsOfTrade()) {
			shipment.setDutySvcIndicator(shipmentValidateResponse.getDutiable()
					.getTermsOfTrade());
		}

		if (shipmentValidateResponse.getDestinationServiceArea()
				.getInboundSortCode() != null) {
			shipment.setInboundSortCode((shipmentValidateResponse
					.getDestinationServiceArea().getInboundSortCode()));
		}
		if (shipmentValidateResponse.getOriginServiceArea()
				.getOutboundSortCode() != null) {
			shipment.setOutboundSortCode((shipmentValidateResponse
					.getOriginServiceArea().getOutboundSortCode()));
		}
		BigDecimal weight = shipmentValidateResponse.getChargeableWeight();
		String weightStr = "";
		if(null!=weight){
			weightStr = weight.toString();
			if(weightStr.indexOf(".")!=-1){
				weightStr = weightStr.substring(0, weightStr.indexOf(".")+2);
			}else{
				weightStr+=".0";
			}
			shipment.setWeight(new BigDecimal(weightStr));
            // Victor, 10 Feb 2015, Fixing PRB0064492, passing null if no weight element present in the response
            } else {
                 shipment.setWeight(null);
            }

		
		//BEGIN :: Added below to populate DimWeight on the RU template label :: Rajesh Nagampurath :: 22-JAN-2015 | XMLPI Label Enhancement | XML_PI_v52_Cyrillic
		BigDecimal dimWeight = shipmentValidateResponse.getDimensionalWeight();
		String dimWeightStr = "";
		if(null!=dimWeight){
			dimWeightStr = dimWeight.toString();
			if(dimWeightStr.indexOf(".")!=-1){
				dimWeightStr = dimWeightStr.substring(0, dimWeightStr.indexOf(".")+2);
			}else{
				dimWeightStr+=".0";
			}
			shipment.setDimensionalWeight(new BigDecimal(dimWeightStr));
		 } else {
             shipment.setDimensionalWeight(null);
		}
		
		//END
		
		if ("K".equals(shipmentValidateResponse.getWeightUnit())) {
			shipment.setWeightUOM(WeightUOM.KGS);
		} else if ("L".equals(shipmentValidateResponse.getWeightUnit())) {
			shipment.setWeightUOM(WeightUOM.LBS);
		}

		/*String val = shipmentValidateResponse.getCourierMessage();
		if (null != val && val.length() > 70) {
			val = val.substring(0, 70);
		}
		shipment.setShipmentContents(val);*/

		shipment.setTotalNumOfPcs(BigInteger.valueOf(1));
		if(null!=shipmentValidateResponse.getContents() && !"".equals(shipmentValidateResponse.getContents())){
			String contents = shipmentValidateResponse.getContents();
			int lenght = contents.length();
			shipment.setSpclInfoSide1("Contents: ");
			int noOfLines = lenght/15;
			int noOfLeftChr = contents.length()%15;
			if(noOfLeftChr!=0){
				noOfLines+=1;
			}
			if(contents.length()>15){
				int i=1;
				int x=0;
				int y=15;
				if(i<=noOfLines){
					shipment.setSpclInfoSide2(contents.substring(x,y));
					i++;
					x+=15;
					if(i == (noOfLines)){
						y=contents.length();
					}else{
						y+=15;				
					}
				}
				if(i<=noOfLines){
					shipment.setSpclInfoSide3(contents.substring(x,y));
					i++;
					x+=15;
					if(i == (noOfLines)){
						y=contents.length();
					}else{
						y+=15;				
					}
				}
				if(i<=noOfLines){
					shipment.setSpclInfoSide4(contents.substring(x,y));
					i++;
					x+=15;
					if(i == (noOfLines)){
						y=contents.length();
					}else{
						y+=15;				
					}
				}
				if(i<=noOfLines){
					shipment.setSpclInfoSide5(contents.substring(x,y));
					i++;
					x+=15;
					if(i == (noOfLines)){
						y=contents.length();
					}else{
						y+=15;				
					}
				}
				if(i<=noOfLines){
					shipment.setSpclInfoSide6(contents.substring(x,y));
					i++;
					x+=15;
					if(i == (noOfLines)){
						y=contents.length();
					}else{
						y+=15;				
					}
				}
				if(i<=noOfLines){
					shipment.setSpclInfoSide7(contents.substring(x,y));
					i++;
					x+=15;
					if(i == (noOfLines)){
						y=contents.length();
					}else{
						y+=15;				
					}
				}
			}else{
				shipment.setSpclInfoSide2(contents);
			}
	}
		
		//shipment.setSpclInfoSideCmb(shipmentValidateResponse.getContents());


		List<String> internalServiceCodeList = (List<String>) shipmentValidateResponse.getInternalServiceCode();
		if(null != shipmentValidateResponse.getInternalServiceCode() && internalServiceCodeList.contains("C")){
			shipment.setInverseVideoInd(InverseVideoInd.Y);
		}
		else{
			shipment.setInverseVideoInd(InverseVideoInd.N);
		}


		shipment.setPaymentCode(PaymentCode.Y);
		if (shipmentValidateResponse.getPieces() != null
				&& shipmentValidateResponse.getPieces().getPiece() != null) {
			shipment.setTotalNumOfPcs(BigInteger
					.valueOf(shipmentValidateResponse.getPieces().getPiece()
							.size()));
		} else {
			throw new LabelServiceException(
					"Piece details not present - Label cannot be printed");
		}

		shipment.setRoutingTextCode(shipmentValidateResponse
				.getDHLRoutingCode());


		if ("BR".equals(shipmentValidateResponse.getConsignee()
				.getCountryCode())
				&& "BR".equals(shipmentValidateResponse.getShipper()
						.getCountryCode())) {
			shipment.setSpclInfo1("Shipper : CNPJ / CPF: "
					+ shipmentValidateResponse.getShipper().getFederalTaxId()
					+ " " + "IE / RG: "
					+ shipmentValidateResponse.getShipper().getStateTaxId());

			shipment.setSpclInfo2("Receiver : CNPJ / CPF: "
					+ shipmentValidateResponse.getConsignee().getFederalTaxId()
					+ " " + "IE / RG: "
					+ shipmentValidateResponse.getConsignee().getStateTaxId());

		}
		else if (("US"
				.equals(shipmentValidateResponse.getShipper().getCountryCode()))) {
			shipment.setSpclInfo1("These commodities, technology or software were exported from the United States ");
			shipment.setSpclInfo2("in accordance with the Export Administration regulations. Diversion contrary ");
			shipment.setSpclInfo3("to U.S law prohibited.");
		}
		else{
			if(null != shipmentValidateResponse.getShipper().getFederalTaxId()){
				shipment.setSpclInfo1("Shipper: ID: "
						+ shipmentValidateResponse.getShipper().getFederalTaxId());
			}
			if(null != shipmentValidateResponse.getConsignee().getFederalTaxId()){
				shipment.setSpclInfo2("Receiver: ID: "
						+ shipmentValidateResponse.getConsignee().getFederalTaxId());
			}
		}
		// START :: Pavan Kumar (TechMahindra) | 30-AUG-2013 | XMLPI 4.7 | Added as per mary comments
		if (shipmentValidateResponse.getCustData() != null) {
			shipment.setCustData((shipmentValidateResponse
					.getCustData()));
		}
		// END :: Pavan Kumar (TechMahindra) | 30-AUG-2013 | XMLPI 4.7 | Added as per mary comments

		return shipment;
	}

	/**
	 * 
	 * @param shipmentResponse
	 * @return shipment
	 * @throws LabelServiceException
	 */
	private Shipment getShipment(ShipmentResponse shipmentResponse, String hideAccountFlag)
			throws LabelServiceException {
		Shipment shipment = new Shipment();
		if (shipmentResponse.getProductContentCode() != null) {
			shipment.setProductContentCode((shipmentResponse
					.getProductContentCode()));
		}
		if (shipmentResponse.getGlobalProductCode() != null) {
			shipment.setGlobalProductCode(shipmentResponse
					.getGlobalProductCode());

		}
		if (shipmentResponse.getProductShortName() != null) {
			shipment.setProductName((shipmentResponse.getProductShortName()));
		}
		shipment.setShptCalendarDate(shipmentResponse.getShipmentDate());
		//shipment.setWeight(new BigDecimal(0.0));
		shipment.setPickupDate(shipmentResponse.getShipmentDate());
		shipment.setRoutingTextCode(shipmentResponse.getDHLRoutingCode());
		if (null != shipmentResponse.getBilling()) {
			if (null != shipmentResponse.getBilling().getBillingAccountNumber()) {
//				shipment.setFreightAcctNum(shipmentResponse.getBilling()
//						.getBillingAccountNumber());

				String registeredAccountObj = shipmentResponse.getBilling()
						.getShipperAccountNumber();
				String dutyAccountNumber = shipmentResponse.getBilling()
						.getDutyAccountNumber();

				String dutyPaymentType = "";
				if (null != shipmentResponse.getBilling().getDutyPaymentType()) {
					dutyPaymentType = shipmentResponse.getBilling().getDutyPaymentType().value();
				}

				String dutyAcctNo = "";
				String dutyAccFinal = "";
				if (dutyAccountNumber == null) {
					dutyAcctNo = "";
				} else {
					dutyAcctNo = dutyAccountNumber.toString().trim();
				}
				Services services = new Services();
				List serviceLst = shipmentResponse.getSpecialService();
				if (serviceLst.size() > 0) {
					for (int i = 0; i < serviceLst.size(); ++i) {
						Service service = new Service();
						SpecialService obj = (SpecialService) serviceLst.get(i);
						if ("D".equals(obj.getSpecialServiceType())
								|| "DD".equals(obj.getSpecialServiceType())) {
//							service.setLocalSvcCode("");
//							service.setGlobalSvcCode("DD");
//							service.setGlobalSvcName("Door to Door");
							dutyAccFinal = dutyAcctNo;
							services.getService().add(service);
						}
					}
				}
				
				if ("T".equalsIgnoreCase(dutyPaymentType)
						|| "O".equalsIgnoreCase(dutyPaymentType) || "R".equalsIgnoreCase(dutyPaymentType)) {
					dutyAccFinal = dutyAcctNo;
					
				}

				if (dutyPaymentType.toString().trim().equalsIgnoreCase("S")) {
					if (dutyAcctNo.equals("")) {
						dutyAccFinal = registeredAccountObj;
					} else {
						dutyAccFinal = dutyAcctNo;
					}
				}
				//shipment.setDutyAcctNum(dutyAccFinal == null ? " " : dutyAccFinal);
				if(null != dutyAccFinal){
					shipment.setDutyAcctNum("DTP A/C: " + dutyAccFinal);
				} else{
					shipment.setDutyAcctNum("DTP A/C: ");
				}

			}

			/**  XML PI 4.7 - To fix existing 4.6 bug - 29/10/2013 - change to send ShipperAcccountNumebr when payment type is S */
			ShipmentPaymentType shipPaymentType = shipmentResponse.getBilling().getShippingPaymentType();
			if (shipPaymentType != null ){
				String paymentType = shipPaymentType.value();
				//System.out.println("paymentType :"+paymentType+":");
				if ("S".equalsIgnoreCase(paymentType)){
					String billingAccountNumber = shipmentResponse.getBilling().getBillingAccountNumber();
					if (billingAccountNumber!= null && !"".equalsIgnoreCase(billingAccountNumber)){
					//	System.out.println("Setting BillingAccountNumber to FRT ACC Number :"+billingAccountNumber);
						shipment.setFreightAcctNum(billingAccountNumber);
					}else{
						String shipperAccountNumber = shipmentResponse.getBilling().getShipperAccountNumber();
						//System.out.println("Setting shipperAccountNumber to FRT ACC Number :"+shipperAccountNumber);
						shipment.setFreightAcctNum(shipperAccountNumber);
					}
				}else{
					String billingAccountNumber = shipmentResponse.getBilling().getBillingAccountNumber();
//					/System.out.println("Setting BillingAccountNumber to FRT ACC Number :"+billingAccountNumber);
					if (billingAccountNumber!= null && !"".equalsIgnoreCase(billingAccountNumber)){
						shipment.setFreightAcctNum(billingAccountNumber);
					}
				}
			}
			/**  XML PI 4.7 - To fix existing 4.6 bug - 29/10/2013 - change to send ShipperAcccountNumebr when payment type is S */

			
		}
		
		if ((hideAccountFlag != null) && (hideAccountFlag.equalsIgnoreCase("Y"))) {
			shipment.setFreightAcctNum(null);
		    shipment.setDutyAcctNum(null);
		}
		
		// for special service
		StringBuffer spServiceSB = new StringBuffer();
		for (SpecialService spService : shipmentResponse.getSpecialService()) {
			if (spServiceSB.length() > 0) {
				spServiceSB.append("-");
			}
			spServiceSB.append(spService.getSpecialServiceType());
		}
		if (spServiceSB.length() > 0) {
			shipment.setDutySvcIndicator(spServiceSB.toString());
		}

		if (null != shipmentResponse.getDutiable()) {
			shipment.setShptInfo3(shipmentResponse.getDutiable()
					.getDeclaredValue()
					+ " "
					+ shipmentResponse.getDutiable().getDeclaredCurrency());
		}

		if (null != shipmentResponse.getReference()
				&& shipmentResponse.getReference().size() > 0) {
			Reference valRef = shipmentResponse.getReference().get(0);
			String refId = valRef.getReferenceID();
			String ref1 = "";
			String ref2 = "";
			if (refId.length() > 20) {
				ref1 = refId.substring(0, 20);
				ref2 = refId.substring(20, refId.length());
				shipment.setShptInfo2(ref1);
				shipment.setShptInfo1(ref2);
			} else {
				ref1 = refId;
				shipment.setShptInfo2(ref1);
			}
		}

		if (null != shipmentResponse.getDutiable()
				&& null != shipmentResponse.getDutiable().getTermsOfTrade()) {
			shipment.setDutySvcIndicator(shipmentResponse.getDutiable()
					.getTermsOfTrade());
		}

		if (shipmentResponse.getDestinationServiceArea().getInboundSortCode() != null) {
			shipment.setInboundSortCode((shipmentResponse
					.getDestinationServiceArea().getInboundSortCode()));
		}
		if (shipmentResponse.getOriginServiceArea().getOutboundSortCode() != null) {
			shipment.setOutboundSortCode((shipmentResponse
					.getOriginServiceArea().getOutboundSortCode()));
		}
		BigDecimal weight = shipmentResponse.getChargeableWeight();
		String weightStr = "";
		if(null!=weight){
			weightStr = weight.toString();
			if(weightStr.indexOf(".")!=-1){
				weightStr = weightStr.substring(0, weightStr.indexOf(".")+2);
			}else{
				weightStr+=".0";
			}
			shipment.setWeight(new BigDecimal(weightStr));
            // Victor, 10 Feb 2015, Fixing PRB0064492, passing null if no weight element present in the response
            } else {
              shipment.setWeight(null);
            }

		
		
		//BEGIN :: Added below to populate DimWeight on the RU template label :: Rajesh Nagampurath :: 22-JAN-2015 | XMLPI Label Enhancement | XML_PI_v52_Cyrillic
				BigDecimal dimWeight = shipmentResponse.getDimensionalWeight();
				String dimWeightStr = "";
				if(null!=dimWeight){
					dimWeightStr = dimWeight.toString();
					if(dimWeightStr.indexOf(".")!=-1){
						dimWeightStr = dimWeightStr.substring(0, dimWeightStr.indexOf(".")+2);
					}else{
						dimWeightStr+=".0";
					}
					shipment.setDimensionalWeight(new BigDecimal(dimWeightStr));
				 } else {
		             shipment.setDimensionalWeight(null);
				}
		//END
		if ("K".equals(shipmentResponse.getWeightUnit())) {
			shipment.setWeightUOM(WeightUOM.KGS);
		} else if ("L".equals(shipmentResponse.getWeightUnit())) {
			shipment.setWeightUOM(WeightUOM.LBS);
		}

		/*String val = shipmentResponse.getCourierMessage();
		if (null != val && val.length() > 70) {
			val = val.substring(0, 70);
		}
		shipment.setShipmentContents(val);*/

		shipment.setTotalNumOfPcs(BigInteger.valueOf(1));
		if(null!=shipmentResponse.getContents() && !"".equals(shipmentResponse.getContents())){
				String contents = shipmentResponse.getContents();
				int lenght = contents.length();
				shipment.setSpclInfoSide1("Contents: ");
				int noOfLines = lenght/15;
				int noOfLeftChr = contents.length()%15;
				if(noOfLeftChr!=0){
					noOfLines+=1;
				}
				if(contents.length()>15){
					int i=1;
					int x=0;
					int y=15;
					if(i<=noOfLines){
						shipment.setSpclInfoSide2(contents.substring(x,y));
						i++;
						x+=15;
						if(i == (noOfLines)){
							y=contents.length();
						}else{
							y+=15;				
						}
					}
					if(i<=noOfLines){
						shipment.setSpclInfoSide3(contents.substring(x,y));
						i++;
						x+=15;
						if(i == (noOfLines)){
							y=contents.length();
						}else{
							y+=15;				
						}
					}
					if(i<=noOfLines){
						shipment.setSpclInfoSide4(contents.substring(x,y));
						i++;
						x+=15;
						if(i == (noOfLines)){
							y=contents.length();
						}else{
							y+=15;				
						}
					}
					if(i<=noOfLines){
						shipment.setSpclInfoSide5(contents.substring(x,y));
						i++;
						x+=15;
						if(i == (noOfLines)){
							y=contents.length();
						}else{
							y+=15;				
						}
					}
					if(i<=noOfLines){
						shipment.setSpclInfoSide6(contents.substring(x,y));
						i++;
						x+=15;
						if(i == (noOfLines)){
							y=contents.length();
						}else{
							y+=15;				
						}
					}
					if(i<=noOfLines){
						shipment.setSpclInfoSide7(contents.substring(x,y));
						i++;
						x+=15;
						if(i == (noOfLines)){
							y=contents.length();
						}else{
							y+=15;				
						}
					}
				}else{
					shipment.setSpclInfoSide2(contents);
				}
				
		}
	
		//shipment.setSpclInfoSideCmb(shipmentResponse.getContents());

		List<String> internalServiceCodeList = (List<String>) shipmentResponse.getInternalServiceCode();
		if(null != shipmentResponse.getInternalServiceCode() && internalServiceCodeList.contains("C")){
			shipment.setInverseVideoInd(InverseVideoInd.Y);
		}
		else{
			shipment.setInverseVideoInd(InverseVideoInd.N);
		}

		shipment.setPaymentCode(PaymentCode.Y);
		shipment.setTotalNumOfPcs(BigInteger.valueOf(shipmentResponse
				.getPieces().getPiece().size()));
		shipment.setRouteLcSpcCode(shipmentResponse.getDHLRoutingDataId());
		shipment.setRoutingTextCode(shipmentResponse.getDHLRoutingCode());

		if ("BR".equals(shipmentResponse.getConsignee().getCountryCode())
				&& "BR".equals(shipmentResponse.getShipper().getCountryCode())) {
			shipment.setSpclInfo1("Shipper: CNPJ / CPF: "
					+ shipmentResponse.getShipper().getFederalTaxId() + " "
					+ "IE / RG: "
					+ shipmentResponse.getShipper().getStateTaxId());

			shipment.setSpclInfo2("Receiver: CNPJ / CPF: "
					+ shipmentResponse.getConsignee().getFederalTaxId() + " "
					+ "IE / RG: "
					+ shipmentResponse.getConsignee().getStateTaxId());

		}
		else if (("US".equals(shipmentResponse.getShipper().getCountryCode()))) {
			shipment.setSpclInfo1("These commodities, technology or software were exported from the United States ");
			shipment.setSpclInfo2("in accordance with the Export Administration regulations. Diversion contrary ");
			shipment.setSpclInfo3("to U.S law prohibited.");
		}
		else{
			if(null != shipmentResponse.getShipper().getFederalTaxId()){
				shipment.setSpclInfo1("Shipper: ID: "
						+ shipmentResponse.getShipper().getFederalTaxId());
			}
			if(null != shipmentResponse.getConsignee().getFederalTaxId()){
				shipment.setSpclInfo2("Receiver: ID: "
						+ shipmentResponse.getConsignee().getFederalTaxId());
			}
		}
		// START :: Pavan Kumar (TechMahindra) | 30-AUG-2013 | XMLPI 4.7 | Added as per mary comments
		if (shipmentResponse.getCustData() != null) {
			shipment.setCustData((shipmentResponse
					.getCustData()));
		}
		// END :: Pavan Kumar (TechMahindra) | 30-AUG-2013 | XMLPI 4.7 | Added as per mary comments

		return shipment;
	}

	/**
	 * 
	 * @param shipmentValidateResponse
	 * @return pieces
	 * @throws LabelServiceException
	 */
	private Pieces getPieces(ShipmentValidateResponse shipmentValidateResponse)
			throws LabelServiceException {
		Pieces pieces = new Pieces();
		int size = shipmentValidateResponse.getPieces().getPiece().size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				ShipValResponsePiece shipValResponsePiece = shipmentValidateResponse
						.getPieces().getPiece().get(i);
				Piece piece = new Piece();
//				Not needed for Region specific response as Filing is part of Global Shipment response only
				//				if(null!=shipmentValidateResponse.getDutiable().getFiling() 
//						&& null!=shipmentValidateResponse.getDutiable().getFiling().getFTSR()
//						&& !"".equals(shipmentValidateResponse.getDutiable().getFiling().getFTSR())){
//					piece.setContents("EEI: "+shipmentValidateResponse.getDutiable().getFiling().getFTSR());
//				}else if(null!=shipmentValidateResponse.getDutiable().getFiling() 
//						&& null!=shipmentValidateResponse.getDutiable().getFiling().getITN()
//						&& !"".equals(shipmentValidateResponse.getDutiable().getFiling().getITN())){
//					piece.setContents("EEI: "+shipmentValidateResponse.getDutiable().getFiling().getITN());
//				}

				piece.setLicencePlateNum(shipValResponsePiece.getLicensePlate());
				BigDecimal weight = shipValResponsePiece.getWeight();
				String weightStr = "";
				if(null!=weight){
					weightStr = weight.toString();
					if(weightStr.indexOf(".")!=-1){
						weightStr = weightStr.substring(0, weightStr.indexOf(".")+2);
					}else{
						weightStr+=".0";
					}
					piece.setWeight(new BigDecimal(weightStr));
				}else{
					piece.setWeight(null);	
				}
				pieces.getPiece().add(piece);
			}
		}
		return pieces;
	}

	/**
	 * 
	 * @param shipmentResponse
	 * @return pieces
	 * @throws LabelServiceException
	 */
	private Pieces getPieces(ShipmentResponse shipmentResponse)
			throws LabelServiceException {
		Pieces pieces = new Pieces();
		int size = shipmentResponse.getPieces().getPiece().size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				ShipValResponsePiece shipValResponsePiece = shipmentResponse
						.getPieces().getPiece().get(i);
				Piece piece = new Piece();
				
				if(null!=shipmentResponse.getShipper().getCountryCode() 
						&& "US".equalsIgnoreCase(shipmentResponse.getShipper().getCountryCode()))
				{
					if ((shipmentResponse.getDutiable() != null) && (shipmentResponse.getDutiable().getFiling() != null) && 
			            (shipmentResponse.getDutiable().getFiling().getITN() != null) && 
			            (!"".equals(shipmentResponse.getDutiable().getFiling().getITN())))
			            piece.setContents("EEI: " + shipmentResponse.getDutiable().getFiling().getITN());
			          else if ((shipmentResponse.getDutiable() != null) && (shipmentResponse.getDutiable().getFiling() != null) && 
			            (shipmentResponse.getDutiable().getFiling().getFilingType() != null) && 
			            (!"".equals(shipmentResponse.getDutiable().getFiling().getFilingType())) && ("AES4".equalsIgnoreCase(shipmentResponse.getDutiable().getFiling().getFilingType().value())) && 
			            (shipmentResponse.getDutiable().getFiling().getAES4EIN() != null) && (!"".equals(shipmentResponse.getDutiable().getFiling().getAES4EIN())))
			            piece.setContents("EEI: AESPOST " + shipmentResponse.getDutiable().getFiling().getAES4EIN());
			          else if ((shipmentResponse.getDutiable() != null) && (shipmentResponse.getDutiable().getFiling() != null) && 
			            (shipmentResponse.getDutiable().getFiling().getFTSR() != null) && 
			            (!"".equals(shipmentResponse.getDutiable().getFiling().getFTSR()))) {
			            piece.setContents("EEI: " + shipmentResponse.getDutiable().getFiling().getFTSR());
			          }
				}
				piece.setLicencePlateNum(shipValResponsePiece.getLicensePlate());
				BigDecimal weight = shipValResponsePiece.getWeight();
				String weightStr = "";
				if(null!=weight){
					weightStr = weight.toString();
					if(weightStr.indexOf(".")!=-1){
						weightStr = weightStr.substring(0, weightStr.indexOf(".")+2);
					}else{
						weightStr+=".0";
					}
					piece.setWeight(new BigDecimal(weightStr));
				}else{
					piece.setWeight(null);	
				}
				pieces.getPiece().add(piece);
			}
		}

		return pieces;
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

}