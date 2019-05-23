/*
 * Copyright (c) 2012 DHL
 * 
 * All rights reserved.
 */
package com.dhl.xmlpi.labelservice.ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.dhl.xmlpi.labelservice.LabelServiceController;
import com.dhl.xmlpi.labelservice.errorresponse.CommercialInvoiceErrorResponse;
import com.dhl.xmlpi.labelservice.errorresponse.LabelErrorResponse;
import com.dhl.xmlpi.labelservice.exception.LabelServiceException;
import com.dhl.xmlpi.labelservice.model.request.LabelLayout;
import com.dhl.xmlpi.labelservice.model.response.CommercialInvoiceResponse;
import com.dhl.xmlpi.labelservice.model.response.LabelResponse;
import com.dhl.xmlpi.shipVal.models.LabelImage;
import com.dhl.xmlpi.shipVal.models.MultiLabel;
import com.dhl.xmlpi.shipVal.models.MultiLabels;
import com.dhl.xmlpi.shipVal.models.OutputFormat;
import com.dhl.xmlpi.shipVal.models.ShipmentResponse;
import com.dhl.xmlpi.shipVal.models.ShipmentValidateResponse;

/**
 * @author ravi_singhal
 * 
 */
public class LabelPrinterController {

	/** The main panel containing the view */
	private LabelPrinterView frame;

	/** The file chooser */
	private JFileChooser fileChooser;

	/** The file chooser */
	private JFileChooser fileChooserToLocn;

	/** The file chooser */
	private String processedXMLFilePath;

	/** Label Service Controller */
	private LabelServiceController labelServiceController;

	/** Tabel Model */
	private DefaultTableModel tableModel;

	/** File */
	File[] files;

	/**
	 * Default Constructor
	 */
	public LabelPrinterController(String xmlFilePath,
			String processedXMLFilePath, String pdfPath, String httpUrl) {
		this.processedXMLFilePath = processedXMLFilePath;
		labelServiceController = new LabelServiceController(httpUrl);
		frame = new LabelPrinterView();
		init(xmlFilePath, pdfPath);
	}

	/**
	 * Initializes the various components of the panel
	 */
	private void init(String xmlFilePath, String pdfPath) {
		frame.getFilePathField().setText(xmlFilePath);
		frame.getPdfLocTextField().setText(pdfPath);
		configureFileChooser(xmlFilePath);
		configureFileChooserToLocn(pdfPath);
		configureBrowse();
		configurePDFBrowse();
		configureCloseButton();
		configureSubmitButton();
		configureOutputFormatCombo();
		configureStatusTable();
		configurePrinterComboBox();
		enableDisableFields();
		//BEGIN :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
		enableOutputFormatCombo();
		configureCompanyLogo();
		configureLabelTemplate();
		//END
		enableOrDisableDecodeCheckbox();
		//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
		configureDecodeCheckbox();
		//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
		configureDocumentTypeOption();
	}

