/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author droma
 */
public class EncodedVideo {
    public ArrayList<BufferedImage> frames = new ArrayList<>();
    private final byte gop;
    public ArrayList<Tesela> teselas = new ArrayList<>();
    
    public EncodedVideo(byte gop) {
        this.gop    = gop;
    }
    
    public EncodedVideo() {
        this.gop    = 0;
    }
    
    public int getGOP() {
        return this.gop;
    }
    
}
