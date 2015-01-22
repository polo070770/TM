/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author droma
 */
public class Codec extends Observable implements Runnable{
    private final float imgTh;
    private final short nTeselas;
    private final short nMov;
    private final short height;
    private final short width;
    public EncodedVideo encVid;
    private int idn;
    private final javax.swing.JTextArea console;
    private Boolean encDone;
    private Boolean decDone;
    private Thread th;
    
    public Codec(float imgTh, short nTeselas, short nMov, byte gop, 
            ArrayList<Image> imgArray, javax.swing.JTextArea console) {
        this.imgTh      = imgTh;
        this.nTeselas   = nTeselas;
        this.nMov       = nMov;
        this.height     = (short) imgArray.get(0).getHeight();
        this.width      = (short) imgArray.get(0).getWidth();
        this.encVid     = new EncodedVideo(gop);
        for(Image img: imgArray) {
            this.encVid.frames.add(img.getImgBufMod());
        }
        this.idn        = 0;
        this.encDone    = false;
        this.decDone    = false;
        this.console    = console;
    }
    
    public Codec(ArrayList<Image> imgArray, javax.swing.JTextArea console) {
        this.imgTh      = 0.0f;
        this.nTeselas   = 0;
        this.nMov       = 0;
        this.height     = (short) imgArray.get(0).getHeight();
        this.width      = (short) imgArray.get(0).getWidth();
        this.encVid     = new EncodedVideo();
        for(Image img: imgArray) {
            this.encVid.frames.add(img.getImgBufMod());
        }
        this.idn        = 0;
        this.encDone    = true;
        this.decDone    = false;
        this.console    = console;
    }
    
    public Boolean getEncodeFinished() {
        return this.encDone;
    }
    
    public Boolean getDecodeFinished() {
        return this.decDone;
    }
    
    public void startEncoding() {
        this.th         = new Thread(this);
        this.th.start();
    }
    
    public Boolean compareSubImg(BufferedImage img1, BufferedImage img2) {
        /*
        Compare to subimages and if the mean value of the differences is less than
        the threashold return true. 
        */
        double meanVal = 0.0d;
        for(int i = 0; i < img1.getWidth(); i++) {
            for(int j = 0; j < img1.getHeight(); j++) {
                Color c1 = new Color(img1.getRGB(i, j));
                Color c2 = new Color(img2.getRGB(i, j));
                
                meanVal += ( ( Math.abs(c1.getRed() - c2.getRed()) + 
                        Math.abs(c1.getGreen() - c2.getGreen()) +
                        Math.abs(c1.getBlue() - c2.getBlue()) ) / 3.0d);
            }
        }
        meanVal = (meanVal / ((double) img1.getWidth()*img1.getHeight()));
        if (meanVal < this.imgTh)
            return true;
        return (meanVal < this.imgTh);
    }
    
