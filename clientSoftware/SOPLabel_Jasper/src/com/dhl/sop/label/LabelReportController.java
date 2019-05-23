/**
 * @author himanshu_agnihotri
 * This class populates the jasper file with the values from request xml file 
 * and export the global label report into pdf form.
 */
package com.dhl.sop.label;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import com.dhl.ShipmentValidateResponse;
import com.dhl.datatypes.Dutiable;
import com.dhl.datatypes.DutyTaxPaymentType;
import com.dhl.datatypes.Reference;
import com.dhl.datatypes.ShipValResponsePiece;
import com.dhl.datatypes.SpecialService;

//import org.apache.commons.collections.BidiMap; 
//import org.apache.commons.collections.bidimap.DualHashBidiMap;


public class LabelReportController {
	
	/**
	 * This method fetches different values from request xml and then 
	 * populates .jasper file and finally generates .pdf file.
	 * 
	 * @param String
	 *            requestXMLPath request xml file path
	 *
	 * @param String
	 *            sopLabelPath jasper file path
	 *            
	 * @return String
	 */  
	
    public static ShipmentValidateResponse generateSOPLabel(String requestXMLPath, String sopLabelPath) throws Exception {
    	
    	System.out.println("LabelReportController: Inside method generateSOPLabel");
    	//Parsing response xml using JAXB
		JAXBContext jaxbContext = JAXBContext.newInstance("com.dhl");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		ShipmentValidateResponse shipmentValidateResponse 
            = (ShipmentValidateResponse) unmarshaller.unmarshal(
                    new java.io.FileReader(requestXMLPath));//End
		
        //Start:Creating JasperReport Object using .jasper file 
		JasperReport jasperReport;
		jasperReport = (JasperReport) JRLoader.loadObject("JasperReports/SOPLabel.jasper");//End
		JasperPrint jasperPrint;
		
		//Start:Populating HashMap with label values which are same for all the pieces (pages)
	    HashMap <String, String> shipmentDetails = new HashMap <String, String>();
	    
		shipmentDetails.put("ProductShortName",shipmentValidateResponse.getProductShortName() );
		
		//Added on 12th Jan 2010 for CNPJ and IE
		
		String shipperCNPJ = shipmentValidateResponse.getShipper().getFederalTaxId();
		String consigneeCNPJ = shipmentValidateResponse.getConsignee().getFederalTaxId();
		String shipperIE = shipmentValidateResponse.getShipper().getStateTaxId();
		String consigneeIE = shipmentValidateResponse.getConsignee().getStateTaxId();
		
		// mary 5th Aug 2010 - remove the restriction of CNPJ which only for BR - begin
		if("BR".equalsIgnoreCase(shipmentValidateResponse.getShipper().getCountryCode())){

			if (shipperCNPJ != null && !"".equals(shipperCNPJ.trim()))   // mary 29/10/2010 add the empty checking for each fields here
			{	
				shipmentDetails.put("ShipperCNPJ", "CNPJ / CPF:"+shipperCNPJ);
			}

			if (consigneeCNPJ != null && !"".equals(consigneeCNPJ.trim())) 
			{
				shipmentDetails.put("ConsigneeCNPJ", "CNPJ / CPF:"+consigneeCNPJ);
			}

			if (shipperIE != null && !"".equals(shipperIE.trim())) 
			{
				shipmentDetails.put("ShipperIE", shipperIE );
			}
			if (consigneeIE != null && !"".equals(consigneeIE.trim())) 
			{
				shipmentDetails.put("ConsigneeIE", consigneeIE );
			}
		} 
		else 
		{
			if (shipperCNPJ != null && !"".equals(shipperCNPJ.trim())) 
			{
				shipmentDetails.put("ShipperCNPJ", "ID:"+shipperCNPJ);
			}

			if (consigneeCNPJ != null && !"".equals(consigneeCNPJ.trim())) 
			{
				shipmentDetails.put("ConsigneeCNPJ", "ID:"+consigneeCNPJ);
			}
	
		}
		
		//Added on 12th Jan 2010 for CNPJ and IE
		
		// mary 5th Aug 2010 - remove the restriction of CNPJ which only for BR - end

		//Added on 12th Jan 2010 for CommerceControlStatement : US export only
		
		if("US".equalsIgnoreCase(shipmentValidateResponse.getShipper().getCountryCode()) 
			&& !"US".equalsIgnoreCase(shipmentValidateResponse.getConsignee().getCountryCode())){
			
			shipmentDetails.put("CommerceControlStatement", 
					"These commodities, technology or software were exported " +
					"from the United States in accordance with the Export Administration " +
					"regulations. Diversion contrary to U.S law prohibited. " +
					"Shipment may be varied via intermediate stopping places " +
					"which DHL deems appropriate." );
		}
		
		
        shipmentDetails.put("ProductContentCode", shipmentValidateResponse.getProductContentCode());
				
        shipmentDetails.put("ShipperCompanyName", shipmentValidateResponse.getShipper().getCompanyName());
        //Populate shipper address line and company name
		StringBuilder shipperAddress = new StringBuilder();
		//shipperAddress.append(shipmentValidateResponse.getShipper().getCompanyName());
		//shipperAddress.append("\n");
		List<String> shipperAddressList = (List<String>) shipmentValidateResponse.getShipper().getAddressLine();

		Iterator<String> addressIterator = shipperAddressList.iterator();	
			
		while (addressIterator.hasNext()) {
			String addressLine = addressIterator.next();			
			shipperAddress.append(addressLine);
			shipperAddress.append("\n");
		}		
		
		
		shipmentDetails.put("ShipperAddress", shipperAddress.toString());
				
		//Populate Shippper City Divison Code and postal code
		StringBuilder shipperAdressDetails = new StringBuilder();
		String shipperCity = shipmentValidateResponse.getShipper().getCity();
		if (shipperCity != null && !"".equals(shipperCity.trim())) {
			shipperAdressDetails.append(shipperCity);
			shipperAdressDetails.append("  ");
		}
		
		String shipperDivisionCode = shipmentValidateResponse.getShipper().getDivisionCode();
		if (shipperDivisionCode != null && !"".equals(shipperDivisionCode.trim())) {
			shipperAdressDetails.append(shipperDivisionCode);
			shipperAdressDetails.append("  ");
		}
		String shipperPostalCode = shipmentValidateResponse.getShipper().getPostalCode();
		if (shipperPostalCode != null && !"".equals(shipperPostalCode.trim())) {
			shipperPostalCode = shipperPostalCode.toUpperCase();
			
			//mary June 2011 move to here print the shipper postal code only if it is available as there are non-postcode country
			shipperAdressDetails.append(shipperPostalCode);
		}
		//mary June 2011 move to print the shipper postal code only if it is available as there are non-postcode country - begin)
		// shipperAdressDetails.append(shipperPostalCode);
		//mary June 2011 move to print the shipper postal code only if it is available as there are non-postcode country - end)
		
		
		
		shipmentDetails.put("ShipperCity", shipperAdressDetails.toString());
		shipmentDetails.put("ShipperCountry", shipmentValidateResponse.getShipper().getCountryName());
		
		
		shipmentDetails.put("OriginServiceAreaCode",shipmentValidateResponse.getOriginServiceArea().getServiceAreaCode());
		
		//Add Shipper Contact Name and Shipper Phone Number
		shipmentDetails.put("ShipperContactName",shipmentValidateResponse.getShipper().getContact().getPersonName());
		
		shipmentDetails.put("ShipperPhoneNumber","Ph:"+shipmentValidateResponse.getShipper().getContact().getPhoneNumber());
		
		//Populate Consignee company name & Consignee address line 
		
		StringBuilder destinationAddressSB = new StringBuilder();
		destinationAddressSB.append(shipmentValidateResponse.getConsignee().getCompanyName());
		// Modify on 01-Feb-2010 to include Contact Name
		destinationAddressSB.append("\n");
		destinationAddressSB.append(shipmentValidateResponse.getConsignee().getContact().getPersonName());
		// End  Contact Name change
		List<String> receiverAddressList = (List<String>) shipmentValidateResponse.getConsignee().getAddressLine();
			
		Iterator<String> consigneeAddressIterator = receiverAddressList.iterator();	
		
		while (consigneeAddressIterator.hasNext()) {
			String addressLine = consigneeAddressIterator.next();
			destinationAddressSB.append("\n");
			destinationAddressSB.append(addressLine);
         }
		
		String destinationAddress = destinationAddressSB.toString();
		//remove last new line
		if(destinationAddress.endsWith("\n")){		
			destinationAddress = destinationAddress.substring(0,destinationAddress.length() -1 );
		}
		
		shipmentDetails.put("DestinationAddress", destinationAddress);
		
		//Populate consignee Address details
		StringBuilder receiverAddressDetails = new StringBuilder();
		String receiverCity = shipmentValidateResponse.getConsignee().getCity();
		if(receiverCity != null && !"".equals(receiverCity.trim())){
			receiverAddressDetails.append(receiverCity);
			receiverAddressDetails.append("  ");			
		}
		
		String receiverDivisionCode = shipmentValidateResponse.getConsignee().getDivisionCode();
		if(receiverDivisionCode != null && !"".equals(receiverDivisionCode.trim())){
			receiverAddressDetails.append(receiverDivisionCode);
			receiverAddressDetails.append("  ");			
		}
		
		String receiverPostalCode = shipmentValidateResponse.getConsignee().getPostalCode();
		
		if (receiverPostalCode != null && !"".equals(receiverPostalCode.trim())) {
			receiverPostalCode = receiverPostalCode.toUpperCase();
	
		//mary June 2011 move to here to print the shipper postal code only if it is available as there are non-postcode country
			receiverAddressDetails.append(receiverPostalCode);
		}

		//mary June 2011 move to print the shipper postal code only if it is available as there are non-postcode country - begin)	
		//receiverAddressDetails.append(receiverPostalCode);
		//mary June 2011 move to print the shipper postal code only if it is available as there are non-postcode country - end)	

		int destCityLength = receiverAddressDetails.toString().length();
		if(destCityLength > 46){
		shipmentDetails.put("DestinationCity", receiverAddressDetails.toString());
		}else {
		shipmentDetails.put("DestinationCityExtra", receiverAddressDetails.toString());
		}
		shipmentDetails.put("DestinationCountry", shipmentValidateResponse.getConsignee().getCountryName());
	    
		//Populate consignee Contact details
		StringBuilder receiverContactDetails = new StringBuilder();
		// Modify on 01-Feb-2010 to move Contact Name to destination address field
		//receiverContactDetails.append(shipmentValidateResponse.getConsignee().getContact().getPersonName());
		//receiverContactDetails.append("\n");
		// End move Contact Name
		receiverContactDetails.append("Ph:"+shipmentValidateResponse.getConsignee().getContact().getPhoneNumber());
		//receiverContactDetails.append("\n");
		shipmentDetails.put("ReceiverContactDetails",receiverContactDetails.toString());
		
		//Populate OutBoundSortCode, InBoundSortCode and DestinationFacilityCode
		shipmentDetails.put("OutBoundSortCode",shipmentValidateResponse.getOriginServiceArea().getOutboundSortCode());
		shipmentDetails.put("InBoundSortCode",shipmentValidateResponse.getDestinationServiceArea().getInboundSortCode());
		
		StringBuffer destinationFacilityCodeSB = new StringBuffer();
		destinationFacilityCodeSB.append(shipmentValidateResponse.getConsignee().getCountryCode());
		destinationFacilityCodeSB.append("-");
		destinationFacilityCodeSB.append(shipmentValidateResponse.getDestinationServiceArea().getServiceAreaCode());
		String facilityCode= shipmentValidateResponse.getDestinationServiceArea().getFacilityCode();
		if(facilityCode!= null && !facilityCode.equals(""))	{
			destinationFacilityCodeSB.append("-");
				destinationFacilityCodeSB.append(shipmentValidateResponse.getDestinationServiceArea().getFacilityCode());
			}	
			shipmentDetails.put("DestinationFacilityCode",destinationFacilityCodeSB.toString());
		
		//Populate feature codes
		List<String> internalServiceCodeList = (List<String>) shipmentValidateResponse.getInternalServiceCode();
		StringBuffer internalServiceCodeSB = new StringBuffer();
		
		//If shipment is dutiable ProductContentCode should be displayed in inverse video
        if(internalServiceCodeList.contains("C"))	{		
        	//DuitableFlag will be used to determine whether ProductContentCode 
        	// should be displayed in inverse video
			shipmentDetails.put("DuitableFlag","true");
			//internalServiceCodeList.remove("C");
		} else{					
			shipmentDetails.put("DuitableFlag","false");
		}
        //Start: Added for sorting product feature
		Collections.sort(internalServiceCodeList, new InternalServiceCodeComparator());
		for(String iSC: internalServiceCodeList){
			internalServiceCodeSB.append(iSC);
			internalServiceCodeSB.append("-");
		}
		//End
		
		String internalServiceCode = internalServiceCodeSB.toString();
		//remove last '-'
		if(internalServiceCode.endsWith("-")){		
			internalServiceCode = internalServiceCode.substring(0,internalServiceCode.length() -1 );
		}
		if(internalServiceCodeList.size()== 0 || internalServiceCodeList.size()== 1 )	{
			
			shipmentDetails.put("InternalServiceFlag","one");
		}else if(internalServiceCodeList.size()== 2)	{
			shipmentDetails.put("InternalServiceFlag","two");
		}else if(internalServiceCodeList.size()== 3)	{
			shipmentDetails.put("InternalServiceFlag","three");
		}else if(internalServiceCodeList.size()== 4)	{
			shipmentDetails.put("InternalServiceFlag","four");
		}else	{
			shipmentDetails.put("InternalServiceFlag","five");
		}
		shipmentDetails.put("InternalServiceCode",internalServiceCode);
		
		

		//Populate Date Time Code
		shipmentDetails.put("DeliveryTime",shipmentValidateResponse.getDeliveryTimeCode());
		shipmentDetails.put("CalendarDate",shipmentValidateResponse.getDeliveryDateCode());
		
		//Populate Account number
		Long registeredAccountObj = Long.valueOf(shipmentValidateResponse.getBilling().getShipperAccountNumber());
		shipmentDetails.put("AccountNumber",registeredAccountObj.toString());
		
		List<Reference> referenceList = (List<Reference>) shipmentValidateResponse.getReference();
		String referenceId = "";
		if(referenceList != null && referenceList.size()>0){
			Reference reference = (Reference)referenceList.get(0);
			referenceId = "" + reference.getReferenceID();
		}
		shipmentDetails.put("ReferenceNumber",referenceId);
        shipmentDetails.put("Date",shipmentValidateResponse.getShipmentDate().toString());
		// Modified on 01-Feb-2010 to make static text "Content:" align with value
        shipmentDetails.put("ContentsDescription","Content: "+shipmentValidateResponse.getContents());
		shipmentDetails.put("DataId","("+shipmentValidateResponse.getDHLRoutingDataId()+")");
				
		String airWayBillNumber = shipmentValidateResponse.getAirwayBillNumber();
		String airWayBillNumberWithSpaces="";
		// AWB should be in seperated by space after 4 digits, staring from right
		
		while(airWayBillNumber.length() > 0){
			
			int len = airWayBillNumber.length() - 4;
			if(len > 0){
				airWayBillNumberWithSpaces = airWayBillNumber.substring(len) + " " + airWayBillNumberWithSpaces;
				airWayBillNumber = airWayBillNumber.substring(0,len);

			}else{
				airWayBillNumberWithSpaces = airWayBillNumber +  " " + airWayBillNumberWithSpaces;
				airWayBillNumber="";
			}			
		}
						
		shipmentDetails.put("AirWayBillNumber",airWayBillNumberWithSpaces);
		
		String routingBarCodeText =shipmentValidateResponse.getDHLRoutingCode();
		if (routingBarCodeText != null) {
			routingBarCodeText = routingBarCodeText.toUpperCase();			
		}
		shipmentDetails.put("DHLRoutingBarCodeText",routingBarCodeText);
		
		shipmentDetails.put("AirWayBillBarCode",shipmentValidateResponse.getAirwayBillNumber());
		//shipmentDetails.put("DHLRoutingBarCode",shipmentValidateResponse.getDHLRoutingCode());//End
		//String routingBarcode = "("+shipmentValidateResponse.getDHLRoutingDataId()+")" + shipmentValidateResponse.getDHLRoutingCode();		
		//KPA: 24-Jan-2011: Remove parentheses from routingBarcode
		String routingBarcode = shipmentValidateResponse.getDHLRoutingDataId() + shipmentValidateResponse.getDHLRoutingCode();		

		shipmentDetails.put("DHLRoutingBarCode",routingBarcode);//End
		//Start:Populating piece specific values in pieceMap which will be different on each page
		ArrayList <ShipValResponsePiece> pieceList = (ArrayList <ShipValResponsePiece> ) shipmentValidateResponse.getPieces().getPiece();
		Iterator <ShipValResponsePiece> itrPiece = pieceList.iterator();
		String totalPieces = "0";
		if(pieceList != null){
			totalPieces = "" + pieceList.size();
		}		
		shipmentDetails.put("Pieces",totalPieces);
		
		//List hold maps which contains piece details for each page
		ArrayList<HashMap<String,String>> pieceFieldMaps = new ArrayList<HashMap<String,String>>();
		
		String unit = shipmentValidateResponse.getWeightUnit();
		String weightUnit;
		if(unit != null){
			weightUnit = unit.toString();
		} else {
			weightUnit = "";
		}		
		
		//Weight Unit conversion
		if(weightUnit.equalsIgnoreCase("G")) {			
			weightUnit="gm";				
		}else if(weightUnit.equalsIgnoreCase("K")) {			
			weightUnit="kg";			
		} else {			
			weightUnit="lbs";
		}
		//Populate values for each piece
		while(itrPiece.hasNext())	{
			
			HashMap<String, String> pieceMap = new HashMap<String, String>();
			ShipValResponsePiece shipValResponsePiece = (ShipValResponsePiece)itrPiece.next();
			pieceMap.put("PieceNumber", shipValResponsePiece.getPieceNumber().toString()+"/"+totalPieces);
			pieceMap.put("PieceCounter", shipValResponsePiece.getPieceNumber().toString());
			pieceMap.put("PieceWeight",shipValResponsePiece.getWeight().toString()+" "+weightUnit);
			pieceMap.put("PieceIdentifier","("+shipValResponsePiece.getDataIdentifier()+")");
			String licensePlateWithSpaces = ArchiveLabelReportController.getLicensePlateWithSpaces(shipValResponsePiece.getLicensePlate());
			pieceMap.put("LicensePlateBarCodeText",licensePlateWithSpaces);
			pieceMap.put("LicensePlateBarCode",shipValResponsePiece.getDataIdentifier()+shipValResponsePiece.getLicensePlate());
			pieceFieldMaps.add(pieceMap);
						
		}//End
		//Added on 30-Mar-2010
		List<SpecialService> serviceList = shipmentValidateResponse.getSpecialService();
		DutyTaxPaymentType dutyPaymentType = shipmentValidateResponse.getBilling().getDutyPaymentType();
		Long dutyAccountNumber = shipmentValidateResponse.getBilling().getDutyAccountNumber();
		if (serviceList != null) {		
			for (SpecialService specialService : serviceList) {
				
				if ("D".equalsIgnoreCase(specialService.getSpecialServiceType()) &&
						(DutyTaxPaymentType.fromValue("T") == dutyPaymentType)) {
					shipmentDetails.put("DutyAccountNumber","" + dutyAccountNumber);
				}
				// mary 5th August 2010 remove this Insurance Value which populate from Insurance Charges field value - begin
				/*if ("I".equalsIgnoreCase(specialService.getSpecialServiceType())) {
					shipmentDetails.put("InsuredAmount","" + specialService.getChargeValue() + " " + specialService.getCurencyCode());					;
				}*/
				// mary 5th August 2010 remove this Insurance Value which populate from Insurance Charges field value - end
				
			}
		}

		// mary 5th August 2010 Insurance Value which populate from InsuredAmount field value in xml request - begin
        if(shipmentValidateResponse.getInsuredAmount() != null)
		{
        	shipmentDetails.put("InsuredAmount","" + shipmentValidateResponse.getInsuredAmount() + " " + shipmentValidateResponse.getCurrencyCode());
        }
		// mary 5th August 2010 Insurance Value which populate from InsuredAmount field value in xml request - end

		Dutiable dutiable = shipmentValidateResponse.getDutiable();
		if (dutiable != null) {
			shipmentDetails.put("DeclaredValue","" + dutiable.getDeclaredValue() + " " + dutiable.getDeclaredCurrency());
			//shipmentDetails.put("DeclaredValueCurrency","" + shipmentValidateResponse.getDutiable().getDeclaredCurrency());
			if (dutiable.getTermsOfTrade() != null) {				
				shipmentDetails.put("TermsOfTrade", dutiable.getTermsOfTrade());
			}
			
		}
		if (shipmentValidateResponse.getBilling().getBillingAccountNumber() != 0) {			
			shipmentDetails.put("BillingAccountNumber",
					"" + shipmentValidateResponse.getBilling().getBillingAccountNumber());
		}
		//End
		
		//Start:Creating JRMapCollectionDataSource object and exporting the Report in pdf format
		JRMapCollectionDataSource mapDS = new JRMapCollectionDataSource(pieceFieldMaps);
		jasperPrint = JasperFillManager.fillReport(jasperReport, shipmentDetails,mapDS);
		//Updated below from GlobalLabel to TransportLabel :: Pavan Kumar (TechMahindra) | 25-JUL-2013 | XMLPI 4.7 | Archive document image output option
	    net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jasperPrint, 
	    		sopLabelPath+"TransportLabel_"+shipmentValidateResponse.getAirwayBillNumber()+".pdf");//End
	    System.out.println("LabelReportController:generateSOPLabel() : Label generated");
	    
	    return shipmentValidateResponse;
	 }
    
}