	/**
	 * @param xmlFilePath
	 * @throws LabelServiceException
	 * @throws PrintException
	 * @throws PrinterException
	 */
	private void processXmlFile(String xmlFilePath) {
		JAXBContext jc;
		Unmarshaller u;
		ShipmentValidateResponse shipmentValidateResponse;
		ShipmentResponse shipmentResponse;
		String successMessage = "Successfully Processed and moved to ";
		//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
		String failureMessage = "";
		boolean sucessflag=false;
		//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
		try {
			try {
				jc = JAXBContext.newInstance("com.dhl.xmlpi.shipVal.models");
				u = jc.createUnmarshaller();
				u.setSchema(null);
				Object obj = u.unmarshal(new File(xmlFilePath));
				
				String hideAccountFlag = "N";

		        if (frame.gethideAccount().isSelected()) {
		          hideAccountFlag = "Y";
		        }
				
				if (obj instanceof ShipmentValidateResponse) {
					shipmentValidateResponse = (ShipmentValidateResponse) obj;
					
				//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
					if(frame.getDecodeCheckBox().isSelected()){
				//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
					if (shipmentValidateResponse.getLabelImage() != null
							&& shipmentValidateResponse.getLabelImage().size() > 0) {
						
						LabelImage labelImage = shipmentValidateResponse
								.getLabelImage().get(0);
						OutputFormat outputFormat = labelImage
								.getOutputFormat();
						byte[] outputImage = labelImage.getOutputImage();
						//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
							if (outputImage != null && outputFormat != null) {
									List<LabelLayout> labelLayouts = new ArrayList<LabelLayout>();
									labelLayouts.add(LabelLayout.ECOM);
									labelLayouts.add(LabelLayout.ARCH);
									handleLabelServiceResponse(outputImage,
											shipmentValidateResponse
													.getAirwayBillNumber(),
													outputFormat.value(), xmlFilePath, labelLayouts, null);
									sucessflag=true;
							}else{
								failureMessage="outputImage or outputFormat is empty";
							}
							
						if (labelImage.getMultiLabels() != null) {

							MultiLabels multiLabels = labelImage
									.getMultiLabels();

							if (multiLabels.getMultiLabel() != null) {

								List<MultiLabel> listOfMultiLabels = multiLabels
										.getMultiLabel();

								for (int i = 0; i < listOfMultiLabels.size(); i++) {
									MultiLabel multiLabel=	(MultiLabel)listOfMultiLabels.get(i);

									String docFormat = (String)  multiLabel
											.getDocFormat();
									String docName = (String) multiLabel
											.getDocName();
									byte[] docImage = (byte[]) multiLabel
											.getDocImageVal();

									if (docFormat != null && docName != null
											&& docImage != null) {
										List<LabelLayout> labelLayouts = new ArrayList<LabelLayout>();
										if("CustomInvoiceImage".equalsIgnoreCase(docName)) {
											handleLabelServiceResponse(
													docImage,
													shipmentValidateResponse
															.getAirwayBillNumber(),
													docFormat, xmlFilePath,
													labelLayouts, docName);
										} else {
											handleLabelServiceResponse(
													docImage,
													shipmentValidateResponse
															.getAirwayBillNumber(),
													docFormat, xmlFilePath,
													labelLayouts, null);
										}
										sucessflag=true;
									} else{
										failureMessage="docName or docFormat or docImage is empty";
									}

								}
							}
						}
					} else {
						failureMessage="Label Image is empty";
					}
					}else {
						processFile(xmlFilePath, shipmentValidateResponse, hideAccountFlag);
						sucessflag=true;
					}
			//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
				} else if (obj instanceof ShipmentResponse) {
					shipmentResponse = (ShipmentResponse) obj;
					//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
					if (frame.getDecodeCheckBox().isSelected()) {
						//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
						if (shipmentResponse.getLabelImage() != null && shipmentResponse.getLabelImage().size() > 0) {
							LabelImage labelImage = shipmentResponse.getLabelImage().get(0);
							OutputFormat outputFormat = labelImage.getOutputFormat();
							byte[] outputImage = labelImage.getOutputImage();
							//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
							if (outputImage != null && outputFormat != null) {
								List<LabelLayout> labelLayouts = new ArrayList<LabelLayout>();
								labelLayouts.add(LabelLayout.ECOM);
								labelLayouts.add(LabelLayout.ARCH);
								handleLabelServiceResponse(outputImage, shipmentResponse.getAirwayBillNumber(),
										outputFormat.value(), xmlFilePath, labelLayouts, null);
								sucessflag = true;
							} else {
								failureMessage = "outputImage or outputFormat is empty";
							}

							if (labelImage.getMultiLabels() != null) {

								MultiLabels multiLabels = labelImage.getMultiLabels();

								if (multiLabels.getMultiLabel() != null) {

									List<MultiLabel> listOfMultiLabels = multiLabels.getMultiLabel();

									for (int i = 0; i < listOfMultiLabels.size(); i++) {
										MultiLabel multiLabel = (MultiLabel) listOfMultiLabels.get(i);

										String docFormat = (String) multiLabel.getDocFormat();
										String docName = (String) multiLabel.getDocName();
										byte[] docImage = (byte[]) multiLabel.getDocImageVal();

										if (docFormat != null && docName != null && docImage != null) {
											List<LabelLayout> labelLayouts = new ArrayList<LabelLayout>();
											if ("CustomInvoiceImage".equalsIgnoreCase(docName)) {
												handleLabelServiceResponse(docImage,
														shipmentResponse.getAirwayBillNumber(), docFormat, xmlFilePath,
														labelLayouts, docName);
											} else {
												handleLabelServiceResponse(docImage,
														shipmentResponse.getAirwayBillNumber(), docFormat, xmlFilePath,
														labelLayouts, null);
											}
											sucessflag = true;
										} else {
											failureMessage = "docName or docFormat or docImage is empty";
										}

									}
								}
							} else {
								failureMessage = "Label Image is empty";
							}
						} else {
							// Below is to re-print commercial invoice PDF file
							if (frame.getAllTypeRadioButton().isSelected()) {
								processFile(xmlFilePath, shipmentResponse, hideAccountFlag);
								if(shipmentResponse.getdHLInvoiceType() != null) {
									processCommercialInvoice(xmlFilePath, shipmentResponse);	
								}
							} else if (frame.getCustomInvoiceRadioButton().isSelected()) {
								processCommercialInvoice(xmlFilePath, shipmentResponse);
							} else {
								processFile(xmlFilePath, shipmentResponse, hideAccountFlag);
							}
							sucessflag = true;
						}
					} else {

						// Below is to re-print commercial invoice PDF file
						if (frame.getAllTypeRadioButton().isSelected()) {
							processFile(xmlFilePath, shipmentResponse, hideAccountFlag);
							if(shipmentResponse.getdHLInvoiceType() != null) {
								processCommercialInvoice(xmlFilePath, shipmentResponse);
							}
						} else if (frame.getCustomInvoiceRadioButton().isSelected()) {
							processCommercialInvoice(xmlFilePath, shipmentResponse);
						} else {
							processFile(xmlFilePath, shipmentResponse, hideAccountFlag);
						}
						sucessflag = true;
					}
				}
				
				if(sucessflag){
					moveFile(xmlFilePath);
					successMessage = "Successfully Decoded Label Image and moved to ";
					logEvent(xmlFilePath, "Success", successMessage
							+ processedXMLFilePath, null);
				}else if(failureMessage.length()>0){
					logEvent(xmlFilePath, "Failure", failureMessage, null);
				}
				//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
			} catch (JAXBException e) {
				throw new LabelServiceException(
						"Failed to parse the Shipval Response XML File", e);
			}
			
		} catch (LabelServiceException e) {
			logEvent(xmlFilePath, "Failure", e.getXmlpiMessage(), e);
		}
	}

