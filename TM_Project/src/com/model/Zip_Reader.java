package com.model;

import com.vista.Appmain;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;

/**
 * Clase que gestiona y mantiene los elementos de un zip, en este caso son
 * imágenes.
 */
public class Zip_Reader {

    private static final ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    private ZipFile zFl;

    /**
     * Constructor vacio
     */
    public Zip_Reader() {
    }

    /**
     * Inicializar el ZipFile y llama a "leer las entradas". Si la lista no está
     * vacía, la vacía.
     *
     * @param file : File, ruta donde se encuentra el fichero zip.
     */
    public void readZip(File file) {
        if (images.size() > 0) {
            images.removeAll(images);
        }

        try {
            zFl = new ZipFile(file);
        } catch (IOException ex) {
            Logger.getLogger(Appmain.class.getName()).log(Level.SEVERE, null, ex);
        }

        readEntries();
    }

    /**
     * Leer y guardar las entradas que hay en un zip.
     */
    public void readEntries() {
        Enumeration<? extends ZipEntry> entries = zFl.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            try {
                images.add(ImageIO.read(zFl.getInputStream(entry)));
            } catch (Exception ex) {
                System.out.println("Exception when reading the image");
            }
        }
    }

    /**
     * Saca la entrada del zip según el índice dado, la convierte en una imagen
     * y la devuelve.
     *
     * @param index : int, index de la entrada deseada
     * @return BufferedImage, una imagen de la entrada del zip.
     */
    public BufferedImage getImage(int index) {
        return images.get(index);
    }

    /**
     * Retorna todo el contenido del fichero zip
     *
     * @return contenido del zip
     */
    public ArrayList<BufferedImage> getImages() {
        return images;
    }

    /**
     * Devuelve el tamaño del zip
     *
     * @return int: tamaño del zip
     */
    public int getLength() {
        return images.size();
    }
}