    public void compareImage(int idnB, int idnR) {
        /*
        Compare two images. All the tesseras of imgRef present in imgB are added 
        to the tessera list of encVid. These tesseras are eliminated from imgB. 
        */
        int count = 0;
        short iWidth   = (short) Math.floor(this.width / this.nTeselas);
        short iHeight  = (short) Math.floor(this.height / this.nTeselas);
        
        BufferedImage imgB = this.encVid.frames.get(idnB);
        BufferedImage imgR = this.encVid.frames.get(idnR);
        
        Boolean foundSub = false;
        ArrayList<Short []> tesPos = new ArrayList<>();
        ArrayList<Tesela> tesArr = new ArrayList<>();

        short imgWidth, imgHeight, wOffset, hOffset;
        for(short i = 0; i < this.nTeselas; i++) {
            for(short j = 0; j < this.nTeselas; j++) {
                //Search if there is a correspondence
                wOffset = (short) (i*iWidth);
                hOffset = (short) (j*iHeight);
                if(i == (this.nTeselas - 1))
                    imgWidth = (short) (this.width - wOffset);
                else
                    imgWidth = iWidth;
                if(j == (this.nTeselas - 1))
                    imgHeight = (short) (this.height - hOffset);
                else
                    imgHeight = iHeight;
                BufferedImage subImgRef = imgR.getSubimage(wOffset, hOffset, 
                        imgWidth, imgHeight);

                short startx = (short) ((i* iWidth) - this.nMov);
                if (startx < 0)
                    startx = 0;
                short starty = (short) ((j* iHeight) - this.nMov);
                if (starty < 0)
                    starty = 0;
                short endx = (short) (startx + this.nMov);
                if (endx > this.width - imgWidth)
                    endx = (short) (this.width - imgWidth);
                short endy = (short) (starty + this.nMov);
                if (endy > this.height - imgHeight)
                    endy = (short) (this.height - imgHeight);
                for(short k = startx; k < endx; k++) {
                    for(short l = starty; l < endy; l++) {
                        BufferedImage subImgB = imgB.getSubimage(k, l, 
                                imgWidth, imgHeight);
                        foundSub = compareSubImg(subImgB, subImgRef);
                        //If yes, put to zero and save info
                        if (foundSub) {
                            //System.out.println("(x,y):" + k +"," + l +
                            //        "\tW, H:" + imgWidth + "," + imgHeight);
                            /*int[] a1 = imgB.getRGB(k, l, imgWidth, imgHeight, 
                                    new int[imgWidth*imgHeight], 0, imgWidth);
                             System.out.println(Arrays.toString(a1));*/
                           // imgB.setRGB(k, l, imgWidth, imgHeight, 
                            //        new int[imgWidth*imgHeight], 0, imgWidth);
                            tesPos.add(new Short[]{k, l});
                            l = (short) (l + imgHeight);
                            //Com evitar de forma eficient la zona de la tesela ja trobada?
                            //k = 
                            break;
                        }
                        if (foundSub)
                            break;
                    }
                }
                //If the tesPos has some info, save it
                if (!tesPos.isEmpty()) {
                    Tesela tes = new Tesela(imgWidth, imgHeight, wOffset, hOffset, 
                            tesPos.size(), (byte) idnB, (byte) idnR);
                    for(Short[] pos: tesPos) {
                        //tes.addPosition((short[]) JSType.toPrimitive(pos));
                        tes.addPosition(new short[]{pos[0], pos[1]});
                        count++;
                    }
                    this.encVid.teselas.add(tes);
                    tesArr.add(tes);
                }
                tesPos.clear();
            }
        }
        //Substitute the removed teselas with its mean value
        for(Tesela tes: tesArr) {
            BufferedImage tesImg = imgR.getSubimage(tes.getWOffset(), tes.getHOffset(), 
                        tes.getWidth(), tes.getHeight());
            int[] mValArray = new int[tes.getWidth()*tes.getHeight()];
            Arrays.fill(mValArray, imgBufMean(tesImg));
            for(short[] pos: tes.getPositions()) {
                imgB.setRGB(pos[0], pos[1], tes.getWidth(), tes.getHeight(), 
                        mValArray, 0, tes.getWidth() );
            }
        }
        promediodeVecinos(imgB);
        //System.out.println("Teselas found:" + count);
        this.console.append(count + ",");
    }
    
    public void encode() {
        /*
        Encode video. refID for reference frame is the position.
        */
        //refID = idn
        int gop = this.encVid.getGOP();
        int nFrames = this.encVid.frames.size();
        int tenPer = (int) (nFrames * 0.1);
        long startTime =  System.currentTimeMillis();
        //System.out.print("Encoding video...\n");
        this.console.append("Encoding video...\n");
        for (int i=0; i <= nFrames; i += gop ) {
            if ( (i % tenPer ) == 0)
                //System.out.print("\n" + i + "%\t");
                this.console.append("\n" + i + "%\t");
            for (int j=1; j < gop; j++) {
                if (i+j >= nFrames)
                    break;
                compareImage(i+j, i);
            }
        } 
        String msg = "\nFinished video encoding. Total time spend: " 
                + ((System.currentTimeMillis()-startTime)/1000) + " sec.\n";
        //System.out.print(msg);
        this.console.append(msg);
    }
    