	/**
	 * @param xmlFilePath
	 * @param outputImage
	 * @throws PrintException
	 */
	private void printLabel(String xmlFilePath, byte[] outputImage)
			throws LabelServiceException {
		try {
			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			PrintService printService = (PrintService) frame
					.getPrinterComboBox().getSelectedItem();
			DocPrintJob printerJob = printService.createPrintJob();
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			Doc doc = new SimpleDoc(outputImage, flavor, null);
			printerJob.print(doc, pras);
		} catch (PrintException e) {
			throw new LabelServiceException("Unable to print", e);
		}
	}

	public void logEvent(String xmlFilePath, String status, String message,
			Exception e) {
		addMessageToTable(xmlFilePath, status, message);
		if (e != null && e.getMessage() != null) {

			if (e instanceof LabelServiceException
					&& ((LabelServiceException) e).getOriginalCause() != null
					&& (((LabelServiceException) e).getOriginalCause()) instanceof LabelServiceException) {
				writeTofile(
						xmlFilePath,
						status,
						message
								+ ((LabelServiceException) ((LabelServiceException) e)
										.getOriginalCause()).getXmlpiMessage()
								+ ": Exception is " + e.getMessage());
			} else {
				writeTofile(xmlFilePath, status, message + ": exception is "
						+ e.getMessage());
			}

		} else {
			writeTofile(xmlFilePath, status, message);
		}
	}

	/**
	 * @param xmlFilePath
	 * @param shipmentValidateResponse
	 * @param outputImage
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void saveLabelInFile(String xmlFilePath, String awb,
			byte[] outputImage, List<LabelLayout> labelLayouts, String docName)
			throws LabelServiceException {
		String fileName = "";
		try {
			 if (null != labelLayouts && labelLayouts.size() == 2) {
				fileName = frame.getPdfLocTextField().getText() + "/" + awb
						+ ".pdf";
			} //BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
			else if(null != labelLayouts && labelLayouts.size()==1) {
				for (LabelLayout labelLayout : labelLayouts) {
					if (LabelLayout.ECOM.equals(labelLayout)) {
						//Updated below from Global_Label to Transport_Label :: Pavan Kumar (TechMahindra) | 25-JUL-2013 | XMLPI 4.7
						fileName = frame.getPdfLocTextField().getText() + "/"
								+ "Transport_Label_" + awb + ".pdf";
					} else if (LabelLayout.ARCH.equals(labelLayout)) {
						//Updated below from Archive_Label to Archive_Document :: Pavan Kumar (TechMahindra) | 25-JUL-2013 | XMLPI 4.7
						fileName = frame.getPdfLocTextField().getText() + "/"
								+ "Archive_Document_" + awb + ".pdf";
					} else if (LabelLayout.CI.equals(labelLayout)) {
						//Updated below from Commercial Invoice Document :: Pavan Kumar (TechMahindra) | 11-MAY-2017 | XMLPI 6.1
						fileName = frame.getPdfLocTextField().getText() + "/"
								+ awb + "_CI.pdf";
					} 
				}
			}else{
				if("CustomInvoiceImage".equalsIgnoreCase(docName)) {
					fileName = frame.getPdfLocTextField().getText() + "/"
							+ awb + "_CI.pdf";
				} else {
					fileName = frame.getPdfLocTextField().getText() + "/"
							+ "Shipment_Receipt_Document_" + awb + ".pdf";
				}
			}
			//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
			BufferedOutputStream bos = null;
			FileOutputStream fos = new FileOutputStream(new File(fileName));
			bos = new BufferedOutputStream(fos);
			// To write byte array to file use, public void write(byte[] b)
			// method of BufferedOutputStream class.
			bos.write(outputImage);
			bos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			throw new LabelServiceException("Unable to create file", e);
		} catch (IOException e) {
			throw new LabelServiceException("Unable to write to file", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void configurePrinterComboBox() {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
				null, null);
		for (PrintService printService : printServices) {
			frame.getPrinterComboBox().addItem(printService);
		}
		PrintService printService = PrintServiceLookup
				.lookupDefaultPrintService();
		frame.getPrinterComboBox().setSelectedItem(printService);
	}

	private void configureStatusTable() {
		tableModel = new DefaultTableModel() {
			/** serialVersionUID */
			private static final long serialVersionUID = 2119378871290617284L;

