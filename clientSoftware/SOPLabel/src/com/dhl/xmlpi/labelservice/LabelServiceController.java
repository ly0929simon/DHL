/*
 * 
 * */
package com.dhl.xmlpi.labelservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.dhl.xmlpi.labelservice.errorresponse.CommercialInvoiceErrorResponse;
import com.dhl.xmlpi.labelservice.errorresponse.LabelErrorResponse;
import com.dhl.xmlpi.labelservice.exception.LabelServiceException;
import com.dhl.xmlpi.labelservice.model.request.LabelLayout;
import com.dhl.xmlpi.labelservice.model.response.CommercialInvoiceResponse;
import com.dhl.xmlpi.labelservice.model.response.LabelResponse;
import com.dhl.xmlpi.shipVal.models.ShipmentResponse;
import com.dhl.xmlpi.shipVal.models.ShipmentValidateResponse;

public class LabelServiceController {

	private String httpUrl;

	public LabelServiceController(String httpUrl) {
		this.httpUrl = httpUrl;

	}
	
	//Updated the below with labelTemplate, dhlLogoFlag and companyLogoFlag parameters
	//Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	public String processFile(String filePath, String labelFormat,
			List<LabelLayout> labelLayouts,
			ShipmentValidateResponse shipmentValidateResponse, String resolution, String labelTemplate, 
			boolean dhlLogoFlag, boolean companyLogoFlag, String hideAccountFlag)
			throws LabelServiceException {
		if (!filePath.toLowerCase().endsWith("xml")) {
			return null;
		}
		LabelServiceRequestCreator labelServiceRequestCreator = new LabelServiceRequestCreator();
		String labelServiceReqXml = labelServiceRequestCreator
				.createRequestFromShipValResp(filePath, labelFormat,
						labelLayouts, shipmentValidateResponse, resolution, labelTemplate, dhlLogoFlag, companyLogoFlag, hideAccountFlag);
		HttpURLConnection servletConnection = createUrlConnection();
		sendRequestToXmlpi(labelServiceReqXml, servletConnection);
		String responseXmlString = readResponseFromXmlpi(servletConnection);
		return responseXmlString;

	}

	//Updated the below with labelTemplate, dhlLogoFlag and companyLogoFlag parameters
	//Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	public String processFile(String filePath, String labelFormat,
			List<LabelLayout> labelLayouts, ShipmentResponse shipmentResponse, String resolution, String labelTemplate, 
			boolean dhlLogoFlag, boolean companyLogoFlag, String hideAccountFlag)
			throws LabelServiceException {
		if (!filePath.toLowerCase().endsWith("xml")) {
			return null;
		}
		LabelServiceRequestCreator labelServiceRequestCreator = new LabelServiceRequestCreator();
		String labelServiceReqXml = labelServiceRequestCreator
				.createRequestFromShipValResp(filePath, labelFormat,
						labelLayouts, shipmentResponse,resolution, labelTemplate, dhlLogoFlag, companyLogoFlag,hideAccountFlag);
		HttpURLConnection servletConnection = createUrlConnection();
		sendRequestToXmlpi(labelServiceReqXml, servletConnection);
		String responseXmlString = readResponseFromXmlpi(servletConnection);
		return responseXmlString;
	}

	public LabelResponse unmarshalLabelServResp(String labelServiceRespXml)
			throws LabelServiceException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.dhl.xmlpi.labelservice.model.response");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(null);
			LabelResponse labelServiceResponse = (LabelResponse) unmarshaller
					.unmarshal(new StringReader(labelServiceRespXml));
			return labelServiceResponse;
		} catch (JAXBException e) {
			LabelServiceException rootCause = new LabelServiceException(
					"Unable to parse response received from XMLPI, Response XML received from XMLPI = "
							+ labelServiceRespXml, e);
			throw new LabelServiceException(
					"Unable to parse response received from XMLPI", rootCause);
		}
	}

	public LabelErrorResponse unmarshalLabelServErrResp(
			String labelServiceRespXml) throws LabelServiceException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.dhl.xmlpi.labelservice.errorresponse");

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(null);

			LabelErrorResponse labelErrorResponse = (LabelErrorResponse) unmarshaller
					.unmarshal(new StringReader(labelServiceRespXml));

			return labelErrorResponse;

		} catch (JAXBException e) {
			LabelServiceException rootCause = new LabelServiceException(
					"Unable to parse response received from XMLPI, Response XML received from XMLPI = "
							+ labelServiceRespXml, e);
			throw new LabelServiceException(
					"Unable to parse response received from XMLPI", rootCause);
		}
	}

	//BEGIN :: Ravi Rastogi (TechMahindra) | 22-AUG-2013 | XMLPI 4.7 | Enhanced below method for UTF-8 changes
	private String readResponseFromXmlpi(HttpURLConnection servletConnection)
			throws LabelServiceException {
		try {
			InputStreamReader isr = new InputStreamReader(servletConnection.getInputStream(),"UTF8");
			
			BufferedReader rd = new BufferedReader(isr);
			StringBuilder result = new StringBuilder();
			String line = "";
			
			while ((line = rd.readLine()) != null) {
			    result.append(line).append("\n");
			}
			
			//
			return result.toString();
		} catch (IOException e) {
			throw new LabelServiceException(
					"Unable to read response from XMLPI", e);
		}
	}
	//END
	 //BEGIN :: Ravi Rastogi (TechMahindra) | 22-AUG-2013 | XMLPI 4.7 | Enhanced below method for UTF-8 changes
	private void sendRequestToXmlpi(String labelServiceReqXml,
			HttpURLConnection servletConnection) throws LabelServiceException {
		try {
			
			servletConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			servletConnection.setRequestProperty("Accept-Charset", "UTF-8");
			String len = Integer.toString(labelServiceReqXml.getBytes("UTF-8").length);
			servletConnection.setRequestProperty("Content-Length", len);
			servletConnection.connect();
			OutputStreamWriter wr = new OutputStreamWriter(servletConnection.getOutputStream(),"UTF8");
			
			wr.write(labelServiceReqXml);
			wr.flush();
			wr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//END
	private HttpURLConnection createUrlConnection()
			throws LabelServiceException {
		try {
			String query = "isUTF8Support=true";
			URL servletURL = new URL(httpUrl + "?" + query);
			HttpURLConnection servletConnection = null;
			servletConnection = (HttpURLConnection) servletURL.openConnection();
			servletConnection.setDoOutput(true); // to allow us to write to the
													// URL
			servletConnection.setDoInput(true);
			servletConnection.setUseCaches(false);
			servletConnection.setRequestMethod("POST");
			
			
			return servletConnection;
		} catch (MalformedURLException e) {
			throw new LabelServiceException(
					"Unable to connect to XMLPI, Incorrect XMLPI URL", e);
		} catch (ProtocolException e) {
			throw new LabelServiceException(
					"Unable to connect to XMLPI, Incorrect Protocol", e);
		} catch (IOException e) {
			throw new LabelServiceException(
					"Unable to connect to XMLPI Service", e);
		}
	}
	
	public String processCommercialInvoice(String filePath, ShipmentResponse shipmentResponse, boolean dhlLogoFlag, boolean companyLogoFlag)
			throws LabelServiceException {
		
		if (!filePath.toLowerCase().endsWith("xml")) {
			return null;
		}
		CommercialInvoiceRequestCreator commercialInvoiceRequestCreator = new CommercialInvoiceRequestCreator();
		String commercialInvoiceReqXml = commercialInvoiceRequestCreator
				.createRequestFromShipValResp(filePath, shipmentResponse, dhlLogoFlag, companyLogoFlag);
		HttpURLConnection servletConnection = createUrlConnection();
		sendRequestToXmlpi(commercialInvoiceReqXml, servletConnection);
		String responseXmlString = readResponseFromXmlpi(servletConnection);
		return responseXmlString;
	}
	
	public CommercialInvoiceResponse unmarshalCommercialInvoiceResp(String labelServiceRespXml)
			throws LabelServiceException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.dhl.xmlpi.labelservice.model.response");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(null);
			CommercialInvoiceResponse commercialInvoiceResponse = (CommercialInvoiceResponse) unmarshaller
					.unmarshal(new StringReader(labelServiceRespXml));
			return commercialInvoiceResponse;
		} catch (JAXBException e) {
			LabelServiceException rootCause = new LabelServiceException(
					"Unable to parse response received from XMLPI, Response XML received from XMLPI = "
							+ labelServiceRespXml, e);
			throw new LabelServiceException(
					"Unable to parse response received from XMLPI", rootCause);
		}
	}
	
	public CommercialInvoiceErrorResponse unmarshalCommercialInvErrResp(
			String labelServiceRespXml) throws LabelServiceException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.dhl.xmlpi.labelservice.errorresponse");

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(null);

			CommercialInvoiceErrorResponse commercialInvoiceErrorResponse = (CommercialInvoiceErrorResponse) unmarshaller
					.unmarshal(new StringReader(labelServiceRespXml));

			return commercialInvoiceErrorResponse;

		} catch (JAXBException e) {
			LabelServiceException rootCause = new LabelServiceException(
					"Unable to parse response received from XMLPI, Response XML received from XMLPI = "
							+ labelServiceRespXml, e);
			throw new LabelServiceException(
					"Unable to parse response received from XMLPI", rootCause);
		}
	}
}
