package com.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class Encoder implements Runnable {

    private int ntes;
    private int nmov;
    private int gop;
    private int qth;
    //private ArrayList<int[]> list_relations = new ArrayList<>();
    private ArrayList<Tesela> teselas;

    private ArrayList<BufferedImage> imgs;
    private HashMap<Integer, ArrayList<BufferedImage>> frame_teselas = new HashMap<>();

    private String path;

    private final int width_tesela;
    private final int height_tesela;

    public Encoder(int ntes, int nmov, int gop, int qth, ArrayList<BufferedImage> images, String path) {

        this.imgs = (ArrayList<BufferedImage>) images.clone();

        this.ntes = ntes;
        this.nmov = nmov;
        this.gop = gop;
        this.qth = qth;
        this.path = path;

        this.width_tesela = imgs.get(1).getWidth() / ntes;
        this.height_tesela = imgs.get(1).getHeight() / ntes;

        for (int i = 0; i < this.imgs.size(); i++) {
            frame_teselas.put(i, buildTeselas(this.imgs.get(i)));
        }

        teselas = new ArrayList<>();
    }

    private ArrayList<BufferedImage> buildTeselas(BufferedImage image) {
        ArrayList<BufferedImage> list_teseles = new ArrayList<>();
        for (int j = 0; j < image.getHeight(); j += height_tesela) {
            for (int i = 0; i < image.getWidth(); i += width_tesela) {

                BufferedImage tesImage = image.getSubimage(i, j, width_tesela, height_tesela);
                list_teseles.add(tesImage);
            }
        }
        return list_teseles;
    }

    private double getCorrelation(BufferedImage tesela, BufferedImage subimage) {
        double sdf_correlation = 0.;
        for (int j = 0; j < height_tesela; j++) {
            for (int i = 0; i < width_tesela; i++) {

                Color pixel1 = new Color(tesela.getRGB(i, j));
                Color pixel2 = new Color(subimage.getRGB(i, j));
                sdf_correlation += ((Math.abs(pixel1.getRed() - pixel2.getRed())
                        + Math.abs(pixel1.getGreen() - pixel2.getGreen())
                        + Math.abs(pixel1.getBlue() - pixel2.getBlue())) / 3.0);
            }
        }
        sdf_correlation = sdf_correlation / (width_tesela * height_tesela);
        return sdf_correlation;
    }

    private int getTeselaMean(BufferedImage img) {
        int mean_R = 0;
        int mean_G = 0;
        int mean_B = 0;
        int mean_A = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color c = new Color(img.getRGB(i, j));
                mean_R += c.getRed();
                mean_G += c.getGreen();
                mean_B += c.getBlue();
                mean_A += c.getAlpha();
            }
        }

        int size = img.getWidth() * img.getHeight();
        mean_R /= size;
        mean_G /= size;
        mean_B /= size;
        mean_A /= size;

        return new Color(mean_R, mean_G, mean_B, mean_A).getRGB();
    }

    @Override
    public void run() {

        for (int i = 0; i < imgs.size() - 1; i++) {
            ArrayList<BufferedImage> teselas_imgB = frame_teselas.get(i);

            int j = i + 1;
            int end = i + gop;

            if (end >= imgs.size()) {
                end = imgs.size() - 1;
            }

            for (int k = 0; k < teselas_imgB.size(); k++) {

                BufferedImage tesela_imgB = teselas_imgB.get(k);

                boolean correlationed = false;

                while (j <= end && !correlationed) {
                    ArrayList<BufferedImage> teselas_imgR = frame_teselas.get(j);

                    for (int l = k; l < k + nmov; l++) {
                        if (l >= teselas_imgR.size()) {
                            break;
                        }

                        double corr = getCorrelation(tesela_imgB, teselas_imgR.get(l));
                        if (corr < qth) {
                            int rOffset = k / ntes;
                            int cOffset = k % ntes;
                            Tesela tesela = new Tesela(i, j, k, l, height_tesela, width_tesela, rOffset, cOffset);
                            teselas.add(tesela);

                            int[] mean_tesela = new int[width_tesela * height_tesela];
                            Arrays.fill(mean_tesela, getTeselaMean(tesela_imgB));

                            imgs.get(i).setRGB(cOffset * width_tesela, rOffset * height_tesela, tesela_imgB.getWidth(),
                                    tesela_imgB.getHeight(), mean_tesela, 0, tesela_imgB.getWidth());

                            correlationed = true;
                            break;
                        }
                    }
                    j++;
                }
            }

            float[] filter = new float[]{
                1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
                1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
                1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f

            };

            //imgs.set(i, Image_Modifiers.filter(imgs.get(i), filter));

        }

        Zip_Writer zw = new Zip_Writer(path, "jpeg");
        zw.setModified_images(imgs);
        Thread th = new Thread(zw);
        th.start();

        File out = new File(path + File.separator + "tesela.gzip");

        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        try {
            ObjectOutputStream os = new ObjectOutputStream(bs);
            for (Tesela tes : this.teselas) {
                os.writeObject(tes);

            }
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(Encoder.class.getName()).log(Level.SEVERE, null, ex);
        }

        byte[] bytes = bs.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(out);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            gz.write(bytes);
            gz.flush();
            gz.close();
            fos.flush();
            fos.close();

        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }
}
