/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.salida;


/**
 * Fábrica encargada de ensamblar el mecanismo de salida.
 *
 * Crea el Dispatcher, crea el ClienteTCP, registra el observador y devuelve el
 * contrato público IDispatcher.
 */
public class DispatcherFactory {

    /**
     * Crea el mecanismo de salida configurado.
     *
     * @return dispatcher expuesto mediante el contrato IDispatcher
     */
    public static IDispatcher crearDispatcher() {
        Dispatcher dispatcher = new Dispatcher();
        ClienteTCP clienteTCP = new ClienteTCP();

        dispatcher.addObserver(clienteTCP);

        return dispatcher;
    }
}