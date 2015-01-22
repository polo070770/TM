package com.controlador;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ControllerPlayer {

    private final VideoPlayer video_player;
    private Thread thread_video;

    private int fps;
    private static final int DEFAULT_FPS = 20;

    private boolean playing;
    public static boolean forward_direction;

    /**
     * Constructor, inicializa los atributos
     *
     * @param ctrl, controlador delegante
     */
    public ControllerPlayer(Controller ctrl) {
        video_player = new VideoPlayer(ctrl);
        thread_video = null;

        fps = DEFAULT_FPS;
        playing = false;
        forward_direction = true;
    }

    /**
     * Lanza el thread de reproducción.
     */
    public void play() {
        waitForThread();
        if (!playing) {
            video_player.init(fps);
            thread_video = new Thread(video_player);
            thread_video.start();
            playing = true;
        }
    }

    /**
     * Pausar la reproducción.
     */
    public void pause() {
        video_player.pause_Thread();
        playing = false;
    }

    /**
     * Termina la reproducción y resetear algunos valores.
     */
    public void stop() {
        video_player.terminate_Thread();
        playing = false;
        fps = DEFAULT_FPS;
        forward_direction = true;
    }

    /**
     * Si se está reproduciendo adelante, accelera la reproducción incrementando
     * el fps Si se está reproduciendo atrás, cambia el sentido de la
     * reproducción y resetear fps Si la reproducción está parada, avanza una
     * imagen
     *
     * @param offset, el valor de avance
     */
    public void goForward(int offset) {
        if (playing) {
            if (forward_direction) {
                fps += offset;
            } else {
                forward_direction = true;
                fps = DEFAULT_FPS;
            }
            pause();
            play();
        } else {
            video_player.nextImg();
        }
    }

    /**
     * Si se está reproduciendo atrás, accelera la reproducción incrementando el
     * fps Si se está reproduciendo adelante, cambia el sentido de la
     * reproducción y resetear fps Si la reproducción está parada, retrocede una
     * imagen
     *
     * @param offset, el valor de avance
     */
    public void goBack(int offset) {
        if (playing) {
            if (!forward_direction) {
                fps += offset;
            } else {
                forward_direction = false;
                fps = DEFAULT_FPS;
            }
            pause();
            play();
        } else {
            video_player.previusImg();
        }
    }

    /**
     * Indica si está en reproducción
     *
     * @return boleano "si está reproduciendo"
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Indica si el sentido de reproducción es adelante
     *
     * @return boleano "es adelante"
     */
    public boolean isForward() {
        return forward_direction;
    }

    /**
     * Se llama cuando se cierra el frame principal.
     */
    public void onCloseWindow() {
        pause();
        waitForThread();
    }

    /**
     * Hacer que el thread de reproducción sea null
     */
    public void threadVideoEnded() {
        thread_video = null;
    }

    /**
     * Espera a que el thread de reproducción se una.
     */
    private void waitForThread() {
        if (thread_video != null) {
            try {
                thread_video.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControllerPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int getFPS() {
        return this.fps;
    }
}
