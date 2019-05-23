package com.dhl.xmlpi.labelservice;

import java.io.File;
import java.util.PropertyResourceBundle;

import com.dhl.xmlpi.labelservice.ui.LabelPrinterController;

public class LabelServiceInvoker {
	 
	public void invoke() {
		
		PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle
				.getBundle("label");
		String xmlFilePath = getAbsoluteFilePath(bundle.getString("XML_FILE_PATH"));// "..\\TransformXMLtoHTML\\ResponseXMLS\\";
		String processedXMLFilePath = getAbsoluteFilePath(bundle.getString("PROCESSED_XML_FILE_PATH"));// "..\\TransformXMLtoHTML\\ProcessedXMLS\\";
		String pdfPath = getAbsoluteFilePath(bundle.getString("RESPONSE_PATH"));
		String httpUrl = bundle.getString("SERVER_URL");
	

		showLabelUI(xmlFilePath, processedXMLFilePath, pdfPath, httpUrl);
	}

	private void showLabelUI(String xmlFilePath, String processedXMLFilePath,
			String pdfPath, String httpUrl) {
		LabelPrinterController labelPrinterController = new LabelPrinterController(
				xmlFilePath, processedXMLFilePath, pdfPath, httpUrl);
		labelPrinterController.show();
	}
	
	private String getAbsoluteFilePath(String filePath) {
		return new File(filePath).getAbsolutePath();
	}
	
	public static void main(String[] args) {
		LabelServiceInvoker invoker = new LabelServiceInvoker();
		
		invoker.invoke();
	}

}
