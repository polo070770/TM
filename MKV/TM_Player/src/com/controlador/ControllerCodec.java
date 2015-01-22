package com.controlador;

import com.model.Decoder;
import com.model.Encoder;
import com.vista.Main_Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCodec {

    public ControllerCodec() {
    }

    public void encode(int ntes, int nmov, int gop, int qth, ArrayList<BufferedImage> images, String path) {
        Main_Frame.ta_debug.append("\nEncoding...");
        try {
            Encoder encoder = new Encoder(ntes, nmov, gop, qth, images, path);
            Thread th = new Thread(encoder);
            th.start();
            th.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
        Main_Frame.ta_debug.append("\nEncoding finished!");

    }

    public void decode(File file, File file_tesela) {
        Main_Frame.ta_debug.append("\nDecoding...");
        try {
            Decoder decoder = new Decoder(file, file_tesela);
            Thread th = new Thread(decoder);
            th.start();
            th.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
        Main_Frame.ta_debug.append("\nDecoding finished!");
    }
}
