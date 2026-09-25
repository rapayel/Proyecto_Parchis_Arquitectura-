/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.salida;

import org.comunes.ContextoConexion;
import org.comunes.Observer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Observer del flujo de salida.
 *
 * Recibe el contexto notificado por el Dispatcher y transmite sus bytes mediante
 * TCP.
 */
public class ClienteTCP implements Observer {

    /**
     * Reacciona a la notificación del Dispatcher transmitiendo los bytes del
     * contexto recibido.
     *
     * @param contexto contexto de comunicación a transmitir
     */
    @Override
    public void update(ContextoConexion contexto) {
        if (contexto == null) {
            System.out.println("[ClienteTCP] Contexto nulo.");
            return;
        }

        transmitir(contexto);
    }

    /**
     * Transmite los bytes usando conexión TCP intermitente.
     *
     * @param contexto contexto con host, puerto y bytes
     */
    private void transmitir(ContextoConexion contexto) {
        try (Socket socket = new Socket(contexto.getHost(), contexto.getPuerto());
             OutputStream out = socket.getOutputStream()) {

            out.write(contexto.getBytes());
            out.flush();

            System.out.println("[ClienteTCP] Bytes transmitidos correctamente.");

        } catch (IOException e) {
            System.err.println("[ClienteTCP] Error al transmitir bytes: " + e.getMessage());
        }
    }
}