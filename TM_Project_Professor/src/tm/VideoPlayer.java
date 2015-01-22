/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author droma
 */
public class VideoPlayer {
    
    private State state;
    private Boolean forward = true;
    public ImageFrame imgFrameOri;
    public ImageFrame imgFrameMod;
    public ArrayList<Image> imgArray;
    private int idn;
    private Boolean HSBfilter = false;
    private Image.Filter filterStatus;
    float hue, brightness, saturation;
    private int binaryTh =  64;
    private Boolean die = false;
    private final javax.swing.JTextArea console;
//    private Boolean switchToEncVideo;
//    private EncodedVideo encVid;
    
    private long frameRate;
    public static final long INIT_FRAME_RATE = 10;
    private Thread th;
    private Timer frTm;
    
    public enum State { 
        RUN, STOP, PAUSE
    };
    
    public VideoPlayer(ArrayList<Image> imgArray, 
            javax.swing.JTextArea consoleOutput) {
        init();
        this.console = consoleOutput;
        this.imgArray = imgArray;
        int imgHeight = this.imgArray.get(0).getHeight();
        int imgWidth = this.imgArray.get(0).getWidth();
        Rectangle size = gui_frame.getFrames()[0].getBounds();
        this.imgFrameOri = new ImageFrame("Original Frame");
        this.imgFrameOri.setSize(imgWidth, imgHeight);
        this.imgFrameOri.setMinimumSize(new Dimension(imgWidth, imgHeight));
        this.imgFrameOri.setLocation(size.width + 5, 5);
        this.imgFrameOri.setVisible(true);
        this.imgFrameMod = new ImageFrame("Modified Frame");
        this.imgFrameMod.setSize(imgWidth, imgHeight);
        this.imgFrameMod.setMinimumSize(new Dimension(imgWidth, imgHeight));
        this.imgFrameMod.setLocation(size.width + imgWidth + 10, 5);
        this.imgFrameMod.setVisible(true);
        reloadImg();
    }
    
    public void clear() {
        stop();
        this.imgArray.clear();
        this.imgFrameMod.dispose();
        this.imgFrameOri.dispose();
    }
    
    private void init() {
        this.filterStatus = Image.Filter.ORIGINAL;
        this.saturation = 0.0f;
        this.state = State.STOP;
        this.forward = true;
        this.idn = 0;
        this.frameRate = 10;
        this.frTm = new Timer();
        //this.frT = new FrameRateTimer();
//        this.switchToEncVideo = false;
    }
    
    public ArrayList<Image> getImgArray() {
        return this.imgArray;
    }
      
    public ArrayList<BufferedImage> getBuffImgModArray() {
        ArrayList<BufferedImage> bufImgArr = new ArrayList();
        for(Image img: this.imgArray)
            bufImgArr.add(img.getImgBufMod());
        return bufImgArr;
    }
    
    public Boolean getBackward() {
        return !this.forward;
    }
    
    public float getBrightness() {
        return this.brightness;
    }
    
    public Boolean getForward() {
        return this.forward;
    }

    public float getHue() {
        return this.hue;
    }
    
    public float getSaturation() {
        return this.saturation;
    }
    
    public State getState() {
        return this.state;
    }
    
    public void setBackward() {
        if (this.forward && this.state == State.RUN)
            setFramerate(VideoPlayer.INIT_FRAME_RATE);
        this.forward = false;
    }
    
    public void setBinaryTh(int th) {
        this.binaryTh = th;
    }
    
    public void setBrightness(float br) {
        checkHSB(this.hue, this.saturation, br);
        this.brightness = br;
        changeFilter(this.filterStatus, true);
    }
    
    public void setFramerate(long fr) {
        long waitTime = (long) (1000.0d/fr);
        if (waitTime < 1) {
            this.console.append("Frame rate already set to maximum value of "
                    + Long.toString(this.frameRate) + "\n");
        } else {
            this.frTm.cancel();
            this.frameRate  = fr;
            this.frTm       = new Timer();
            this.frTm.scheduleAtFixedRate(new FrameRateTask(), (long) 0, waitTime);
            this.console.append("Frame rate set to "+ Long.toString(this.frameRate) + "\n");
        }
    }
    
