/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Equipo 1
 */
public class Casilla {
    private int posicion;
    private boolean segura;
    private List<Ficha> fichas;

    public Casilla(int posicion, boolean segura) {
        this.posicion = posicion;
        this.segura = segura;
        this.fichas = new ArrayList<>();
    }

    public void agregarFicha(Ficha ficha) {
        if (fichas.size() >= 2) {
            throw new IllegalStateException("La casilla está llena (máximo 2 fichas).");
        }
        fichas.add(ficha);
    }

    public void retirarFicha(Ficha ficha) {
        fichas.remove(ficha);
    }

    public boolean estaSegura() {
        return segura;
    }

    public boolean estaOcupada() {
        return !fichas.isEmpty();
    }

    public boolean tieneFichaRival(Jugador jugador) {
        for (Ficha ficha : fichas) {
            if (ficha.getJugador() != jugador) {
                return true;
            }
        }
        return false;
    }

    public boolean formaBarrera() {
        if (fichas.size() < 2) {
            return false;
        }
        Jugador jugador = fichas.get(0).getJugador();
        for (Ficha ficha : fichas) {
            if (ficha.getJugador() != jugador) {
                return false;
            }
        }
        return true;
    }

    public Ficha obtenerFichaRival(Jugador jugador) {
        for (Ficha ficha : fichas) {
            if (ficha.getJugador() != jugador) {
                return ficha;
            }
        }
        return null;
    }

    public int getPosicion() {
        return posicion;
    }

    public List<Ficha> getFichas() {
        return fichas;
    }
}