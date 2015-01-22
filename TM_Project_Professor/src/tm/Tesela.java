/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import java.io.Serializable;

/**
 *
 * @author droma
 */
public class Tesela implements Serializable{
    private final byte frameID;
    private final byte interID;
    private short[][] positions;
    //teselas can have different sizes
    private final short width;
    private final short height;
    //offset inside original image
    private final short wOffset;
    private final short hOffset;
    private byte idn;
       
    public Tesela(short width, short height, short wOffset, short hOffset, 
            int nPos, byte interID, byte frameID) {
        this.interID    = interID;
        this.frameID    = frameID;
        this.width      = width;
        this.height     = height;
        this.wOffset    = wOffset;
        this.hOffset    = hOffset;
        this.idn        = 0;
        this.positions  = new short[nPos][2];
    }
    
    public void addPosition(short [] position) {
        this.positions[this.idn++] = position;
    }
    
    public short[] getPosition() {
        if (hasElements())
            return this.positions[this.idn--];
        else 
            return null;      
    }
    
    public short getWidth() {
        return this.width;
    }
    
    public short getHeight() {
        return this.height;
    }
    
    public short getHOffset() {
        return this.hOffset;
    }
    
    public short getWOffset() {
        return this.wOffset;
    }
    
    public Boolean hasElements() {
        return (this.idn != 0);
    }
         
    public byte getInterID() {
        return this.interID;
    }
    
    public byte getFrameID() {
        return this.frameID;
    }
    
    public short size() {
        return (short) this.positions.length;
    }
    
    public short[][] getPositions() {
        return this.positions;
    }
    
    @Override
    public String toString() {
        String string = Byte.toString(this.frameID) + Byte.toString(this.interID)
                + Short.toString(this.width) + Short.toString(this.height) 
                + Short.toString(this.wOffset) + Short.toString(this.hOffset)
                + Short.toString((short) this.positions.length);
        for(short[] pos: this.positions) {
            string += Short.toString(pos[0]) + Short.toString(pos[1]);
        }
        return string;
    }
    
    public byte[] toByteArray() {
        int lidn = 0;
        byte[] bArr = new byte[2+5*2+this.positions.length*2*2];
        bArr[0]     = this.frameID;
        bArr[1]     = this.interID;
        bArr[2]     = (byte) ((this.width >> 8) & 0xFF);
        bArr[3]     = (byte) (this.width & 0xFF);
        bArr[4]     = (byte) ((this.height >> 8) & 0xFF);
        bArr[5]     = (byte) (this.height & 0xFF);
        bArr[6]     = (byte) ((this.wOffset >> 8) & 0xFF);
        bArr[7]     = (byte) (this.wOffset & 0xFF);
        bArr[8]     = (byte) ((this.hOffset >> 8) & 0xFF);
        bArr[9]     = (byte) (this.hOffset & 0xFF);
        bArr[10]    = (byte) this.positions.length;
        lidn         = 11;
        for(short[] pos: this.positions) {
            bArr[lidn++]     = (byte) ((pos[0] >> 8) & 0xFF);
            bArr[lidn++]     = (byte) (pos[0] & 0xFF);
            bArr[lidn++]     = (byte) ((pos[1] >> 8) & 0xFF);
            bArr[lidn++]     = (byte) (pos[1] & 0xFF);
        }
        return bArr;
    }
}
