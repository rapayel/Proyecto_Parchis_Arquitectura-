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
    private int progreso;
    private int casillaSalida;
    private Estado estado;

    public Ficha(int id, Jugador jugador) {
        this.id = id;
        this.jugador = jugador;
        this.posicion = -1;
        this.progreso = 0;
        this.casillaSalida = -1;
        this.estado = Estado.SALIDA;
    }

    public void sacarAlTablero(int casillaSalida) {
        if (estado != Estado.SALIDA) {
            throw new IllegalStateException("La ficha no se encuentra en la salida.");
        }
        if (casillaSalida < 1 || casillaSalida > 68) {
            throw new IllegalArgumentException("La casilla de salida no pertenece al circuito.");
        }
        this.casillaSalida = casillaSalida;
        progreso = 1;
        estado = Estado.EN_JUEGO;
        posicion = casillaSalida;
    }

    public boolean avanzarORebotar(int cantidad, int limiteMeta) {
        if (estado == Estado.SALIDA || estado == Estado.META) {
            throw new IllegalStateException("La ficha no se puede mover en este estado.");
        }

        progreso = calcularProgresoDespuesDeAvanzar(cantidad, limiteMeta);
        posicion = posicionParaProgreso(progreso, limiteMeta);

        if (progreso == limiteMeta) {
            estado = Estado.META;
            return true;
        }

        estado = progreso > 68 ? Estado.PASILLO_FINAL : Estado.EN_JUEGO;
        return false;
    }

    int calcularPosicionDespuesDeAvanzar(int cantidad, int limiteMeta) {
        return posicionParaProgreso(calcularProgresoDespuesDeAvanzar(cantidad, limiteMeta), limiteMeta);
    }

    private int calcularProgresoDespuesDeAvanzar(int cantidad, int limiteMeta) {
        int nuevoProgreso = progreso + cantidad;
        if (nuevoProgreso > limiteMeta) {
            nuevoProgreso = limiteMeta - (nuevoProgreso - limiteMeta);
        }
        return nuevoProgreso;
    }

    private int posicionParaProgreso(int progreso, int limiteMeta) {
        if (progreso > 68 || progreso == limiteMeta) {
            return progreso;
        }
        return Math.floorMod(casillaSalida + progreso - 2, 68) + 1;
    }

    public void regresarASalida() {
        posicion = -1;
        progreso = 0;
        casillaSalida = -1;
        estado = Estado.SALIDA;
    }

    public boolean estaEnSalida() {
        return estado == Estado.SALIDA;
    }

    public boolean estaEnJuego() {
        return estado == Estado.EN_JUEGO || estado == Estado.PASILLO_FINAL;
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