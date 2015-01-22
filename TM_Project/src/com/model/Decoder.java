package com.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class Decoder implements Runnable {

    private final Zip_Reader zip_reader;
    private final File file;
    private final File file_tesela;
    private final ArrayList<BufferedImage> decoded_imgs;
    private final ArrayList<Tesela> teselas;

    public Decoder(File file, File file_tesela) {
        zip_reader = new Zip_Reader();
        this.file = file;
        this.file_tesela = file_tesela;

        teselas = new ArrayList<Tesela>();

        zip_reader.readZip(file);

        read_teselas();

        decoded_imgs = zip_reader.getImages();
    }

    @Override
    public void run() {
        for (int i = teselas.size() - 1; i >= 0; i--) {
            Tesela tesela = teselas.get(i);
            int fb = tesela.getFrameBase();
            int fr = tesela.getFrameReference();

            /*int tb_index = tesela.getTbIndex();
             int tr_index = tesela.getTrIndex();*/
            int height = tesela.getHeight();
            int width = tesela.getWidth();
            int rOffset = tesela.getrOffset();
            int cOffset = tesela.getcOffset();

            BufferedImage img_b = decoded_imgs.get(fb);
            BufferedImage img_r = decoded_imgs.get(fr);

            int[] recover = new int[width * height];

            img_r.getRGB(cOffset * width, rOffset * height, width, height, recover, 0, width);
            img_b.setRGB(cOffset * width, rOffset * height, width, height, recover, 0, width);

        }

        String path = this.file.getParent();

        Zip_Writer zw = new Zip_Writer(path, "decoded");
        zw.setModified_images(decoded_imgs);

        Thread th = new Thread(zw);
        th.start();

    }

    private void read_teselas() {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file_tesela);
            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            Tesela tesela = (Tesela) ois.readObject();
            while (true) {
                teselas.add(tesela);
                try {
                    tesela = (Tesela) ois.readObject();
                } catch (Exception ex) {
                    break;
                }

            }
            ois.close();
            fis.close();
        } catch (Exception ex) {
            Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
