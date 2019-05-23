/**
 * @author himanshu_agnihotri
 * This class calls the methods of different controllers to 
 * generate pdf files and finally calls the method of pdfmerger to 
 * generate one combined pdf. 
 *   
 */
package com.dhl.sop.label;

import java.io.File;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;

import com.dhl.ShipmentValidateResponse;

public class LabelReportHandler {
	
	/*public static void main( String[] args ) throws Exception	{
    	
  	  if(args.length != 2)
        {
            System.out.println("Usage : java LabelReportHandler \n");
            System.out.println(" where \n");
            System.out.println("Request XML MessagePath : The complete path of the request XML message to be send. E.g. .\\RequestXML\\shipval.xml \n");
            System.out.println("Response PDF Path : The complete directory path where the respose XML messages are to be saved. E.g. .\\PDFReports\\n");
               
        }
        else
        {
        	
        	System.out.println("Generating SOP compliant label .............. ");
        	ShipmentValidateResponse shipmentValidateResponse = LabelReportController.generateSOPLabel(args[0],args[1]);
        	String awbNumber = shipmentValidateResponse.getAirwayBillNumber();
        	System.out.println("SOP compliant label generated :" + args[1]+"GlobalLabel_"+awbNumber+".pdf");
        	System.out.println("Generating Archive label");
        	ArchiveLabelReportController.generateArchiveLabel(args[0],args[1], shipmentValidateResponse);
        	System.out.println("Archive label generated :" + args[1]+"ArchiveLabel_"+awbNumber+".pdf");
            
            ArrayList<String> argumnets= new ArrayList<String>(); 
            
            
            
            argumnets.add(args[1]+"GlobalLabel_"+awbNumber+".pdf");
            argumnets.add(args[1]+"ArchiveLabel_"+awbNumber+".pdf");
            argumnets.add(args[1]+awbNumber+".pdf");
            PdfMerger.getConcatenatedPdf(argumnets);
            
            System.out.println("Global Label and Archive label merged into:" + args[1]+awbNumber+".pdf");
        }
  }*/
	
	public static void main(String[] args) {		
		
			
			PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle.getBundle("label");
			
			String XMLFilePath = bundle.getString("XML_FILE_PATH");//"..\\TransformXMLtoHTML\\ResponseXMLS\\";
			String processedXMLFilePath = bundle.getString("PROCESSED_XML_FILE_PATH");//"..\\TransformXMLtoHTML\\ProcessedXMLS\\";
			String pdfPath = bundle.getString("RESPONSE_PATH");
			File xmlFile = new File(XMLFilePath);
			String [] xmlFileList = xmlFile.list();
			
			for(int iCount=0;iCount<xmlFileList.length;iCount++) {
				String fileName = xmlFileList[iCount];
				String filePath = XMLFilePath + fileName ;
				String movedFilePath = processedXMLFilePath + fileName ;				
				
				try {
					generate(filePath, pdfPath);
					File responsefile = new File( filePath ) ;
					File processedfile = new File( movedFilePath ) ;
					responsefile.renameTo( processedfile ) ;
					System.out.println("Response file " + fileName +" moved to " + processedXMLFilePath + " directory " ) ;
				} catch (Exception e) {
					System.out.println("Unable to generate label for " + filePath);
				}				
				
			}

		
	}
	private static void generate(String xmlPath, String pdfPath) throws Exception{
		
		System.out.println("Generating SOP compliant label .............. ");
    	ShipmentValidateResponse shipmentValidateResponse = LabelReportController.generateSOPLabel(xmlPath,pdfPath);
    	String awbNumber = shipmentValidateResponse.getAirwayBillNumber();
    	
	    if(awbNumber != null && !"".equals(awbNumber.trim()) ) {
	    	
	    	//BEGIN :: Pavan Kumar (TechMahindra) | 25-JUL-2013 | XMLPI 4.7 | Archive document image output option
	    	//Updated below from ArchiveLabel to ArchiveDocument and GlobalLabel to TransportLabel
	    	System.out.println("SOP compliant label generated :" + pdfPath+"TransportLabel_"+awbNumber+".pdf");
	    	System.out.println("Generating Archive document");
	    	ArchiveLabelReportController.generateArchiveLabel(xmlPath,pdfPath, shipmentValidateResponse);
	    	System.out.println("Archive document generated :" + pdfPath+"ArchiveDocument_"+awbNumber+".pdf");
	        
	        ArrayList<String> argumnets= new ArrayList<String>(); 
	    
	        argumnets.add(pdfPath+"TransportLabel_"+awbNumber+".pdf");
	        argumnets.add(pdfPath+"ArchiveDocument_"+awbNumber+".pdf");
	        argumnets.add(pdfPath+awbNumber+".pdf");
	        PdfMerger.getConcatenatedPdf(argumnets);
	        
	     // Edited by Victor Low K.C. in 12 July 2012. Create 2 new ArrayList 
	    	ArrayList<String> archive= new ArrayList<String>(); 
	    	ArrayList<String> global= new ArrayList<String>();
		    
	    	// Add in 1st String is the path of generated archive label in pdf format, 2nd String to rename the file.
	    	archive.add(pdfPath+"ArchiveDocument_"+awbNumber+".pdf");
	        archive.add(pdfPath+"Archive_Document_"+awbNumber+".pdf");
	        
	    	// Add in 1st String is the path of generated global label in pdf format, 2nd String to rename the file.
	        global.add(pdfPath+"TransportLabel_"+awbNumber+".pdf");
	        global.add(pdfPath+"Transport_Label_"+awbNumber+".pdf");
	        
	        PdfMerger.getConcatenatedPdf(archive);
	        PdfMerger.getConcatenatedPdf(global);
	        // Editing ends here
	        
	        String globalLabelFile = pdfPath+"TransportLabel_"+awbNumber+".pdf";
	        String archiveLabelFile = pdfPath+"ArchiveDocument_"+awbNumber+".pdf";
	        
	        // A File object to represent the filename
	        File globalFile = new File(globalLabelFile);
	        File archiveFile = new File(archiveLabelFile);
	
	        // Attempt to delete it
	        //mary 23th May 2012 - commented to remove the original PDFs
	        globalFile.delete();
	        archiveFile.delete();
	        
	        System.out.println("Transport Label and Archive Document merged into:" + pdfPath+awbNumber+".pdf");
	        
	      //END :: Pavan Kumar (TechMahindra) | 25-JUL-2013 | XMLPI 4.7 | Archive document image output option
	        
    	} else {
    		//Updated below from GlobalLabel to TransportLabel :: Pavan Kumar (TechMahindra) | 25-JUL-2013 | XMLPI 4.7 | Archive document image output option
    		String transportLabelFile = pdfPath+"TransportLabel_"+awbNumber+".pdf";
    		File transportFile = new File(transportLabelFile);
    		transportFile.delete();
    		throw new Exception("Awbnumber is empty");
    	}
	}

}
