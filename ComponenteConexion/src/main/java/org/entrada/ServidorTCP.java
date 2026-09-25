/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.entrada;

import org.comunes.ContextoConexion;
import org.comunes.Observer;
import org.comunes.Subject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author Equipo 1
 */
public class ServidorTCP implements Subject, Runnable {
    private final int puerto;
    private final Queue<ContextoConexion> cola;
    private final List<Observer> observers;
    private ContextoConexion contextoActual;
    private boolean activo;
    private Thread hilo;
    private ServerSocket serverSocket;

    public ServidorTCP(int puerto) {
        this.puerto = puerto;
        this.cola = new LinkedBlockingQueue<>();
        this.observers = new ArrayList<>();
        this.contextoActual = null;
        this.activo = false;
        this.hilo = null;
        this.serverSocket = null;
    }
    
    public void iniciar() {
        if (!activo) {
            activo = true;
            hilo = new Thread(this, "ServidorTCP-Hilo");
            hilo.setDaemon(true);
            hilo.start();
        }
    }

    public void detener() {
        activo = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("[ServidorTCP] Error al cerrar servidor: " + e.getMessage());
            }
        }

        if (hilo != null) {
            hilo.interrupt();
        }
    }

    @Override
    public void run() {
        System.out.println("[ServidorTCP] Hilo iniciado.");

        try (ServerSocket ss = new ServerSocket(puerto)) {
            this.serverSocket = ss;
            System.out.println("[ServidorTCP] Escuchando en puerto " + puerto);

            while (activo) {
                try (Socket socket = ss.accept()) {
                    byte[] bytes = recibirBytes(socket);

                    if (bytes != null && bytes.length > 0) {
                        String hostRemoto = socket.getInetAddress().getHostAddress();
                        int puertoRemoto = socket.getPort();

                        contextoActual = new ContextoConexion(hostRemoto, puertoRemoto, bytes);
                        cola.offer(contextoActual);

                        System.out.println("[ServidorTCP] Contexto agregado a la cola.");
                        notifyObservers();
                    }

                } catch (SocketException e) {
                    if (activo) {
                        System.err.println("[ServidorTCP] Error de socket: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            if (activo) {
                System.err.println("[ServidorTCP] Error en servidor: " + e.getMessage());
            }
        } finally {
            System.out.println("[ServidorTCP] Servidor detenido.");
        }
    }

    private byte[] recibirBytes(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] bloque = new byte[1024];
        int bytesLeidos;

        while ((bytesLeidos = in.read(bloque)) != -1) {
            buffer.write(bloque, 0, bytesLeidos);
        }

        return buffer.toByteArray();
    }

    @Override
    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        if (contextoActual == null) {
            return;
        }

        for (Observer o : observers) {
            o.update(contextoActual);
        }
    }
}