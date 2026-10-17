/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.entidades;

import java.util.ArrayList;
import java.util.Collections;
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
    private int casillaSalida;
    private int casillaEntradaPasillo;

    public Jugador(int id, String nombre, String avatar, List<Ficha> fichas) {
        if (fichas == null || fichas.isEmpty()) {
            throw new IllegalArgumentException("El jugador debe crearse con sus fichas correspondientes.");
        }
        if (fichas.size() < 3 || fichas.size() > 6) {
            throw new IllegalArgumentException("El jugador debe contar con entre 3 y 6 fichas.");
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

    public void asignarRecorrido(int casillaSalida, int casillaEntradaPasillo) {
        this.casillaSalida = casillaSalida;
        this.casillaEntradaPasillo = casillaEntradaPasillo;
    }

    public Ficha obtenerFicha(int idFicha) {
        for (Ficha ficha : fichas) {
            if (ficha.getId() == idFicha) {
                return ficha;
            }
        }
        return null;
    }

    public boolean tieneFichaEnJuego() {
        for (Ficha ficha : fichas) {
            if (ficha.puedeMoverse()) {
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

    public List<Ficha> getFichas() {
        return Collections.unmodifiableList(fichas);
    }

    public int getCasillaSalida() {
        return casillaSalida;
    }

    public int getCasillaEntradaPasillo() {
        return casillaEntradaPasillo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getPerfilCompleto() {
        return nombre + " (" + avatar + ")";
    }
}
