/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.salida;

/**
 * Contrato público del mecanismo de salida.
 *
 * Permite solicitar el envío de bytes hacia un host y puerto específicos.
 */
public interface IDispatcher {

    /**
     * Solicita el envío de bytes a un destino.
     *
     * @param host dirección del host destino
     * @param puerto puerto destino
     * @param bytes bytes a transmitir
     */
    void dispatch(String host, int puerto, byte[] bytes);
}