    public void setForward() {
        if (!this.forward && this.state == State.RUN)
            setFramerate(VideoPlayer.INIT_FRAME_RATE);
        this.forward = true;
        
    }
    
    public void setHue(float hue) {
        checkHSB(hue, this.saturation, this.brightness);
        this.hue = hue;
        changeFilter(this.filterStatus, true);
    }
    
    public void setHSB(float hue, float sat, float bri) {
        checkHSB(hue, sat, bri);
        if (this.HSBfilter) {
            this.hue        = hue;
            this.saturation = sat;
            this.brightness = bri;
            changeFilter(this.filterStatus, true);
        }
    }
    
    public void setSaturation(float sat) {
        checkHSB(this.hue, sat, this.brightness);
        this.saturation = sat;
        changeFilter(this.filterStatus, true);
    }
    
    public void changeState(State st) {
        switch (st) {
            case RUN:
                if (this.state == State.STOP) {
                    filterAll();
                    setFramerate(this.frameRate);
                }
                this.state = st;
                break;
            case STOP:
                if (this.state == State.RUN) {
                    this.frTm.cancel();
                }
                this.frameRate = VideoPlayer.INIT_FRAME_RATE;
                this.idn = 0;
                this.state = st;
                break;
        }       
    }
    
    public void checkHSB(float hue, float sat, float bri) {
        this.HSBfilter = (this.brightness == bri || this.hue == hue || this.saturation == sat);
    }
    
    private void reloadImg() {
        this.imgFrameOri.setVisible(true);
        this.imgFrameOri.getImgArea().setIcon(
                new ImageIcon(this.imgArray.get(this.idn).getImgBuf()));
        reloadModImg();
    }
    
    public void reloadModImg() {
        this.imgFrameMod.setVisible(true);
/*        if (this.switchToEncVideo) {
            this.imgFrameMod.getImgArea().setIcon(
                    new ImageIcon(this.encVid.frames.get(idn)));
        } else {*/
        this.imgFrameMod.getImgArea().setIcon(
            new ImageIcon(this.imgArray.get(this.idn).getImgBufMod()));
        //}
    }
    
    public void changeFilter(Image.Filter newFl, Boolean reloadModImg) {
        //if (this.filterStatus != newFl || this.HSBfilter) {
        this.filterStatus = newFl;
        if (this.state == State.RUN)
            filterAll();
        else {
            if (this.filterStatus == Image.Filter.BINARY)
                imgArray.get(idn).filter(filterStatus, this.binaryTh);      
            else
                imgArray.get(idn).filter(filterStatus); 
            if (this.HSBfilter)
                imgArray.get(idn).setHSB(this.hue, this.saturation, this.brightness);
            if (reloadModImg)
                reloadModImg();
        }
    }
    
    public void filterAll() {
        this.console.append("Filtering all images...");
        for(Image img: this.imgArray) {
            if (this.filterStatus == Image.Filter.BINARY)
                img.filter(this.filterStatus, this.binaryTh);      
            else
                img.filter(this.filterStatus); 
            if (this.HSBfilter)
                img.setHSB(this.hue, this.saturation, this.brightness);
        }
        this.console.append("Done\n");
    }
    
    public void decIdn() {
        if (this.idn == 0) 
            this.idn = this.imgArray.size() - 1;
        else
            this.idn--;
    }
    
    public void incIdn() {
        if (this.idn == this.imgArray.size() - 1) 
            this.idn =  0;
        else
            this.idn++;
    }
    
    public void writeImages(String format, File outDir) {
        this.imgArray.stream().forEach((img) -> img.writeImages(format, outDir));
    }
    
/*    public void loadEncVideo(EncodedVideo vid) {
        this.encVid = vid;
        this.switchToEncVideo = true;
    }*/
           
    public void fastForward() {
        if (this.state == State.STOP || this.state == State.PAUSE) {
            if (this.forward) {
                incIdn();
            } else {
                decIdn();
            }
            this.changeFilter(this.filterStatus, true);
            reloadImg();
        }
        else {
            setFramerate( (long) ( ((float) this.frameRate) * 1.5) );
        }
    }
   
    public void stop() {
        this.frTm.cancel();
    }
    
    class FrameRateTask extends TimerTask {
    
        @Override
        public void run() {
            if (forward) {
                incIdn();
            } else {
                decIdn();
            }
            reloadImg();
        } 
    }
}


