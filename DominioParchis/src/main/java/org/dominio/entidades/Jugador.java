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

    public Jugador(int id, String nombre, String avatar) {
        this.id = id;
        this.nombre = nombre;
        this.avatar = avatar;
        this.fichas = new ArrayList<>();
    }

    public void agregarFicha(Ficha ficha) {
        fichas.add(ficha);
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

    public List<Ficha> getFichas() {
        return fichas;
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
}