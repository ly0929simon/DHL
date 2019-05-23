package com.dhl.xmlpi.labelservice.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import info.clearthought.layout.*;

public class LabelPrinterView extends JFrame {
	public LabelPrinterView() {
		initComponents();
	}

	public JTextField getFilePathField() {
		return filePathField;
	}

	public JButton getBrowseButton() {
		return browseButton;
	}

	public JRadioButton getGlobalRadioButton() {
		return globalRadioButton;
	}

	public JRadioButton getArchiveRadioButton() {
		return archiveRadioButton;
	}

	public JRadioButton getAllTypeRadioButton() {
		return allTypeRadioButton;
	}

	public JComboBox getLabelTemplateComboBox() {
		return LabelTemplateComboBox;
	}

	public JComboBox getOutputFormatComboBox() {
		return outputFormatComboBox;
	}

	public JComboBox getResolutionComboBox() {
		return resolutionComboBox;
	}

	public JLabel getPdfLocLabel() {
		return pdfLocLabel;
	}

	public JTextField getPdfLocTextField() {
		return pdfLocTextField;
	}

	public JButton getPdfLocBrowseButton() {
		return pdfLocBrowseButton;
	}

	public JComboBox getPrinterComboBox() {
		return printerComboBox;
	}

	public JCheckBox getDhlLogoCheckBox() {
		return dhlLogoCheckBox;
	}

	public JCheckBox getCompanyLogoCheckBox() {
		return companyLogoCheckBox;
	}

	//BEGIN :: Added below new implementation for decode checkbox :: Rajesh Nagampurath :: 15-MAY-2015 | Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
	public JCheckBox getDecodeCheckBox() {
		return decodeCheckBox;
	}
	//END :: Rajesh Nagampurath :: 15-MAY-2015 | XMLPI Shipment Receipt CR | XML_PI_v52_Cyrillic_ShpRcpt
	
	public JButton getSubmitButton() {
		return submitButton;
	}

	public JButton getCloseButton() {
		return closeButton;
	}

	public JTable getStatustable() {
		return statustable;
	}
	
	public JCheckBox gethideAccount() {
		return this.hideAccount;
	}

	public JRadioButton getCustomInvoiceRadioButton() {
		return customInvoiceRadioButton;
	}

	public JCheckBox getHideAccount() {
		return hideAccount;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Pavan Appalaraju
		labelPanel = new JPanel();
		panel6 = new JPanel();
		panel2 = new JPanel();
		label3 = new JLabel();
		filePathField = new JTextField();
		browseButton = new JButton();
		label4 = new JLabel();
		panel7 = new JPanel();
		globalRadioButton = new JRadioButton();
		archiveRadioButton = new JRadioButton();
		customInvoiceRadioButton = new JRadioButton();
		allTypeRadioButton = new JRadioButton();
		label5 = new JLabel();
		LabelTemplateComboBox = new JComboBox();
		label1 = new JLabel();
		outputFormatComboBox = new JComboBox();
		label6 = new JLabel();
		resolutionComboBox = new JComboBox();
		pdfLocLabel = new JLabel();
		pdfLocTextField = new JTextField();
		pdfLocBrowseButton = new JButton();
		label2 = new JLabel();
		printerComboBox = new JComboBox();
		label7 = new JLabel();
		dhlLogoCheckBox = new JCheckBox();
		label8 = new JLabel();
		companyLogoCheckBox = new JCheckBox();
		label10 = new JLabel();
		hideAccount = new JCheckBox();
		label9 = new JLabel();
		decodeCheckBox = new JCheckBox();
		panel5 = new JPanel();
		submitButton = new JButton();
		closeButton = new JButton();
		panel4 = new JPanel();
		scrollPane1 = new JScrollPane();
		statustable = new JTable();

		//======== this ========
		setTitle("XML Services v6.2 Label Utility");
		setIconImage(new ImageIcon(getClass().getResource("/LabelUtilityIcon.PNG")).getImage().getScaledInstance(60, 60,  java.awt.Image.SCALE_SMOOTH));
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new TableLayout(new double[][] {
			{2, TableLayout.PREFERRED, 2},
			{2, TableLayout.PREFERRED, 2}}));
		((TableLayout)contentPane.getLayout()).setHGap(5);
		((TableLayout)contentPane.getLayout()).setVGap(5);

