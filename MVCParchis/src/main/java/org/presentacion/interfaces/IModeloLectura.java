/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.presentacion.interfaces;

/**
 *
 * @author Equipo 1
 */
public interface IModeloLectura {
    void registrarObservador(Observador o);
    void removerObservador(Observador o);
    void notificarObservadores();
    
    int getValorDado();
    boolean puedeVolverATirar();
    boolean puedeSacarFicha();
    boolean tieneMovimientoValido();
    
    int getIdFichaSeleccionada();
    int getPosicionFicha();
    boolean isCapturo();
    boolean isLlegoAMeta();
    boolean isCambioTurno();

    int getIdJugadorTurnoActual();
    boolean isDadoLanzadoEnTurno();
}