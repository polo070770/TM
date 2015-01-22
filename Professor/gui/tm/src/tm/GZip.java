/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JTextArea;

/**
 *
 * @author droma
 */
public class GZip {

    private File fl;
    private ZipFile zFl;  
    private JTextArea console;
    private Boolean validZip = true;
//    private List<String> fileNames = new ArrayList<>();
    //private List<InputStream> inStream = new ArrayList<>();
//    public List<BufferedImage> imgStream = new ArrayList<>();

    public GZip() {
        this.validZip = false;
    }
    
    public GZip(File fl, JTextArea console) {
        this.fl = fl;
        try {
            this.zFl = new ZipFile(fl);
            this.validZip = true;
        } catch (ZipException e) {
            System.err.println("File " + fl.getAbsolutePath() + " is not a zip file.");
            this.validZip = false;
        } catch (IOException e) {
            System.err.println("I/O error reading file " + fl.getAbsolutePath());
            this.validZip = false;
        } catch (SecurityException e) {
            System.err.println("File " + fl.getAbsolutePath() + " not readable.");
            this.validZip = false;
        }
        this.console = console;
    }
    
    public Boolean isValidZip() {
        return this.validZip;
    }
    
    public Boolean setFile(File fl) {
        this.fl = fl;
        try {
            this.zFl = new ZipFile(fl);
            this.validZip = true;
        } catch (ZipException e) {
            System.err.println("File " + fl.getAbsolutePath() + " is not a zip file.");
            this.validZip = false;
        } catch (IOException e) {
            System.err.println("I/O error reading file " + fl.getAbsolutePath());
            this.validZip = false;
        } catch (SecurityException e) {
            System.err.println("File " + fl.getAbsolutePath() + " not readable.");
            this.validZip = false;
        }
        return this.validZip;
    }
     
    /*public void readZip() {
        if (this.validZip) {
            Enumeration<? extends ZipEntry> entries = zFl.entries();

            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if (! entry.isDirectory()) {
                    try {
                        this.inStream.add(this.zFl.getInputStream(entry));
                    } catch (IOException ex) {
                        Logger.getLogger(GZip.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } 
    }*/
    
    public ArrayList<Image> readZipImages() {
        ArrayList<Image> imgArray = new ArrayList<>();
        if (this.validZip) {
            Enumeration<? extends ZipEntry> entries = zFl.entries();

            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if (! entry.isDirectory()) {

                    String imgName = entry.getName();
                    String[] parts = imgName.split("/");
                    if (parts.length > 1)
                        imgName = parts[1];
                    try {
                        InputStream is = zFl.getInputStream(entry);
                        ImageInputStream iis = ImageIO.createImageInputStream(is);
                        BufferedImage bufImg = ImageIO.read(iis);
                        Image img = new Image(bufImg, imgName);
                        imgArray.add(img);
                    } catch (IOException ex) {
                        String msg = "Error reading image " + entry.getName() + "\n"; 
                        System.err.print(msg);
                        this.console.append(msg);
                        Logger.getLogger(GZip.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } 
        return imgArray;
    }
    
    public String writeZipImages(ArrayList<Image> imgArray, File outDir, String format,
    String fileName, Boolean modImage) 
            throws FileNotFoundException, IOException {
        File outFile = new File(outDir.toString() + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(outFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (Image img: imgArray) {
            ZipEntry e = new ZipEntry(img.getImgName() + "." + format);
            zos.putNextEntry(e);
            if (modImage)
                ImageIO.write( img.getImgBufMod(), format, zos);
            else
                ImageIO.write( img.getImgBuf(), format, zos);
            zos.closeEntry();
            zos.flush();
        }
        zos.close();
        fos.flush();
        fos.close();
        return outFile.getCanonicalPath();
    }
    
}
