/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dtos;

import java.io.Serializable;

/**
 * Posición de una ficha en el tablero: -1 en el círculo de salida, 1..68 casilla del
 * recorrido común, 69..75 casilla del pasillo final de su jugador y 76 en la meta.
 *
 * @author Equipo 1
 */
public class FichaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idJugador;
    private int idFicha;
    private int posicion;

    public FichaDTO(int idJugador, int idFicha, int posicion) {
        this.idJugador = idJugador;
        this.idFicha = idFicha;
        this.posicion = posicion;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public int getIdFicha() {
        return idFicha;
    }

    public int getPosicion() {
        return posicion;
    }
}
