/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.presentacion.interfaces;

/**
 *
 * @author Equipo 1
 */
public interface IModeloJuego {
    public void registrarObservador(Observador o);
    public void removerObservador(Observador o);
    public void notificarObservadores();
    public void solicitarLanzarDado(int idJugador);
    public void solicitarSeleccionarFicha(int idJugador, int idFicha);
    public int getUltimoDado();
    public int getIdJugadorTurno();
}
