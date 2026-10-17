/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.entidades;

/**
 * Ficha de un jugador. Su posición se interpreta según su estado:
 * SALIDA = -1 (en el círculo), EN_JUEGO = 1..68 (casilla del recorrido común),
 * PASILLO_FINAL = 69..75 (casilla de su propio pasillo) y META = 76.
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

    public void moverA(int nuevaPosicion, Estado nuevoEstado) {
        if (!puedeMoverse()) {
            throw new IllegalStateException("La ficha no se puede mover en este estado.");
        }
        posicion = nuevaPosicion;
        estado = nuevoEstado;
    }

    public void regresarASalida() {
        posicion = -1;
        estado = Estado.SALIDA;
    }

    public boolean puedeMoverse() {
        return estado == Estado.EN_JUEGO || estado == Estado.PASILLO_FINAL;
    }

    public boolean estaEnSalida() {
        return estado == Estado.SALIDA;
    }

    public boolean estaEnJuego() {
        return estado == Estado.EN_JUEGO;
    }

    public boolean estaEnPasilloFinal() {
        return estado == Estado.PASILLO_FINAL;
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

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public int getPosicion() {
        return posicion;
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
