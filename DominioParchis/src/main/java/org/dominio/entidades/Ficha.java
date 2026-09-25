/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.entidades;

/**
 * 
 * @author Equipo 1
 */
public class Ficha {
    private int id;
    private Jugador jugador;
    private int posicion;
    private Estado estado;

    public Ficha(int id, Jugador jugador) {
        this.id = id;
        this.jugador = jugador;
        this.posicion = -1;
        this.estado = Estado.SALIDA;
    }

    public void sacarAlTablero(int casillaSalida) {
        if (estado != Estado.SALIDA) {
            throw new IllegalStateException("La ficha no se encuentra en la salida.");
        }
        estado = Estado.EN_JUEGO;
        posicion = casillaSalida;
    }

    public boolean avanzarORebotar(int cantidad, int limiteMeta) {
        if (estado == Estado.SALIDA || estado == Estado.META) {
            throw new IllegalStateException("La ficha no se puede mover en este estado.");
        }

        int nuevaPosicion = posicion + cantidad;

        if (nuevaPosicion == limiteMeta) {
            posicion = limiteMeta;
            estado = Estado.META;
            return true;
        } else if (nuevaPosicion > limiteMeta) {
            int exceso = nuevaPosicion - limiteMeta;
            posicion = limiteMeta - exceso;
            return false;
        } else {
            posicion = nuevaPosicion;
            return false;
        }
    }

    public void regresarASalida() {
        posicion = -1;
        estado = Estado.SALIDA;
    }

    public boolean estaEnSalida() {
        return estado == Estado.SALIDA;
    }

    public boolean estaEnJuego() {
        return estado == Estado.EN_JUEGO;
    }

    public boolean estaEnMeta() {
        return estado == Estado.META;
    }

    public int getId() {
        return id;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public Estado getEstado() {
        return estado;
    }
    
    public enum Estado {
        SALIDA,
        EN_JUEGO,
        PASILLO_FINAL,
        META
    }
}