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
public class Jugador {
    private int id;
    private String nombre;
    private String avatar;
    private List<Ficha> fichas;

    public Jugador(int id, String nombre, String avatar, List<Ficha> fichas) {
        if (fichas == null || fichas.isEmpty()) {
            throw new IllegalArgumentException("El jugador debe crearse con sus fichas correspondientes.");
        }
        if (fichas.size() != 4) {
            throw new IllegalArgumentException("El jugador debe contar exactamente con 4 fichas.");
        }

        this.id = id;
        this.nombre = nombre;
        this.avatar = avatar;
        this.fichas = new ArrayList<>();

        for (Ficha ficha : fichas) {
            agregarFicha(ficha);
        }
    }

    private void agregarFicha(Ficha ficha) {
        if (ficha == null) {
            throw new IllegalArgumentException("La ficha no puede ser nula.");
        }
        ficha.setJugador(this);
        this.fichas.add(ficha);
    }

    public Ficha obtenerFicha(int idFicha) {
        for (Ficha ficha : fichas) {
            if (ficha.getId() == idFicha) {
                return ficha;
            }
        }
        return null;
    }

    public Ficha obtenerFichaEnJuegoDistintaDe(Ficha fichaExcluida) {
        for (Ficha ficha : fichas) {
            if (ficha.estaEnJuego() && ficha != fichaExcluida) {
                return ficha;
            }
        }
        return null;
    }

    public boolean tieneFichaEnJuego() {
        for (Ficha ficha : fichas) {
            if (ficha.estaEnJuego()) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneFichasEnSalida() {
        for (Ficha ficha : fichas) {
            if (ficha.estaEnSalida()) {
                return true;
            }
        }
        return false;
    }

    public boolean todasLlegaronAMeta() {
        for (Ficha ficha : fichas) {
            if (!ficha.estaEnMeta()) {
                return false;
            }
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPerfilCompleto() {
        return nombre + " (" + avatar + ")";
    }
}