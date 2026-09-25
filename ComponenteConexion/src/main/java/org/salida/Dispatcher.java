/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.salida;

import org.comunes.ContextoConexion;
import org.comunes.Observer;
import org.comunes.Subject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Subject del flujo de salida.
 *
 * Recibe solicitudes de envío, crea el contexto correspondiente, lo almacena en
 * una cola y notifica a sus observadores.
 */
public class Dispatcher implements IDispatcher, Subject {

    /**
     * Cola de contextos pendientes de salida.
     */
    private final Queue<ContextoConexion> cola;

    /**
     * Observadores registrados.
     */
    private final List<Observer> observers;

    /**
     * Último contexto generado, usado durante la notificación.
     */
    private ContextoConexion contextoActual;

    public Dispatcher() {
        this.cola = new LinkedBlockingQueue<>();
        this.observers = new ArrayList<>();
        this.contextoActual = null;
    }

    /**
     * Recibe una solicitud de envío, crea un contexto, lo almacena y notifica a
     * los observadores.
     *
     * @param host dirección del host destino
     * @param puerto puerto destino
     * @param bytes bytes a transmitir
     */
    @Override
    public void dispatch(String host, int puerto, byte[] bytes) {
        if (host == null || host.isBlank()) {
            System.out.println("[Dispatcher] Host inválido.");
            return;
        }

        if (puerto <= 0) {
            System.out.println("[Dispatcher] Puerto inválido.");
            return;
        }

        if (bytes == null || bytes.length == 0) {
            System.out.println("[Dispatcher] Bytes nulos o vacíos.");
            return;
        }

        contextoActual = new ContextoConexion(host, puerto, bytes);
        cola.offer(contextoActual);

        System.out.println("[Dispatcher] Contexto agregado a la cola.");
        notifyObservers();
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

    /**
     * Notifica a los observadores registrados con el contexto actual.
     */
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