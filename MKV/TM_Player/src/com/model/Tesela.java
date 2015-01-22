package com.model;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author maikel
 */
public class Tesela implements Serializable {

    //frame base de donde eliminamos la tesela, frame de referencia la subimagen de la cual es utilizada para recuperar la tesela eliminada.
    private int frame_base;
    private int frame_reference;
    /*private int tb_index;
     private int tr_index;*/

    //Anchura y altura de la tesela
    private int height_tesela;
    private int width_tesela;

    //Desplazamiento de teselas dentro de la imagen original
    private int rOffset;
    private int cOffset;

    public Tesela(int frame_id, int inter_id, int fb_index, int fr_index, int height, int width, int wOffset, int hOffset) {
        frame_base = frame_id;
        frame_reference = inter_id;
        /*this.tb_index = fb_index;
         this.tr_index = fr_index;*/

        this.height_tesela = height;
        this.width_tesela = width;

        this.rOffset = wOffset;
        this.cOffset = hOffset;
    }

    public int getFrameBase() {
        return frame_base;
    }

    public int getFrameReference() {
        return frame_reference;
    }

    /*public int getTbIndex(){
     return tb_index;
     }
    
     public int getTrIndex(){
     return tr_index;
     }*/
    public int getHeight() {
        return height_tesela;
    }

    public int getWidth() {
        return width_tesela;
    }

    public int getrOffset() {
        return rOffset;
    }

    public int getcOffset() {
        return cOffset;
    }
}
