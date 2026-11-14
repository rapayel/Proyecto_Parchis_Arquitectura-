/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dtos;

/**
 * 
 * @author Equipo 1
 */
public class ResultadoSeleccionarFichaDTO implements IDTO {
    private static final long serialVersionUID = 1L;
    private int idJugador;
    private int idFicha;
    private int posicionFicha;
    private boolean capturo;
    private int idJugadorFichaCapturada;
    private int idFichaCapturada;
    private boolean llegoAMeta;
    private boolean cambioTurno;
    private boolean ganador;

    public ResultadoSeleccionarFichaDTO(
            int idJugador,
            int idFicha,
            int posicionFicha,
            boolean capturo,
            boolean llegoAMeta,
            boolean cambioTurno) {
        this(idJugador, idFicha, posicionFicha, capturo, -1, -1, llegoAMeta, cambioTurno, false);
    }

    public ResultadoSeleccionarFichaDTO(
            int idJugador,
            int idFicha,
            int posicionFicha,
            boolean capturo,
            boolean llegoAMeta,
            boolean cambioTurno,
            boolean ganador) {
        this(idJugador, idFicha, posicionFicha, capturo, -1, -1, llegoAMeta, cambioTurno, ganador);
    }

    public ResultadoSeleccionarFichaDTO(
            int idJugador,
            int idFicha,
            int posicionFicha,
            boolean capturo,
            int idJugadorFichaCapturada,
            int idFichaCapturada,
            boolean llegoAMeta,
            boolean cambioTurno,
            boolean ganador) {

        this.idJugador = idJugador;
        this.idFicha = idFicha;
        this.posicionFicha = posicionFicha;
        this.capturo = capturo;
        this.idJugadorFichaCapturada = idJugadorFichaCapturada;
        this.idFichaCapturada = idFichaCapturada;
        this.llegoAMeta = llegoAMeta;
        this.cambioTurno = cambioTurno;
        this.ganador = ganador;
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

    public int getIdJugadorFichaCapturada() {
        return idJugadorFichaCapturada;
    }

    public int getIdFichaCapturada() {
        return idFichaCapturada;
    }

    public boolean isLlegoAMeta() {
        return llegoAMeta;
    }

    public boolean isCambioTurno() {
        return cambioTurno;
    }

    public boolean isGanador() {
        return ganador;
    }
}