    public int imgBufMean(BufferedImage img) {
        int mValR = 0;
        int mValG = 0;
        int mValB = 0;
        int mValA = 0;
        
        for(int i = 0; i < img.getWidth(); i++) {
            for(int j = 0; j < img.getHeight(); j++) {
                Color c1 = new Color(img.getRGB(i, j));
                mValR += c1.getRed();
                mValG += c1.getGreen();
                mValB += c1.getBlue();
                mValA += c1.getAlpha();
                //meanVal += ( c1.getRed() + c1.getGreen() + c1.getBlue() ) / 3.0d;
            }
        }
        int size = img.getWidth()*img.getHeight();
        mValR /= size;
        mValG /= size;
        mValB /= size;
        mValA /= size;
        Color col = new Color(mValR, mValG, mValB, mValA);
        return col.getRGB();
    }
    
    public void promediodeVecinos(BufferedImage img) {
        int sumaR, sumaG, sumaB, rgb;
        int sumaA, PromA;
        int PromR, PromG, PromB;
        //solo es hacer la media de las tonalidades
        //filtrogris();
        //pasado a gris   
        for (int i = 1; i < img.getWidth() - 1; i++) {  //bucles para correr todos los pixels
            for (int j = 1; j < img.getHeight() - 1; j++) {
                //suma de la matriz de vecinos
                sumaR = 0;
                sumaG = 0;
                sumaB = 0;
                sumaA = 0;
                for (int f = i - 1; f <= i + 1; f++) {
                    for (int g = j - 1; g <= j + 1; g++) {
                        Color colores = new Color(img.getRGB(f, g));
                        sumaR = colores.getRed() + sumaR;
                        sumaG = colores.getGreen()+ sumaG;
                        sumaB = colores.getBlue()+ sumaB;
                        sumaA = colores.getAlpha() + sumaA;
                    }
                }
                PromR = sumaR / 9;
                PromG = sumaG / 9;
                PromB = sumaB / 9;
                PromA = sumaA / 9;
                rgb = new Color(PromR, PromG, PromB, PromA).getRGB();
                img.setRGB(i, j, rgb);
            }
        }
    }
    
    public void decode() {
        long startTime =  System.currentTimeMillis();
        //int nTes = this.encVid.teselas.size();
        this.console.append("Decoding video...\n");
        for (Tesela tes: this.encVid.teselas) {
            addImage(tes);
        } 
        String msg = "Finished video encoding. Total time spend: " 
                + ((System.currentTimeMillis()-startTime)/1000) + " sec.\n";
        this.console.append(msg);
    }
    
    public void addImage(Tesela tes) {
        BufferedImage img = this.encVid.frames.get(tes.getFrameID());
        BufferedImage imgI = this.encVid.frames.get(tes.getInterID());
        
        int[] pixVec = new int[tes.getHeight()*tes.getWidth()];
        img.getRGB(tes.getWOffset(), tes.getHOffset(), 
                tes.getWidth(), tes.getHeight(), pixVec, 0, tes.getWidth());
        for(short[] pos: tes.getPositions()) {
            imgI.setRGB(pos[0], pos[1], tes.getWidth(), tes.getHeight(), pixVec,
                    0, tes.getWidth() );
        }
    }
    
    public void saveTeselas(File out) {
        try {
            FileOutputStream fos = new FileOutputStream(out);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            oos.writeObject(this.encVid.teselas);
            oos.flush();
            oos.close();
            fos.close();
            this.console.append("Tesela info saved to " + out.getCanonicalPath() + "\n");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Codec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Codec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveCustTes(File out) {
        try {
            FileOutputStream fos = new FileOutputStream(out);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            for(Tesela tes: this.encVid.teselas) {
                gz.write(tes.toByteArray());
                gz.write((byte) 0xFF);
            }
            //ObjectOutputStream oos = new ObjectOutputStream(gz);
            //oos.writeObject(this.encVid.teselas);
            //oos.flush();
            //oos.close();
            gz.flush();
            gz.close();
            fos.flush();
            fos.close();
            //this.console.append("Tesela info saved to " + out.getCanonicalPath() + "\n");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Codec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Codec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readTeselas(File in) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(in);
            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            ArrayList<Tesela> tesArr = new ArrayList((ArrayList<Tesela>) ois.readObject());
            this.encVid.teselas = tesArr;
            ois.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Codec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Codec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Codec.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
 
    
    @Override
    public void run() {
        setChanged();
        if (!this.encDone) {
            encode();
            this.encDone = true;
            notifyObservers(false);
        } else if (!this.decDone) {
            decode();
            this.decDone = true;
            notifyObservers(true);
        }

    }
    
}