			@Override
			public boolean isCellEditable(int paramInt1, int paramInt2) {
				return false;
			}
		};
		tableModel.addColumn("File Name");
		tableModel.addColumn("Status");
		tableModel.addColumn("Message");
		frame.getStatustable().setModel(tableModel);

	}

	/**
	 * configure the file chooser
	 */
	private void configureFileChooser(String xmlFilePath) {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		File currentDir = new File(xmlFilePath);
		fileChooser.setCurrentDirectory(currentDir);
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				if (f.getName().toLowerCase().endsWith("xml")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Directories and XML Files";
			}

		};
		fileChooser.addChoosableFileFilter(filter);

	}

	/**
	 * configure the file chooser to select location
	 */
	private void configureFileChooserToLocn(String pdfPath) {
		fileChooserToLocn = new JFileChooser();
		fileChooserToLocn.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File currentDir = new File(pdfPath);
		fileChooserToLocn.setCurrentDirectory(currentDir);
		FileFilter dirFilter = new FileFilter() {

			@Override
			public boolean accept(File fi) {
				if (fi.isDirectory()) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Directories Only";
			}

		};
		fileChooserToLocn.addChoosableFileFilter(dirFilter);

	}

	/**
	 * Configures the browse button to show the File Chooser
	 * 
	 */
	private void configureBrowse() {
		frame.getBrowseButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (frame.getFilePathField().getText() != null) {
					fileChooser.setCurrentDirectory(new File(frame
							.getFilePathField().getText()));
				}
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					frame.getFilePathField().setText(file.getPath());
				}
				frame.getFilePathField().setCaretPosition(
						frame.getFilePathField().getDocument().getLength());
			}
		});
	}

	/*
	 * Configures the browse button to show the Directory to save file
	 */
	private void configurePDFBrowse() {
		frame.getPdfLocBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (frame.getPdfLocTextField().getText() != null) {
					fileChooserToLocn.setCurrentDirectory(new File(frame
							.getPdfLocTextField().getText()));
				}
				int returnVal = fileChooserToLocn.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file1 = fileChooserToLocn.getSelectedFile();
					frame.getPdfLocTextField().setText(file1.getPath());
				}
				frame.getPdfLocTextField().setCaretPosition(
						frame.getPdfLocTextField().getDocument().getLength());
			}
		});
	}

	/**
	 * 
	 *
	 */
	private void configureCloseButton() {
		frame.getCloseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
	}

	private void configureOutputFormatCombo() {
		frame.getOutputFormatComboBox().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				enableDisableFields();
			}
		});

	}

	/**
	 * 
	 */
	private void enableDisableFields() {
		String outputFormat = (String) frame.getOutputFormatComboBox()
				.getSelectedItem();
		boolean displayLocationFields = ("PDF").equals(outputFormat);
		frame.getPdfLocTextField().setEnabled(displayLocationFields);
		frame.getPdfLocBrowseButton().setEnabled(displayLocationFields);
		frame.getPrinterComboBox().setEnabled(!displayLocationFields);
	}

	/**
	 * 
	 *
	 */
	private void configureSubmitButton() {
		frame.getSubmitButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					doSubmit();
				} finally {
					frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					frame.repaint();
				}
			}
		});

	}

	public void doSubmit() {
		String xmlFilePath = frame.getFilePathField().getText();
		File xmlLocation = new File(xmlFilePath);
		if (xmlLocation.isFile()) {
			processXmlFile(xmlFilePath);
		} else {
			String[] xmlFileList = xmlLocation.list();
			if (xmlFileList == null) {
				logEvent(xmlFilePath, "Failure", "No xml file to process", null);
				return;
			}
			for (String xmlFile : xmlFileList) {
				if (!xmlFile.toLowerCase().endsWith("xml")) {
					logEvent(xmlFile, "Skipped", "Not an XML File", null);
					continue;
				}
				processXmlFile(xmlFilePath + "/" + xmlFile);
			}
		}
	}

	private void writeTofile(String xmlFilePath, String status, String error) {

		try {
			PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle
					.getBundle("label");
			String LogFilePath = bundle.getString("LOG_FILE_PATH");
			File file = new File(xmlFilePath);
			FileWriter outFile;
			outFile = new FileWriter(LogFilePath
					+ new SimpleDateFormat("yyyyMMdd").format(new Date())
					+ ".txt", true);
			BufferedWriter outStream = new BufferedWriter(outFile);
			outStream.newLine();
			outStream.write((new Date()).toString());
			outStream.write('\n' + "Filename : " + file.getName() + '\n'
					+ "Status : " + status + '\n' + "Message : " + error);
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getAbsoluteFilePath(String filePath) {
		return new File(filePath).getAbsolutePath();
	}

	private void processFile(String xmlFilePath,
			ShipmentValidateResponse shipmentValidateResponse, String hideAccountFlag)
			throws LabelServiceException {
		String labelFormat = (String) frame.getOutputFormatComboBox()
				.getSelectedItem();
		List<LabelLayout> labelLayouts = new ArrayList<LabelLayout>();
		setLabelLayout(labelLayouts);
		
		//BEGIN :: Ravi Rastogi (MSAT) | 24-JUL-2013 | XMLPI 4.7 | Resolution option
		String resolution = (String) frame.getResolutionComboBox().getSelectedItem();
		//END
		
		//BEGIN :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
		String labelTemplate = (String) frame.getLabelTemplateComboBox().getSelectedItem();
		boolean dhlLogoFlag = frame.getDhlLogoCheckBox().isSelected();
		boolean companyLogoFlag = frame.getCompanyLogoCheckBox().isSelected();
		//END
		//Updated the below with labelTemplate, dhlLogoFlag and companyLogoFlag parameters
		String responseXmlString = labelServiceController.processFile(
				xmlFilePath, labelFormat, labelLayouts,
				shipmentValidateResponse,resolution, labelTemplate, dhlLogoFlag, companyLogoFlag, hideAccountFlag);
		String strRootElement = getCompleteRootElement(responseXmlString);
		if (strRootElement.equals("res:LabelResponse")) {
			LabelResponse labelResponse = labelServiceController
					.unmarshalLabelServResp(responseXmlString);
			List labelPrintCommandLst = labelResponse.getLabelPrintCommands();
			if (labelPrintCommandLst.size() > 0) {
				com.dhl.xmlpi.labelservice.model.response.LabelPrintCommands labelPrintCommands = (com.dhl.xmlpi.labelservice.model.response.LabelPrintCommands) labelPrintCommandLst
						.get(0);
				// based on format, either print or save
				
				handleLabelServiceResponse(
						labelPrintCommands.getLabelPrintCommand(),
						labelResponse.getAirwayBillNumber(), labelFormat,
						xmlFilePath, labelLayouts, null);
			} else {
				throw new LabelServiceException("No Label Print Command found");
			}
		} else if (strRootElement.equals("res:LabelErrorResponse")) {
			LabelErrorResponse labelErrorResponse = labelServiceController
					.unmarshalLabelServErrResp(responseXmlString);
			throw new LabelServiceException(labelErrorResponse.getResponse()
					.getStatus().getCondition().getConditionData());
		} else {
			throw new LabelServiceException("Error in XML-PI LabelService");
		}

		// move the file to processedXMLFilePath
		moveFile(xmlFilePath);
	}

	private void processFile(String xmlFilePath,
			ShipmentResponse shipmentResponse, String hideAccountFlag) throws LabelServiceException {
		String labelFormat = (String) frame.getOutputFormatComboBox()
				.getSelectedItem();
		List<LabelLayout> labelLayouts = new ArrayList<LabelLayout>();
		setLabelLayout(labelLayouts);
		//BEGIN :: Ravi Rastogi (MSAT) | 24-JUL-2013 | XMLPI 4.7 | Resolution option
		String resolution = (String) frame.getResolutionComboBox().getSelectedItem();
		//END
		
		//BEGIN :: Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
		String labelTemplate = (String) frame.getLabelTemplateComboBox().getSelectedItem();
		boolean dhlLogoFlag = frame.getDhlLogoCheckBox().isSelected();
		boolean companyLogoFlag = frame.getCompanyLogoCheckBox().isSelected();
		//END
		//Updated the below with labelTemplate, dhlLogoFlag and companyLogoFlag parameters
		String responseXmlString = labelServiceController.processFile(
				xmlFilePath, labelFormat, labelLayouts, shipmentResponse, resolution, labelTemplate, dhlLogoFlag, companyLogoFlag, hideAccountFlag);
		String strRootElement = getCompleteRootElement(responseXmlString);
		
		if (strRootElement.equals("res:LabelResponse")) {
			LabelResponse labelResponse = labelServiceController
					.unmarshalLabelServResp(responseXmlString);
			List labelPrintCommandLst = labelResponse.getLabelPrintCommands();
			if (labelPrintCommandLst.size() > 0) {
				com.dhl.xmlpi.labelservice.model.response.LabelPrintCommands labelPrintCommands = (com.dhl.xmlpi.labelservice.model.response.LabelPrintCommands) labelPrintCommandLst
						.get(0);
				// based on format, either print or save
				handleLabelServiceResponse(
						labelPrintCommands.getLabelPrintCommand(),
						labelResponse.getAirwayBillNumber(), labelFormat,
						xmlFilePath, labelLayouts, null);
			} else {
				throw new LabelServiceException("No Label Print Command found");
			}
		} else if (strRootElement.equals("res:LabelErrorResponse")) {
			LabelErrorResponse labelErrorResponse = labelServiceController
					.unmarshalLabelServErrResp(responseXmlString);
			throw new LabelServiceException(labelErrorResponse.getResponse()
					.getStatus().getCondition().getConditionData());
			/*
			 * throw new LabelServiceException(labelErrorResponse.getResponse()
			 * .getNote().getCondition().getConditionData());
			 */
		} else {
			throw new LabelServiceException("Error in XML-PI LabelService");
		}

		// move the file to processedXMLFilePath
		moveFile(xmlFilePath);
	}

	private void setLabelLayout(List<LabelLayout> labelLayouts) {
		if (frame.getArchiveRadioButton().isSelected()) {
			labelLayouts.add(LabelLayout.ARCH);
		}
		if (frame.getGlobalRadioButton().isSelected()) {
			labelLayouts.add(LabelLayout.ECOM);
		}
		if (frame.getCustomInvoiceRadioButton().isSelected()) {
			labelLayouts.add(LabelLayout.ECOM);
		}
		if (frame.getAllTypeRadioButton().isSelected()) {
			labelLayouts.add(LabelLayout.ECOM);
			labelLayouts.add(LabelLayout.ARCH);
		}
	}

	private void moveFile(String xmlFilePath) {
		File xmlFile = new File(xmlFilePath);
		String xmlFileName = xmlFile.getName();
		File processedFile = new File(processedXMLFilePath + "/" + xmlFileName);
		xmlFile.renameTo(processedFile);
	}

	private void handleLabelServiceResponse(byte[] labelBytes, String awb,
			String labelFormat, String xmlFilePath,
			List<LabelLayout> labelLayout, String docName) throws LabelServiceException {
		if ("PDF".equals(labelFormat)) {
			saveLabelInFile(xmlFilePath, awb, labelBytes, labelLayout, docName);
		} else {
			printLabel(xmlFilePath, labelBytes);
		}
	}

	private void addMessageToTable(String filePath, String status,
			String message) {
		File file = new File(filePath);
		tableModel.insertRow(0, new String[] { file.getName(), status, message });
		frame.getStatustable().updateUI();

	}

	/**
	 * 
	 *
	 */
	public void show() {
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		while (frame.isVisible()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// DO Nothing
			}
		}
		frame.dispose();
	}

	public String getXmlFilePath() {
		return frame.getFilePathField().getText();
	}

	public String getlabelPrinterController() {
		return null;
	}

	public String getPdfPath() {
		return frame.getPdfLocTextField().getText();
	}

	public String getLabelFormat() {
		return frame.getOutputFormatComboBox().getSelectedItem().toString();
	}

	public String getCompleteRootElement(String message)
			throws LabelServiceException {
		String methodname = "LabelServiceController|getCompleteRootElement";
		String rootElement = null;

		try {
			StringTokenizer st = new StringTokenizer(message.trim(), "<>", true);

			String value = null;
			int index = 0;
			while (st.hasMoreTokens()) {
				value = st.nextToken();

				if (value.equals("<")) {
					rootElement = st.nextToken();

					if (!rootElement.startsWith("?")
							&& !rootElement.startsWith("!")) {
						index = rootElement.indexOf(" ");
						if (index != -1) {
							rootElement = rootElement.substring(0, index);
						}
						return rootElement;
					}
				}
			}
		} catch (Exception e) {
			throw new LabelServiceException(e.getMessage());
		}
		return rootElement;
	}
	
	/**
	 * This method is to display "Company Logo name" beside the checkbox, if "Company Logo" option is checked
	 * Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	 */
	private void configureCompanyLogo() {
		frame.getCompanyLogoCheckBox().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if(frame.getCompanyLogoCheckBox().isSelected()) {
					PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle
							.getBundle("label");
					String custCompanyLogo = bundle.getString("CustCompanyLogo");
					frame.getCompanyLogoCheckBox().setText(custCompanyLogo);
				} else {
					frame.getCompanyLogoCheckBox().setText("");
				}
			}
		});

	}
	
	/**
	 * This method will perform action when label template is selected. 
	 * And to display only PDF in "Output Format" option, if "PDF" related label templates are selected 
	 * Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	 */
	private void configureLabelTemplate() {
		frame.getLabelTemplateComboBox().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				enableOutputFormatCombo();
			}
		});

	}
	
	/**
	 * This method is to display only PDF in "Output Format" option, if "PDF" related label templates selected 
	 * Pavan Kumar (TechMahindra) | 01-AUG-2013 | XMLPI 4.7 | Label Enhancement
	 */
	private void enableOutputFormatCombo() {
		String labelTemplate = (String) frame.getLabelTemplateComboBox()
				.getSelectedItem();
		//BEGIN :: Added below New value for XMLPI Label Enhancement :: Rajesh Nagampurath :: 08-DEC-2014 | XMLPI Label Enhancement | XML_PI_v52_Cyrillic
		boolean displayOutputFormat = ("8X4_A4_PDF").equals(labelTemplate) || ("8X4_A4_TC_PDF").equals(labelTemplate) || ("6X4_A4_PDF").equals(labelTemplate) || ("8X4_CI_PDF").equals(labelTemplate) || ("8X4_RU_A4_PDF").equals(labelTemplate) 
				|| ("6X4_PDF".equals(labelTemplate)) || ("8X4_PDF".equals(labelTemplate)) || ("8X4_CustBarCode_PDF".equals(labelTemplate));
		//END :: Rajesh Nagampurath :: 08-DEC-2014 | XMLPI Label Enhancement | XML_PI_v52_Cyrillic
		if(displayOutputFormat) {
			frame.getOutputFormatComboBox().setEnabled(false);
			frame.getOutputFormatComboBox().setSelectedItem("PDF");
		} else {
			frame.getOutputFormatComboBox().setEnabled(true);
		}
	}
	
	//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
	private void configureDecodeCheckbox() {
		frame.getDecodeCheckBox().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				enableOrDisableFieldsBasedOnDecodeChkBox();
				
			}
		});
		
	}
	//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
	//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 23-JUNE-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt	
	private void enableOrDisableDecodeCheckbox() {
		PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle
				.getBundle("label");
		String enableDecodeBoxChked ="true";
		try{
			enableDecodeBoxChked = bundle.getString("DefaultDecode")!=null?bundle.getString("DefaultDecode").trim():"true";
		}catch (Exception e) {
			
		}
		
		if(enableDecodeBoxChked.equalsIgnoreCase("true")){
			frame.getDecodeCheckBox().setSelected(true);
			enableOrDisableFieldsBasedOnDecodeChkBox();
		}
	}
	//END :: Rajesh Nagampurath :: 23-JUNE-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt

