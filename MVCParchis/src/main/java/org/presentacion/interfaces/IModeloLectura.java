/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.presentacion.interfaces;

/**
 *
 * @author lagar
 */
public interface IModeloLectura {
    int getValorDado();
    boolean puedeVolverATirar();
    boolean puedeSacarFicha();
    boolean tieneMovimientoValido();
    
    int getIdFichaSeleccionada();
    int getPosicionFicha();
    boolean isCapturo();
    boolean isLlegoAMeta();
    boolean isCambioTurno();
}