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
public class ResultadoLanzarDadoDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idJugador;
    private int resultadoDado;
    private boolean puedeVolverATirar;
    private boolean puedeSacarFicha;
    private boolean tieneMovimientoValido;

    public ResultadoLanzarDadoDTO(
            int idJugador,
            int resultadoDado,
            boolean puedeVolverATirar,
            boolean puedeSacarFicha,
            boolean tieneMovimientoValido) {

        this.idJugador = idJugador;
        this.resultadoDado = resultadoDado;
        this.puedeVolverATirar = puedeVolverATirar;
        this.puedeSacarFicha = puedeSacarFicha;
        this.tieneMovimientoValido = tieneMovimientoValido;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public int getResultadoDado() {
        return resultadoDado;
    }

    public boolean isPuedeVolverATirar() {
        return puedeVolverATirar;
    }

    public boolean isPuedeSacarFicha() {
        return puedeSacarFicha;
    }

    public boolean isTieneMovimientoValido() {
        return tieneMovimientoValido;
    }
}