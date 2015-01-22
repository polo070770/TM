package com.model;

import com.controlador.Controller;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

public class Zip_Writer implements Runnable {

    private ZipOutputStream out = null;
    private ArrayList<BufferedImage> modified_images;
    private Controller ctrl;

    @Override
    public void run() {

        if (this.ctrl != null) {
            this.modified_images = ctrl.getImages();
            applyModifiers();
        }

        int count = 0;
        while (count < modified_images.size()) {
            ZipEntry entry = new ZipEntry("output_" + count + ".jpeg");

            try {
                out.putNextEntry(entry);
                ImageIO.write(modified_images.get(count), "jpeg", out);

            } catch (Exception ex) {
                Logger.getLogger(Zip_Writer.class.getName()).log(Level.SEVERE, null, ex);
            }

            count++;
        }

        closeStream();
    }

    /**
     * Se abre el flujo de datos y se prepara el zip
     *
     * @param ctrl, controlador, necesario para la recogida de imagenes en
     * reproduccion
     * @param path, ruta a guardar del zip
     * @param fileName, nombre a guardar del zip
     */
    public Zip_Writer(Controller ctrl, String path, String fileName) {

        this.ctrl = ctrl;

        if (out == null) {
            try {
                out = new ZipOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(
                                        new File(path + File.separator + fileName + ".zip"))));
            } catch (Exception ex) {
                Logger.getLogger(Zip_Writer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public Zip_Writer(String path, String fileName) {

        this.ctrl = null;

        if (out == null) {
            try {
                out = new ZipOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(
                                        new File(path + File.separator + fileName + ".zip"))));
            } catch (Exception ex) {
                Logger.getLogger(Zip_Writer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    /**
     * Aplicamos las modicaciones a las imagenes para guardarlas
     */
    private void applyModifiers() {

        for (int idx = 0; idx < modified_images.size(); idx++) {

            modified_images.set(idx, Image_Modifiers.applyModifier(modified_images.get(idx)));

        }

    }

    /**
     * Se cierra el flujo de salida para el salvado de datos en zip
     */
    public void closeStream() {
        try {
            out.flush();
            out.close();
        } catch (Exception ex) {
            Logger.getLogger(Zip_Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setModified_images(ArrayList<BufferedImage> modified_images) {
        this.modified_images = modified_images;
    }

}
