package com.controlador;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JLabel;

/**
 * Clase controladora que dispone de objetos de otros controladores para
 * delegarles las responsabilidades.
 */
public class Controller {

    private final ControllerPlayer ctrlPlayer;
    private final ControllerImage ctrlImage;
    private ControllerCodec ctrlCodec;

    /**
     * Inicializa los controladores pasandoles los parámetros que necesiten.
     *
     * @param label_img : JLabel, label que contiene la imagen en reproducción
     */
    public Controller(JLabel label_img) {
        ctrlPlayer = new ControllerPlayer(this);
        ctrlImage = new ControllerImage(label_img);
        ctrlCodec = new ControllerCodec();
    }

    /**
     * *********Delegación de responsabilidades al controlador de
     * imagen.*********
     */
    /**
     * Llama a "leer el zip" del objeto ControllerImage.
     *
     * @param file : File, ruta donde se encuentra el fichero zip.
     */
    public void readZip(File file) {
        ctrlImage.readZip(file);
    }

    /**
     * Llama a "get imagen" del objeto ControllerImage.
     *
     * @param index : int, index de la entrada deseada
     * @return BufferedImage, una imagen de la entrada del zip.
     */
    public BufferedImage getImage(int index) {
        return ctrlImage.getImage(index);
    }

    /**
     * Llama a "get images" del objeto ControllerImage
     *
     * @return
     */
    public ArrayList<BufferedImage> getImages() {
        return ctrlImage.getImages();
    }

    /**
     * Llama a "get tamaño del zip" del objeto ControllerImage.
     *
     * @return int: tamaño de la lista
     */
    public int getLengthZip() {
        return ctrlImage.getLengthZip();
    }

    /**
     * Llama a "set imagen" del objeto ControllerImage.
     *
     * @param index : int, index de la entrada deseada
     */
    public void setImage(int index) {
        ctrlImage.setImage(index);
    }

    /**
     * *********Delegación de responsabilidades al controlador de
     * Player.*********
     */
    /**
     * Llama al "play" del objeto controlador de Player
     */
    public void play() {
        ctrlPlayer.play();
    }

    /**
     * Llama al "pause" del objeto controlador de Player
     */
    public void pause() {
        ctrlPlayer.pause();
    }

    /**
     * Llama al "stop" del objeto controlador de Player
     */
    public void stop() {
        ctrlPlayer.stop();
    }

    /**
     * Llama al "goForward" del objeto controlador de Player
     *
     * @param offset, el valor de avance
     */
    public void goForward(int offset) {
        ctrlPlayer.goForward(offset);
    }

    /**
     * Llama al "goBack" del objeto controlador de Player
     *
     * @param offset, el valor de avance
     */
    public void goBack(int offset) {
        ctrlPlayer.goBack(offset);
    }

    /**
     * Llama al "isPlaying" del objeto controlador de Player
     *
     * @return boleano "si está reproduciendo"
     */
    public boolean isPlaying() {
        return ctrlPlayer.isPlaying();
    }

    /**
     * Llama al "isForward" del objeto controlador de Player
     *
     * @return boleano "es adelante"
     */
    public boolean isForward() {
        return ctrlPlayer.isForward();
    }

    /**
     * Llama al "onCloseWindow" del objeto controlador de Player
     */
    public void onCloseWindow() {
        ctrlPlayer.onCloseWindow();
    }

    /**
     * Llama al "threadVideoEnded" del objeto controlador de Player
     */
    public void threadVideoEnded() {
        ctrlPlayer.threadVideoEnded();
    }

    public void encode(int ntes, int nmov, int gop, int qth, String path) {
        ctrlCodec.encode(ntes, nmov, gop, qth, ctrlImage.getImages(), path);
    }

    public void decode(File file, File file_tesela) {
        ctrlCodec.decode(file, file_tesela);
    }

    public int getFPS() {
        return ctrlPlayer.getFPS();
    }
}
