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
public class LanzarDadoDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idJugador;

    public LanzarDadoDTO(int idJugador) {
        this.idJugador = idJugador;
    }

    public int getIdJugador() {
        return idJugador;
    }
}
