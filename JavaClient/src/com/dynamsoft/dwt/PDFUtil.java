package com.dynamsoft.dwt;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PDFUtil {
    public static byte[] createPDFFromImage(byte[] byteArray)
        throws IOException
    {
    	InputStream input = null;
    	byte[] ret = null;
        // the document
        PDDocument doc = null;
    	PDPageContentStream contents = null;
        try
        {
        	doc = new PDDocument();
        	// a valid PDF document requires at least one page
        	PDPage page = new PDPage();
        	doc.addPage(page);
        	
        	
        	input = new ByteArrayInputStream(byteArray);
        	BufferedImage img = ImageIO.read(input);
        	PDImageXObject pdImage = LosslessFactory.createFromImage(doc, img);
        	input.close();
        	input=null;
        	
        	
    		contents = new PDPageContentStream(doc, page);
    		contents.drawImage(pdImage, 0, 0);
    		contents.close();

    		ByteArrayOutputStream output = new ByteArrayOutputStream();
            doc.save(output);
            doc.close();
            doc = null;
            
            ret = output.toByteArray();
        }
        finally
        {
        	if(null != input) {
            	input.close();
        	}
        	
        	if(null != contents) {
        		contents.close();
        	}
        	
            if( doc != null )
            {
                doc.close();
            }
        }
        
        return ret;
    }

	public static void saveToFile(byte[] binary, String filePath) {


		try {
			File f = new File(filePath);
			
			FileOutputStream fs = new FileOutputStream(f);
			
			fs.write(binary);
			
			fs.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static byte[] createPDFFromURL(URL url) throws IOException {

    	InputStream input = null;
    	byte[] ret = null;
        // the document
        PDDocument doc = null;
    	PDPageContentStream contents = null;
        try
        {
        	doc = new PDDocument();
        	// a valid PDF document requires at least one page
        	PDPage page = new PDPage();
        	doc.addPage(page);
        	
            BufferedImage image = ImageIO.read(url);
        	PDImageXObject pdImage = LosslessFactory.createFromImage(doc, image);
        	
        	
    		contents = new PDPageContentStream(doc, page);
    		contents.drawImage(pdImage, 0, 0);
    		contents.close();

    		ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            doc.save(output);
            
            ret = output.toByteArray();
        }
        finally
        {
        	if(null != input) {
            	input.close();
        	}
        	
        	if(null != contents) {
        		contents.close();
        	}
        	
            if( doc != null )
            {
                doc.close();
            }
        }
        
        return ret;
	}

}