/**
 * Enable or disable field based on the decode checkBox
 */
	//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 23-JUNE-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt	
	private void enableOrDisableFieldsBasedOnDecodeChkBox() {
		if(frame.getDecodeCheckBox().isSelected()) {
			//disable Label type radio buttons
			frame.getArchiveRadioButton().setEnabled(false);
			
			frame.getGlobalRadioButton().setEnabled(false);
			
			frame.getCustomInvoiceRadioButton().setEnabled(false);
			
			frame.getAllTypeRadioButton().setEnabled(false);
			
			if(!(frame.getPdfLocBrowseButton().isEnabled())){
				frame.getPdfLocBrowseButton().setEnabled(true);
			}
			if(!(frame.getPdfLocTextField().isEnabled())){
				frame.getPdfLocTextField().setEnabled(true);
			}
			
			//Disable Label Type, Label Template, Output Format and Resolution
			frame.getLabelTemplateComboBox().setEnabled(false);
			//frame.getLabelTemplateComboBox().setSelectedItem("8X4_A4_PDF");
			
			frame.getOutputFormatComboBox().setEnabled(false);
			//frame.getOutputFormatComboBox().setSelectedItem("PDF");
			
			frame.getResolutionComboBox().setEnabled(false);
			//frame.getResolutionComboBox().setSelectedItem("200");
			
			//Enable printer and output PDF Location
			frame.getPrinterComboBox().setEnabled(true);
		} else {
			
			frame.getArchiveRadioButton().setEnabled(true);
			
			frame.getGlobalRadioButton().setEnabled(true);
			
			frame.getCustomInvoiceRadioButton().setEnabled(true);
			
			frame.getAllTypeRadioButton().setEnabled(true);
			
			frame.getLabelTemplateComboBox().setEnabled(true);
			
			frame.getResolutionComboBox().setEnabled(true);
			
			frame.getPrinterComboBox().setEnabled(false);
		}
	}
	//END :: Rajesh Nagampurath :: 23-JUNE-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
	
	private void processCommercialInvoice(String xmlFilePath, ShipmentResponse shipmentResponse) throws LabelServiceException {
		
		boolean dhlLogoFlag = frame.getDhlLogoCheckBox().isSelected();
		boolean companyLogoFlag = frame.getCompanyLogoCheckBox().isSelected();

		String responseXmlString = labelServiceController.processCommercialInvoice(xmlFilePath, shipmentResponse, dhlLogoFlag, companyLogoFlag);
		
		String strRootElement = getCompleteRootElement(responseXmlString);
		
		if (strRootElement.equals("res:CommercialInvoiceResponse")) {
			CommercialInvoiceResponse commercialInvoiceResponse = labelServiceController
					.unmarshalCommercialInvoiceResp(responseXmlString);
			List labelPrintCommandLst = commercialInvoiceResponse.getLabelPrintCommands();
			if (labelPrintCommandLst.size() > 0) {
				com.dhl.xmlpi.labelservice.model.response.LabelPrintCommands labelPrintCommands = (com.dhl.xmlpi.labelservice.model.response.LabelPrintCommands) labelPrintCommandLst
						.get(0);
				// based on format, either print or save
				handleLabelServiceResponse(
						labelPrintCommands.getLabelPrintCommand(),
						commercialInvoiceResponse.getAirwayBillNumber(), "PDF", xmlFilePath, null, "CustomInvoiceImage");
			} else {
				throw new LabelServiceException("No Label Print Command found");
			}
		} else if (strRootElement.equals("res:CommercialInvoiceErrorResponse")) {
			CommercialInvoiceErrorResponse commercialInvoiceErrorResponse = labelServiceController
					.unmarshalCommercialInvErrResp(responseXmlString);
			throw new LabelServiceException(
					commercialInvoiceErrorResponse.getResponse().getStatus().getCondition().getConditionData());
		} else {
			throw new LabelServiceException("Error in XML-PI LabelService");
		}
		moveFile(xmlFilePath);
	}
	
	private void configureDocumentTypeOption() {

		frame.getCustomInvoiceRadioButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (frame.getCustomInvoiceRadioButton().isSelected()) {
					frame.getLabelTemplateComboBox().setEnabled(false);
					frame.getOutputFormatComboBox().setEnabled(false);
					frame.getResolutionComboBox().setEnabled(false);
					frame.getPdfLocBrowseButton().setEnabled(true);
					frame.getPrinterComboBox().setEnabled(false);
				}
			}
		});

		frame.getArchiveRadioButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getArchiveRadioButton().isSelected()) {
					frame.getGlobalRadioButton().setEnabled(true);
					frame.getCustomInvoiceRadioButton().setEnabled(true);
					frame.getAllTypeRadioButton().setEnabled(true);
					frame.getLabelTemplateComboBox().setEnabled(true);
					frame.getResolutionComboBox().setEnabled(true);
					frame.getPrinterComboBox().setEnabled(true);
				}
			}
		});

		frame.getAllTypeRadioButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getAllTypeRadioButton().isSelected()) {
					frame.getArchiveRadioButton().setEnabled(true);
					frame.getGlobalRadioButton().setEnabled(true);
					frame.getCustomInvoiceRadioButton().setEnabled(true);
					frame.getLabelTemplateComboBox().setEnabled(true);
					frame.getResolutionComboBox().setEnabled(true);
					frame.getPrinterComboBox().setEnabled(true);
				}
			}
		});

		frame.getGlobalRadioButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getGlobalRadioButton().isSelected()) {
					frame.getArchiveRadioButton().setEnabled(true);
					frame.getCustomInvoiceRadioButton().setEnabled(true);
					frame.getAllTypeRadioButton().setEnabled(true);
					frame.getLabelTemplateComboBox().setEnabled(true);
					frame.getResolutionComboBox().setEnabled(true);
					frame.getPrinterComboBox().setEnabled(true);
				}
			}
		});
	}
}
