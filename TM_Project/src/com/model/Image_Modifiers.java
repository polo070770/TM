package com.model;

import com.vista.Filter_changer_Frame;
import com.vista.HSV_changer_Frame;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public final class Image_Modifiers {

    public static enum filtering_options {

        NEGATIVE, BINARIZATION, FILTERING, NO_FILTER

    };

    public static filtering_options opt_filter;

    /**
     *
     * @param image : BufferedImage, imagen en base de la cuál aplicaremos el
     * filtro hsv
     * @return una imagen clonada y filtrada.
     */
    private static BufferedImage rgb2hsb(BufferedImage image) {

        float fh = HSV_changer_Frame.factor_h;
        float fs = HSV_changer_Frame.factor_s;
        float fv = HSV_changer_Frame.factor_v;

        int width, height, red, green, blue, rgb;
        float[] hsbvals;//El vector contiene los valores hsv de un pixel.

        width = image.getWidth();
        height = image.getHeight();

        BufferedImage output = new BufferedImage(width, height, image.getType());

        /* Recorrer todos los pixeles, sacamos el color RGB de ellos, luego los convertimos en el color hsv, y modificamos
         en base de hsv multiplicando los por un factor dado. Finalmente reconvertimos el pixel en rgb y lo asignamos al
         pixel en cuestion.*/
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(image.getRGB(j, i));
                red = c.getRed();
                green = c.getGreen();
                blue = c.getBlue();

                hsbvals = Color.RGBtoHSB(red, green, blue, null); //Conversión RGB->HSV

                if (fh != 1.f) {
                    hsbvals[0] *= fh;
                    if (hsbvals[0] > 1) {
                        hsbvals[0] = 1;
                    }
                }

                if (fs != 1.f) {
                    hsbvals[1] *= fs;
                    if (hsbvals[1] > 1) {
                        hsbvals[1] = 1;
                    }
                }

                if (fv != 1.f) {
                    hsbvals[2] *= fv;
                    if (hsbvals[2] > 1) {
                        hsbvals[2] = 1;
                    }
                }

                rgb = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]); //Conversión HSV->RGB
                output.setRGB(j, i, rgb); //Actualizar el RGB del píxel.
            }
        }
        return output;
    }

    /**
     * Funcion que convoluciona una imagen con un filtro
     *
     * @param image imagen a aplicar el filtro
     * @return imagen filtrada
     */
    private static BufferedImage filter(BufferedImage image) {
        Kernel kernel = new Kernel(Filter_changer_Frame.columns, Filter_changer_Frame.rows, Filter_changer_Frame.filter);
        ConvolveOp op = new ConvolveOp(kernel);

        return op.filter(image, null);
    }

    public static BufferedImage filter(BufferedImage image, float[] filter) {
        Kernel kernel = new Kernel(Filter_changer_Frame.columns, Filter_changer_Frame.rows, filter);
        ConvolveOp op = new ConvolveOp(kernel);

        return op.filter(image, null);
    }

    /**
     * Funcion que realiza el negativo de una imagen.
     *
     * @param image
     * @return imagen en negativo
     */
    private static BufferedImage negative(BufferedImage image) {

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgba = image.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                        255 - col.getGreen(),
                        255 - col.getBlue());
                output.setRGB(x, y, col.getRGB());
            }
        }

        return output;

    }

    /**
     * Funcion que realiza la binarizacion de una imagen
     *
     * @param image
     * @return imagen binarizada
     */
    private static BufferedImage binarization(BufferedImage image) {

        int THRESHOLD = 100;
        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), image.getType());

        Color one = new Color(255, 255, 255);
        Color zero = new Color(0, 0, 0);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                Color c = new Color(image.getRGB(x, y));

                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();

                if (red < THRESHOLD && green < THRESHOLD && blue < THRESHOLD) {
                    output.setRGB(x, y, one.getRGB());
                } else {
                    output.setRGB(x, y, zero.getRGB());
                }
            }
        }

        return output;

    }

    private static boolean isHSV() {

        float fh = HSV_changer_Frame.factor_h;
        float fs = HSV_changer_Frame.factor_s;
        float fv = HSV_changer_Frame.factor_v;

        return fh != 1.f || fs != 1.f || fv != 1.f;

    }

    public static void setOption(filtering_options opt) {
        opt_filter = opt;
    }

    public static BufferedImage applyModifier(BufferedImage image) {
        if (isHSV()) {
            image = rgb2hsb(image);
        }

        switch (opt_filter) {

            case NO_FILTER:
                //DO NOTHING
                break;
            case NEGATIVE:
                image = negative(image);
                break;
            case BINARIZATION:
                image = binarization(image);
                break;
            case FILTERING:
                image = filter(image);
                break;
        }

        return image;
    }

}
