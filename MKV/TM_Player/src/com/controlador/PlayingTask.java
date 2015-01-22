package com.controlador;

import java.util.TimerTask;

/**
 * Clase que se encarga de pasar las imágenes de reproducción bajo el
 * fps establecido.
 */
public class PlayingTask extends TimerTask {
    private final Controller ctrl;
    public static int index_image;
    private volatile boolean executing;
    private boolean loop_video;
    
    /**
     * Constructor, inicializa los atributos
     * @param controller, controlador delegante
     * @param img_idx, índice de la imagen con el cuál empieza la reproducción
     */
    public PlayingTask(Controller controller, int img_idx) {
        ctrl = controller;
        index_image = img_idx;
        executing = true;
        loop_video = true; //Reproducir otra vez cuando se termine.
    }
    
    @Override
    /**
     * pasar las imágenes de reproducción bajo el fps establecido.
     */
    public void run() {
        if (ControllerPlayer.forward_direction){ //Sentido adelante
            if (index_image < ctrl.getLengthZip()) {
                ctrl.setImage(index_image);
                index_image++;
            } else {
                if (loop_video) { // Vuelve a comenzar desde el inicio la reproduccion
                    index_image = 0;
                } else {  // Codigo para que solo reproduzca una sola vez
                    executing = false;
                    cancel();
                }
            }
        }else{
            if (index_image >= 0) { //Sentido atrás
                ctrl.setImage(index_image);
                index_image--;
            } else {
                if (loop_video) { // Vuelve a comenzar desde el inicio la reproduccion
                    index_image = ctrl.getLengthZip()-1;
                } else { // Codigo para que solo reproduzca una sola vez
                    executing = false;
                    cancel();
                }
            }
        }
    }
    
    /**
     * Devuelve el boleano que indica si está en reproducción
     * @return boleano que indica si está en reproducción
     */
    public boolean isExecuting() {
        return executing;
    }
    
    /**
     * Cancelar la tarea de la reproducción
     * @return el índice en el momento de cancelar la tarea de reproducción.
     */
    public int stopTimerTask() {
        cancel();
        return index_image;
    }
    
    /**
     * Establecer el valor loop.
     * @param loop_video, indica si vuelve a reproducir cuando se termine
     */
    public void setLoopVideo(boolean loop_video) {
        this.loop_video = loop_video;
    }
}
