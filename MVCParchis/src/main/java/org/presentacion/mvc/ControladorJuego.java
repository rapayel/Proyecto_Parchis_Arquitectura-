/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc;

import org.presentacion.interfaces.IModeloJuego;

/**
 * 
 * @author Equipo 1
 */
public class ControladorJuego {
    private IModeloJuego modelo;

    public ControladorJuego(IModeloJuego modelo) {
        this.modelo = modelo;
    }
    
    public void lanzarDado(int idJugador) {
        modelo.solicitarLanzarDado(idJugador);
    }

    public void seleccionarFicha(int idJugador, int idFicha) {
        modelo.solicitarSeleccionarFicha(idJugador, idFicha);
    }
}
