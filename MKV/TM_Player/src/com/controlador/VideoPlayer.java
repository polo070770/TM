package com.controlador;

import java.util.Timer;

public class VideoPlayer implements Runnable {

    private final Controller ctrl;

    private long fps; // Frames per second
    private volatile boolean running; // Boolean para controlar la ejecución
    private int img_index; // Indice en el cual controlamos en la imagen en la que estamos

    /**
     * Constructor
     *
     * @param controller, controlador delegante
     */
    public VideoPlayer(Controller controller) {
        ctrl = controller;
        img_index = 0;
    }

    /**
     * Preparamos el thread para su ejecución
     *
     * @param frame_per_second, tasa de paso de imágenes por segundo
     */
    public void init(int frame_per_second) {
        fps = frame_per_second;
        running = true;
    }

    @Override
    /**
     * Crear un objeto de tarea (reproducción) y asociarle un temporizador.
     */
    public void run() {
        PlayingTask ptask = new PlayingTask(ctrl, img_index); //Instanciar un objeto de tarea
        Timer timer_task = new Timer(); //Instanciar al temporizador
        timer_task.scheduleAtFixedRate(ptask, 0, 1000 / fps); //Asociarle al objeto de tarea el temporizador

        while (ptask.isExecuting() && running) {
        }

        timer_task.cancel();
        img_index = ptask.stopTimerTask();
        ctrl.threadVideoEnded();
    }

    /**
     * Metodo que avanza una sola imagen
     */
    public void nextImg() {
        img_index++;
        if (img_index >= ctrl.getLengthZip()) {
            img_index = ctrl.getLengthZip() - 1;
        }
        ctrl.setImage(img_index);
    }

    /**
     * Metodo que retrasa una sola imagen
     */
    public void previusImg() {
        img_index--;
        if (img_index < 0) {
            img_index = 0;
        }
        ctrl.setImage(img_index);
    }

    /**
     * Pausar la reproducción
     */
    public void pause_Thread() {
        running = false;
    }

    /**
     * Termina la reproducción y volver al inicio
     */
    public void terminate_Thread() {
        running = false;
        ctrl.setImage(0);
        img_index = 0;
    }
}
