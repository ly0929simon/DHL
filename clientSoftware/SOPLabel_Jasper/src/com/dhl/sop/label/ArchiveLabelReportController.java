/**
 * @author himanshu_agnihotri
 * This class populates the jasper file with the values from Request xml file 
 * and export the archive report into pdf form.
 */
package com.dhl.sop.label;
 
import java.io.File;
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


public class ArchiveLabelReportController {

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
	public static void generateArchiveLabel(String requestXMLPath, String archiveLabelPath, 
			ShipmentValidateResponse shipmentValidateResponse) 
	throws Exception	{

		if(shipmentValidateResponse == null){
			//Start:Getting ShipmentValidateResponse object from response xml
			System.out.println("parsing xml");
			JAXBContext jaxbContext = JAXBContext.newInstance("com.dhl");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	
			shipmentValidateResponse 
			= (ShipmentValidateResponse) unmarshaller.unmarshal(
					new File(requestXMLPath));//End
		}

		//Start:Creating JasperReport Object using .jasper file 
		JasperReport jasperReport;
		jasperReport = (JasperReport) JRLoader.loadObject("JasperReports/ArchiveLabel.jasper");//End
		JasperPrint jasperPrint;

		//Start:Populating HashMap with different fixed values for all the pieces
		HashMap <String, String> shipmentDetails = new HashMap <String, String>();

		shipmentDetails.put("ProductShortName",shipmentValidateResponse.getProductShortName() );
				
//		if(shipmentValidateResponse.getDutiable() != null )	{	
//			//DuitableFlag will be used to determine whether ProductContentCode 
//        	// should be displayed in inverse video
//			shipmentDetails.put("DuitableFlag","true");
//		} else {			
//			shipmentDetails.put("DuitableFlag","false");
//		}
		
		shipmentDetails.put("ProductContentCode", shipmentValidateResponse.getProductContentCode());
		
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
		// mary 5th Aug 2010 - remove the restriction of CNPJ which only for BR - end

		//Added on 12th Jan 2010 for CNPJ and IE
		
		//Added on 4th Feb 2010 for CommerceControlStatement : US export only
		
		if("US".equalsIgnoreCase(shipmentValidateResponse.getShipper().getCountryCode()) 
			&& !"US".equalsIgnoreCase(shipmentValidateResponse.getConsignee().getCountryCode())){
			
			shipmentDetails.put("CommerceControlStatement", 
					"These commodities, technology or software were exported " +
					"from the United States in accordance with the Export Administration " +
					"regulations. Diversion contrary to U.S law prohibited. " +
					"Shipment may be varied via intermediate stopping places " +
					"which DHL deems appropriate." );
		}
		
		//Populate shipper address line and company name
		shipmentDetails.put("ShipperCompanyName", shipmentValidateResponse.getShipper().getCompanyName());
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
		if (shipperPostalCode != null) {
			shipperPostalCode = shipperPostalCode.toUpperCase();

		//mary June 2011 move to here print the shipper postal code only if it is available as there are non-postcode country)
			shipperAdressDetails.append(shipperPostalCode);
		}
		
		//mary June 2011 move to print the shipper postal code only if it is available as there are non-postcode country - begin)
		//shipperAdressDetails.append(shipperPostalCode);
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
		// Modify on 03-Feb-2010 to include Contact Name
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
		
		StringBuilder receiverAddressDetails = new StringBuilder();
		String receiverCity = shipmentValidateResponse.getConsignee().getCity();
		if (receiverCity != null && !"".equals(receiverCity.trim())) {
			receiverAddressDetails.append(receiverCity);
			receiverAddressDetails.append("  ");
		}
		String receiverDivisionCode = shipmentValidateResponse.getConsignee().getDivisionCode();
		if (receiverDivisionCode != null && !"".equals(receiverDivisionCode.trim())) {
			receiverAddressDetails.append(receiverDivisionCode);
			receiverAddressDetails.append("  ");
		}
		String receiverPostalCode = shipmentValidateResponse.getConsignee().getPostalCode();
		if (receiverPostalCode != null) {
			receiverPostalCode = receiverPostalCode.toUpperCase();			

			//mary June 2011 move to here print the receiver postal code only if it is available as there are non-postcode country
			receiverAddressDetails.append(receiverPostalCode);
		}
		//mary June 2011 move to print the shipper postal code only if it is available as there are non-postcode country - begin)				
		// receiverAddressDetails.append(receiverPostalCode);
		//mary June 2011 move to print the shipper postal code only if it is available as there are non-postcode country - end)		


		int destCityLength = receiverAddressDetails.toString().length();
		if(destCityLength > 46){
		shipmentDetails.put("DestinationCity", receiverAddressDetails.toString());
		}else {
		shipmentDetails.put("DestinationCityExtra", receiverAddressDetails.toString());
		}
		shipmentDetails.put("DestinationCountry", shipmentValidateResponse.getConsignee().getCountryName());

		StringBuilder receiverContactDetails = new StringBuilder();
		// Modify on 01-Feb-2010 to move Contact Name to destination address field
//		receiverContactDetails.append(shipmentValidateResponse.getConsignee().getContact().getPersonName());
//		receiverContactDetails.append("\n");
		// End move Contact Name
		receiverContactDetails.append("Ph:"+shipmentValidateResponse.getConsignee().getContact().getPhoneNumber());
		//receiverContactDetails.append("\n");
		shipmentDetails.put("ReceiverContactDetails",receiverContactDetails.toString());

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

		List<String> internalServiceCodeList = (List<String>) shipmentValidateResponse.getInternalServiceCode();
		List<SpecialService> serviceList = shipmentValidateResponse.getSpecialService();
		StringBuffer internalServiceCodeSB = new StringBuffer();
		StringBuffer serviceCodeSB = new StringBuffer();
		
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
		if (serviceList != null) 
		{		
			for (SpecialService specialService1 : serviceList) 
			{
				serviceCodeSB.append(specialService1.getSpecialServiceType());
				serviceCodeSB.append("-");
			}
		}

		String internalServiceCode = internalServiceCodeSB.toString();
		String serviceCode = serviceCodeSB.toString();
		//remove last '-'
		if(internalServiceCode.endsWith("-")){		
			internalServiceCode = internalServiceCode.substring(0,internalServiceCode.length() -1 );
		}
		
		//remove last '-'
		if(serviceCode.endsWith("-")){		
			serviceCode = serviceCode.substring(0,serviceCode.length() -1 );
		}
		
		shipmentDetails.put("ServiceCode",serviceCode);
		
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
		shipmentDetails.put("DeliveryTime",shipmentValidateResponse.getDeliveryTimeCode());
		shipmentDetails.put("CalendarDate",shipmentValidateResponse.getDeliveryDateCode());

		Long registeredAccountObj = new Long(shipmentValidateResponse.getBilling().getShipperAccountNumber());
		shipmentDetails.put("AccountNumber",registeredAccountObj.toString());

		List<Reference> referenceList = (List<Reference>) shipmentValidateResponse.getReference();
		String referenceId = "";
		if(referenceList != null && referenceList.size()>0){
			Reference reference = (Reference)referenceList.get(0);
			referenceId = "" + reference.getReferenceID();
		}
		
		shipmentDetails.put("ReferenceNumber",referenceId);
		shipmentDetails.put("Date",shipmentValidateResponse.getShipmentDate().toString());
		shipmentDetails.put("ContentsDescription","Content: "+shipmentValidateResponse.getContents());
			
		String airWayBillNumber = shipmentValidateResponse.getAirwayBillNumber();
		String airWayBillNumberWithSpaces="";

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
		shipmentDetails.put("AirWayBillBarCode",shipmentValidateResponse.getAirwayBillNumber());
		
		String unit = shipmentValidateResponse.getWeightUnit();
		String weightUnit;
		if(unit != null){
			weightUnit = unit.toString();
		} else {
			weightUnit = "";
		}
		
        if(weightUnit.equalsIgnoreCase("G"))	{
			weightUnit="gm";		
		}else if(weightUnit.equalsIgnoreCase("K"))	{			
			weightUnit="Kg";			
		}else {			
			weightUnit="Lbs";
		}
        if(shipmentValidateResponse.getChargeableWeight() != null){
        	shipmentDetails.put("ShipmentWeight",shipmentValidateResponse.getChargeableWeight().toString()+" "+weightUnit);
        }
		shipmentDetails.put("GlobalProductCode",shipmentValidateResponse.getGlobalProductCode());//End

		//Start:Populating piece specific values in pieceMap
		List <ShipValResponsePiece> pieceList = shipmentValidateResponse.getPieces().getPiece();
		
		String totalPieces = "0";
		if(pieceList != null){
			totalPieces = "" + pieceList.size();
		}	
		//String totalPieces = shipmentValidateResponse.getPiece().toString();
		shipmentDetails.put("Pieces",totalPieces);
		ArrayList<HashMap<String,String>> pieceFieldMaps = new ArrayList<HashMap<String,String>>();
		ShipValResponsePiece shipValResponsePiece;

		StringBuilder licensePlateBarCodeText = new StringBuilder();		

		//if(Integer.parseInt(totalPieces) <= 11 || Integer.parseInt(totalPieces) > 11 )	{
		List<ShipValResponsePiece> mainpageList;
		List<ShipValResponsePiece> subpageList;
			
		
		//LP numbers on 1st Archive page
		if(pieceList.size() > 7){
			mainpageList = pieceList.subList(0, 7);
			subpageList = pieceList.subList(7, pieceList.size());
		} else {
			mainpageList = pieceList;
			subpageList = new ArrayList<ShipValResponsePiece>();
		}
		//subpageList = subpageList.subList(0, 39 + 38);
		//int x = 0;
		
		Iterator<ShipValResponsePiece> mainpageIterator = mainpageList.iterator();
		while(mainpageIterator.hasNext()){				
			shipValResponsePiece = (ShipValResponsePiece)mainpageIterator.next();
			// mary 19th Aug 2011 commented the LP Data identifier as end user will confuse this is part of LP number
			//String pieceDataIdentifier = "("+shipValResponsePiece.getDataIdentifier()+") ";
			//licensePlateBarCodeText.append("- ");
			//licensePlateBarCodeText.append(pieceDataIdentifier);
			String licensePlateWithSpacesFirst = getLicensePlateWithSpaces(shipValResponsePiece.getLicensePlate());
			licensePlateBarCodeText.append(licensePlateWithSpacesFirst+"\n");			
		}
		shipmentDetails.put("LicensePlateBarCodeText",licensePlateBarCodeText.toString());	
		if (subpageList.size() > 0) {	
			shipmentDetails.put("Continued_Title","(continued):");								
		} else {
			shipmentDetails.put("Continued_Title",":");	
		}
		  
		Iterator<ShipValResponsePiece> subpageListIterator = subpageList.iterator();
		
		int count = 0;
		
		HashMap<String, String> pieceMap  = new HashMap<String, String>();
		pieceMap.put("PageCounter","2");
		StringBuilder	licensePlateSB = new StringBuilder();
		while(subpageListIterator.hasNext()){
			if(count == 39){
				
				pieceMap.put("FieldLicensePlateBarCodeText",licensePlateSB.toString());
				pieceMap.put("Continued","(continued):");
				pieceFieldMaps.add(pieceMap);
				Integer pageCounter = new Integer(pieceMap.get("PageCounter"));
				
				count = 0;
				pieceMap  = new HashMap<String, String>();
				
				pageCounter++;
				pieceMap.put("PageCounter", pageCounter.toString());
				licensePlateSB = new StringBuilder();
			}
			shipValResponsePiece = (ShipValResponsePiece)subpageListIterator.next();
			// mary 19th Aug 2011 commented the LP Data identifier as end user will confuse this is part of LP number
			//String pieceDataIdentifier = "("+shipValResponsePiece.getDataIdentifier()+") ";
			//licensePlateSB.append("- ");
			//licensePlateSB.append(pieceDataIdentifier);
			String licensePlateWithSpaces = getLicensePlateWithSpaces(shipValResponsePiece.getLicensePlate());
			licensePlateSB.append(licensePlateWithSpaces+"\n");			
			count++;
			
		}
		if (subpageList.size() > 0) {
			pieceMap.put("FieldLicensePlateBarCodeText",licensePlateSB.toString());
			pieceMap.put("Continued",":");
			pieceFieldMaps.add(pieceMap);			
		}
		
		//Added on 30-Mar-2010

		DutyTaxPaymentType dutyPaymentType = shipmentValidateResponse.getBilling().getDutyPaymentType();
		Long dutyAccountNumber = shipmentValidateResponse.getBilling().getDutyAccountNumber();
		String dutyAcctNo = "";
		if(dutyAccountNumber == null)
		{
			dutyAcctNo = "";
		}
		else
		{
			dutyAcctNo = dutyAccountNumber.toString().trim();
		}

		if (serviceList != null) {		
			for (SpecialService specialService : serviceList) {
				
				if ("D".equalsIgnoreCase(specialService.getSpecialServiceType()) || "DD".equalsIgnoreCase(specialService.getSpecialServiceType()) 
						//(DutyTaxPaymentType.fromValue("T") == dutyPaymentType) && 
						//(registeredAccountObj != dutyAccountNumber)	
						) {
					shipmentDetails.put("DutyAccountNumber","" + dutyAcctNo);
				}
				// mary 5th August 2010 remove this Insurance Value which populate from Insurance Charges field value - begin
				/*if ("I".equalsIgnoreCase(specialService.getSpecialServiceType())) {
					shipmentDetails.put("InsuredAmount","" + specialService.getChargeValue() + " " + specialService.getCurrencyCode());					;
				}*/
				// mary 5th August 2010 remove this Insurance Value which populate from Insurance Charges field value - end				
			}
		}

		if(dutyPaymentType.toString().trim().equalsIgnoreCase("T") || dutyPaymentType.toString().trim().equalsIgnoreCase("O")) 
		{
			shipmentDetails.put("DutyAccountNumber","" + dutyAcctNo);
		}

		if(dutyPaymentType.toString().trim().equalsIgnoreCase("S"))
		{
			if(dutyAcctNo.equals(""))
			{
				shipmentDetails.put("DutyAccountNumber","" + registeredAccountObj.toString());
			}
			else
			{
				shipmentDetails.put("DutyAccountNumber","" + dutyAcctNo);
			}
		}

		// mary 5th August 2010 Insurance Value which populate from InsuredAmount field value in xml request - begin
        if(shipmentValidateResponse.getInsuredAmount() != null)
		{
        	shipmentDetails.put("InsuredAmount","" + shipmentValidateResponse.getInsuredAmount() + " " + shipmentValidateResponse.getCurrencyCode());
        }
		// mary 5th August 2010 Insurance Value which populate from InsuredAmount field value in xml request - end
        
        // Start | Victor | 22May2013 | XML Services Insurance Legal issue
        Boolean toChange = false;
        String [] capChangeCtry = {
        		"PA", "CR", "GT", "SV", "NI", "DO", "HN", "AR", "BO", "CL", "EC", "PE", "PY", "UY"
        };
        
        for (int i=0; i<capChangeCtry.length; i++) {
        	String ctry = capChangeCtry[i];
        	if (shipmentValidateResponse.getCountryCode().equalsIgnoreCase(ctry)) 
        		toChange=true;        
        }
        
       if (toChange) 
    		shipmentDetails.put("InsuredAmntCap", "Ship. Value Prot.    :");
       else
        	shipmentDetails.put("InsuredAmntCap", "Insurance Amount  :");
       
         // End | Victor | 22May2013 | XML Services Insurance Legal issue

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
		//Updated below from ArchiveLabel to ArchiveDocument :: Pavan Kumar (TechMahindra) | 25-JUL-2013 | XMLPI 4.7 | Archive document image output option
		net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jasperPrint, 
				archiveLabelPath+"ArchiveDocument_"+shipmentValidateResponse.getAirwayBillNumber()+".pdf");//End
	}
	
	/**
	 * This method modifies license plate in bunch of 
	 * four digits starting from right.
	 * 
	 * @param String
	 *            licensePlateWithoutSpaces to put spaces
	 *            
	 * @return String
	 */ 
	public static String getLicensePlateWithSpaces(String licensePlateWithoutSpaces)	{
		
		
		String licensePlateWithSpaces = "";
		if(licensePlateWithoutSpaces == null){
			return licensePlateWithSpaces;
		}
		licensePlateWithoutSpaces = licensePlateWithoutSpaces.toUpperCase();
		int index  = licensePlateWithoutSpaces.indexOf("JD01"); 
		String jd01 = licensePlateWithoutSpaces.substring(0, index + 4);
		licensePlateWithoutSpaces = licensePlateWithoutSpaces.substring(index + 4);
		
		
		while(licensePlateWithoutSpaces.length() > 0){
			int len = licensePlateWithoutSpaces.length() - 4;
			if(len > 0){
				licensePlateWithSpaces = licensePlateWithoutSpaces.substring(len) + " " + licensePlateWithSpaces;
				licensePlateWithoutSpaces = licensePlateWithoutSpaces.substring(0,len);

			}else{
				licensePlateWithSpaces = licensePlateWithoutSpaces +  " " + licensePlateWithSpaces;
				licensePlateWithoutSpaces="";
			}

		}
		licensePlateWithSpaces = jd01 +  " " + licensePlateWithSpaces;

		return licensePlateWithSpaces;
	}
}