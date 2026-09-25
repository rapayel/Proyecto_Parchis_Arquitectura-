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
public class ResultadoSeleccionarFichaDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idJugador;
    private int idFicha;
    private int posicionFicha;
    private boolean capturo;
    private boolean llegoAMeta;
    private boolean cambioTurno;

    public ResultadoSeleccionarFichaDTO(
            int idJugador,
            int idFicha,
            int posicionFicha,
            boolean capturo,
            boolean llegoAMeta,
            boolean cambioTurno) {

        this.idJugador = idJugador;
        this.idFicha = idFicha;
        this.posicionFicha = posicionFicha;
        this.capturo = capturo;
        this.llegoAMeta = llegoAMeta;
        this.cambioTurno = cambioTurno;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public int getIdFicha() {
        return idFicha;
    }

    public int getPosicionFicha() {
        return posicionFicha;
    }

    public boolean isCapturo() {
        return capturo;
    }

    public boolean isLlegoAMeta() {
        return llegoAMeta;
    }

    public boolean isCambioTurno() {
        return cambioTurno;
    }
}