		//======== labelPanel ========
		{

			// JFormDesigner evaluation mark
			labelPanel.setLayout(new TableLayout(new double[][] {
				{TableLayout.PREFERRED},
				{5, TableLayout.PREFERRED, 5, 200}}));
			((TableLayout)labelPanel.getLayout()).setHGap(2);
			((TableLayout)labelPanel.getLayout()).setVGap(2);

			//======== panel6 ========
			{
				panel6.setBorder(new TitledBorder("Shipment Label"));
				panel6.setLayout(new TableLayout(new double[][] {
					{TableLayout.PREFERRED},
					{TableLayout.PREFERRED, 5, TableLayout.PREFERRED}}));

				//======== panel2 ========
				{
					panel2.setLayout(new TableLayout(new double[][] {
						{TableLayout.PREFERRED, 400, TableLayout.PREFERRED},
						{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
					((TableLayout)panel2.getLayout()).setHGap(5);
					((TableLayout)panel2.getLayout()).setVGap(8);

					//---- label3 ----
					label3.setText("Shipment Response XML File: ");
					label3.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label3, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					panel2.add(filePathField, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- browseButton ----
					browseButton.setText("Browse");
					browseButton.setFont(new Font("Arial", Font.PLAIN, 12));
					browseButton.setMnemonic('B');
					panel2.add(browseButton, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label4 ----
					label4.setText("Document Type:");
					label4.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label4, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//======== panel7 ========
					{
						panel7.setLayout(new TableLayout(new double[][] {
							{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED},
							{TableLayout.PREFERRED, 0}}));
						((TableLayout)panel7.getLayout()).setHGap(5);
						((TableLayout)panel7.getLayout()).setVGap(5);

						//---- globalRadioButton ----
						globalRadioButton.setText("Transport");
						globalRadioButton.setFont(new Font("Arial", Font.PLAIN, 12));
						panel7.add(globalRadioButton, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- archiveRadioButton ----
						archiveRadioButton.setText("Archive");
						archiveRadioButton.setFont(new Font("Arial", Font.PLAIN, 12));
						panel7.add(archiveRadioButton, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- customInvoiceRadioButton ----
						customInvoiceRadioButton.setText("Custom Invoice");
						customInvoiceRadioButton.setFont(new Font("Arial", Font.PLAIN, 12));
						panel7.add(customInvoiceRadioButton, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- allTypeRadioButton ----
						allTypeRadioButton.setText("All");
						allTypeRadioButton.setFont(new Font("Arial", Font.PLAIN, 12));
						allTypeRadioButton.setSelected(true);
						panel7.add(allTypeRadioButton, new TableLayoutConstraints(3, 0, 3, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					}
					panel2.add(panel7, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label5 ----
					label5.setText("Label Template:");
					label5.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label5, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- LabelTemplateComboBox ----
					LabelTemplateComboBox.setModel(new DefaultComboBoxModel(new String[] {
						"8X4_A4_PDF",
						"8X4_thermal",
						"8X4_A4_TC_PDF",
						"8X4_CI_PDF",
						"8X4_CI_thermal",
						"6X4_A4_PDF",
						"6X4_thermal",
						"8X4_RU_A4_PDF",
						"6X4_PDF",
						"8X4_PDF",
						"8X4_CustBarCode_PDF",
						"8X4_CustBarCode_thermal"
					}));
					LabelTemplateComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(LabelTemplateComboBox, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label1 ----
					label1.setText("Output Format:");
					label1.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label1, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- outputFormatComboBox ----
					outputFormatComboBox.setModel(new DefaultComboBoxModel(new String[] {
						"PDF",
						"EPL2",
						"ZPL2"
					}));
					outputFormatComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(outputFormatComboBox, new TableLayoutConstraints(1, 3, 1, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label6 ----
					label6.setText("Resolution:");
					label6.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label6, new TableLayoutConstraints(0, 4, 0, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- resolutionComboBox ----
					resolutionComboBox.setModel(new DefaultComboBoxModel(new String[] {
						"200",
						"300"
					}));
					resolutionComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(resolutionComboBox, new TableLayoutConstraints(1, 4, 1, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- pdfLocLabel ----
					pdfLocLabel.setText("Output PDF Location:");
					pdfLocLabel.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(pdfLocLabel, new TableLayoutConstraints(0, 5, 0, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- pdfLocTextField ----
					pdfLocTextField.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(pdfLocTextField, new TableLayoutConstraints(1, 5, 1, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- pdfLocBrowseButton ----
					pdfLocBrowseButton.setText("Browse");
					pdfLocBrowseButton.setFont(new Font("Arial", Font.PLAIN, 12));
					pdfLocBrowseButton.setMnemonic('R');
					panel2.add(pdfLocBrowseButton, new TableLayoutConstraints(2, 5, 2, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label2 ----
					label2.setText("Printer:");
					label2.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label2, new TableLayoutConstraints(0, 6, 0, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- printerComboBox ----
					printerComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(printerComboBox, new TableLayoutConstraints(1, 6, 1, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label7 ----
					label7.setText("DHL Logo:");
					label7.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label7, new TableLayoutConstraints(0, 7, 0, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					panel2.add(dhlLogoCheckBox, new TableLayoutConstraints(1, 7, 1, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label8 ----
					label8.setText("Company Logo:");
					label8.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label8, new TableLayoutConstraints(0, 8, 0, 8, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					panel2.add(companyLogoCheckBox, new TableLayoutConstraints(1, 8, 1, 8, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label10 ----
					label10.setText("Hide Account:");
					label10.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label10, new TableLayoutConstraints(0, 9, 0, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					panel2.add(hideAccount, new TableLayoutConstraints(1, 9, 1, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label9 ----
					label9.setText("Decode:");
					label9.setFont(new Font("Arial", Font.PLAIN, 12));
					panel2.add(label9, new TableLayoutConstraints(0, 10, 0, 10, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					panel2.add(decodeCheckBox, new TableLayoutConstraints(1, 10, 1, 10, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				}
				panel6.add(panel2, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//======== panel5 ========
				{
					panel5.setLayout(new TableLayout(new double[][] {
						{TableLayout.PREFERRED, TableLayout.PREFERRED},
						{TableLayout.PREFERRED}}));
					((TableLayout)panel5.getLayout()).setHGap(5);
					((TableLayout)panel5.getLayout()).setVGap(5);

					//---- submitButton ----
					submitButton.setText("Submit");
					submitButton.setFont(new Font("Arial", Font.PLAIN, 12));
					submitButton.setMnemonic('S');
					panel5.add(submitButton, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- closeButton ----
					closeButton.setText("Close");
					closeButton.setFont(new Font("Arial", Font.PLAIN, 12));
					closeButton.setHorizontalAlignment(SwingConstants.RIGHT);
					closeButton.setMnemonic('C');
					panel5.add(closeButton, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				}
				panel6.add(panel5, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
			}
			labelPanel.add(panel6, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

			//======== panel4 ========
			{
				panel4.setBorder(new TitledBorder("File Processing Status"));
				panel4.setLayout(new TableLayout(new double[][] {
					{TableLayout.FILL},
					{TableLayout.FILL}}));

				//======== scrollPane1 ========
				{
					scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

					//---- statustable ----
					statustable.setModel(new DefaultTableModel(
						new Object[][] {
							{null, null, null},
						},
						new String[] {
							"File Name", "Status", "Message"
						}
					) {
						Class<?>[] columnTypes = new Class<?>[] {
							String.class, String.class, String.class
						};
						boolean[] columnEditable = new boolean[] {
							false, false, false
						};
						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
						@Override
						public boolean isCellEditable(int rowIndex, int columnIndex) {
							return columnEditable[columnIndex];
						}
					});
					statustable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
					statustable.setFont(new Font("Arial", Font.PLAIN, 12));
					statustable.setPreferredScrollableViewportSize(new Dimension(450, 100));
					scrollPane1.setViewportView(statustable);
				}
				panel4.add(scrollPane1, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
			}
			labelPanel.add(panel4, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
		}
		contentPane.add(labelPanel, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));
		pack();
		setLocationRelativeTo(getOwner());

		//---- labelTypeButtonGroup ----
		ButtonGroup labelTypeButtonGroup = new ButtonGroup();
		labelTypeButtonGroup.add(globalRadioButton);
		labelTypeButtonGroup.add(archiveRadioButton);
		labelTypeButtonGroup.add(customInvoiceRadioButton);
		labelTypeButtonGroup.add(allTypeRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Pavan Appalaraju
	private JPanel labelPanel;
	private JPanel panel6;
	private JPanel panel2;
	private JLabel label3;
	private JTextField filePathField;
	private JButton browseButton;
	private JLabel label4;
	private JPanel panel7;
	private JRadioButton globalRadioButton;
	private JRadioButton archiveRadioButton;
	private JRadioButton customInvoiceRadioButton;
	private JRadioButton allTypeRadioButton;
	private JLabel label5;
	private JComboBox LabelTemplateComboBox;
	private JLabel label1;
	private JComboBox outputFormatComboBox;
	private JLabel label6;
	private JComboBox resolutionComboBox;
	private JLabel pdfLocLabel;
	private JTextField pdfLocTextField;
	private JButton pdfLocBrowseButton;
	private JLabel label2;
	private JComboBox printerComboBox;
	private JLabel label7;
	private JCheckBox dhlLogoCheckBox;
	private JLabel label8;
	private JCheckBox companyLogoCheckBox;
	private JLabel label10;
	private JCheckBox hideAccount;
	private JLabel label9;
	private JCheckBox decodeCheckBox;
	private JPanel panel5;
	private JButton submitButton;
	private JButton closeButton;
	private JPanel panel4;
	private JScrollPane scrollPane1;
	private JTable statustable;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
