/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dtos;

import java.io.Serializable;

/**
 * 
 * @author Equipo 1
 */
public class SeleccionarFichaDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idJugador;
    private int idFicha;

    public SeleccionarFichaDTO(int idJugador, int idFicha) {
        this.idJugador = idJugador;
        this.idFicha = idFicha;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public int getIdFicha() {
        return idFicha;
    }
}