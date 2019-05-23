/**
 * @author himanshu_agnihotri
 * This class merges the archive label pdf and global 
 * label pdf into one pdf file  
 */
package com.dhl.sop.label;


import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

public class PdfMerger {

	/**
	 * This method merges the archive label pdf and global 
     * label pdf into one pdf file  
	 * 
	 * @param ArrayList
	 *            args request xml file path
	 *            this list consist path of source 
	 *            files and destination file.
	 *
	 * @return void
	 *            generate merged pdf file.
	 */ 
    public static void getConcatenatedPdf(ArrayList<String> args) {
    	
        if (args.size() < 2) {
            System.err.println("arguments: file1 [file2 ...] destfile");
        }
        else {
        	System.out.println("Pdf Merging");
            try {
                int pageOffset = 0;
                ArrayList<Object> master = new ArrayList<Object>();
                int f = 0;
                String outFile = args.get(args.size()-1);
                Document document = null;
                PdfCopy  writer = null;
                while (f < args.size()-1) {
                    // create a reader for a certain document
                    PdfReader reader = new PdfReader(args.get(f));
                    reader.consolidateNamedDestinations();
                    // retrieve the total number of pages
                    int n = reader.getNumberOfPages();
                    n=n-1;
                    List<Object> bookmarks = SimpleBookmark.getBookmark(reader);
                    if (bookmarks != null) {
                        if (pageOffset != 0)
                            SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
                        master.addAll(bookmarks);
                    }
                    pageOffset += n;
                    
                    if (f == 0) {
                        // step 1: creation of a document-object
                        document = new Document(reader.getPageSizeWithRotation(1));
                        // step 2: create a writer that listens to the document
                        writer = new PdfCopy(document, new FileOutputStream(outFile));
                        // step 3: open the document
                        document.open();
                    }
                    // step 4: add content
                    PdfImportedPage page;
                    for (int i = 0; i < n; ) {
                        ++i;
                        page = writer.getImportedPage(reader, i);
                        writer.addPage(page);
                    }
                    PRAcroForm form = reader.getAcroForm();
                    if (form != null)
                        writer.copyAcroForm(reader);
                    f++;
                }
                if (!master.isEmpty())
                    writer.setOutlines(master);
                // step 5: close the document
                document.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
