package com.vista;

/**
 * Clase Main
 */
public class Appmain {
    /**
     * Punto de partida de la aplicación, crea un objeto del mismo y llama a run().
     * @param args : parámetros de la consola
     */
    public static void main(String[] args) {
        Appmain main = new Appmain();
        main.run();
    }

    /**
     * Instancia a la frame principal de la aplicación.
     */
    public void run(){
        new Main_Frame();
    }
}
