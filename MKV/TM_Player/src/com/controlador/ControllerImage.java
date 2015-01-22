package com.controlador;

import com.model.Image_Modifiers;
import com.model.Zip_Reader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Clase controladora que gestiona las imágenes (get, modificación de los
 * píxeles, actualizar la imagen de la reproducción...).
 */
public class ControllerImage {

    private final JLabel label_img;
    private final Zip_Reader zipReader;

    /**
     * Contructor, inicializa los atributos
     *
     * @param label_img : JLabel, label que contiene la imagen en reproducción
     */
    public ControllerImage(JLabel label_img) {
        this.label_img = label_img;
        this.zipReader = new Zip_Reader();

        Image_Modifiers.setOption(Image_Modifiers.filtering_options.NO_FILTER);

    }

    /**
     * Llama a "leer el zip" del objeto Zip_Reader.
     *
     * @param file : File, ruta donde se encuentra el fichero zip.
     */
    public void readZip(File file) {
        zipReader.readZip(file);
    }

    /**
     * Llama a "get imagen" del objeto Zip_Reader
     *
     * @param index : int, index de la entrada deseada
     * @return BufferedImage, una imagen de la entrada del zip.
     */
    public BufferedImage getImage(int index) {
        return zipReader.getImage(index);
    }

    /**
     * Llama a "get tamaño del zip" del objeto Zip_Reader
     *
     * @return int: tamaño de la lista
     */
    public int getLengthZip() {
        return zipReader.getLength();
    }

    public ArrayList<BufferedImage> getImages() {
        return zipReader.getImages();
    }

    /**
     * Aplica la modificacion a la imagen dada antes de ponerla en reproducción.
     *
     * @param image : BufferedImage, imagen que se quiere poner para la
     * reproducción.
     */
    private void setImageIcon(BufferedImage image) {

        image = Image_Modifiers.applyModifier(image);

        label_img.setIcon(new ImageIcon(image));
    }

    /**
     * Ponemos la imagen que está en la posición que indica el parámetro en la
     * reproducción.
     *
     * @param index : int, index de la entrada deseada
     */
    public void setImage(int index) {
        BufferedImage image = zipReader.getImage(index);
        try {
            setImageIcon(image);
        } catch (Exception e) {
            if (ControllerPlayer.forward_direction) {
                PlayingTask.index_image = 1;
            } else {
                PlayingTask.index_image = zipReader.getLength() - 1;
            }
            image = zipReader.getImage(PlayingTask.index_image);
            setImageIcon(image);
        }
    }

}
