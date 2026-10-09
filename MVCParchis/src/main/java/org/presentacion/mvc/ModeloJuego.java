/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc;

import java.util.ArrayList;
import java.util.List;
import org.presentacion.interfaces.*;
import org.dominio.fachada.IFachadaJuego;
import org.dtos.*;

/**
 * 
 * @author Equipo 1
 */
public class ModeloJuego implements IModeloJuego, IModeloLectura {
    private List<Observador> observadores;
    private IFachadaJuego fachadaJuego;
    private ResultadoLanzarDadoDTO ultimoResultadoDado;
    private ResultadoSeleccionarFichaDTO ultimoResultadoFicha;
    private int idJugadorTurnoActual = 1;
    private int idJugadorUltimaJugada = -1;
    private boolean dadoLanzadoEnTurno = false;
    
    public ModeloJuego(IFachadaJuego fachadaJuego) {
        this.observadores = new ArrayList<>();
        this.fachadaJuego = fachadaJuego;
    }

    @Override
    public void registrarObservador(Observador o) {
        observadores.add(o);
    }

    @Override
    public void removerObservador(Observador o) {
        observadores.remove(o);
    }

    @Override
    public void notificarObservadores() {
        for (Observador observador : observadores) {
            observador.update(this);
        }
    }

    @Override
    public void solicitarLanzarDado(int idJugador) {
        this.ultimoResultadoFicha = null;

        this.ultimoResultadoDado = fachadaJuego.lanzarDado(new LanzarDadoDTO(idJugador));
        
        if (ultimoResultadoDado != null) {
            this.dadoLanzadoEnTurno = ultimoResultadoDado.isTieneMovimientoValido();
            if (!this.dadoLanzadoEnTurno) {
                this.idJugadorTurnoActual = (this.idJugadorTurnoActual % 4) + 1;
            }
        }
        notificarObservadores();
    }

    @Override
    public void solicitarSeleccionarFicha(int idJugador, int idFicha) {
        this.ultimoResultadoFicha = fachadaJuego.seleccionarFicha(new SeleccionarFichaDTO(idJugador, idFicha));
        
        if (ultimoResultadoFicha != null) {
            this.idJugadorUltimaJugada = idJugador;
            this.dadoLanzadoEnTurno = false;
            if (ultimoResultadoFicha.isCambioTurno()) {
                this.idJugadorTurnoActual = (this.idJugadorTurnoActual % 4) + 1;
            }
        }
        
        notificarObservadores();
    }
    
    @Override
    public int getValorDado() {
        return ultimoResultadoDado != null ? ultimoResultadoDado.getResultadoDado() : 0;
    }

    @Override
    public boolean puedeVolverATirar() {
        return ultimoResultadoDado != null && ultimoResultadoDado.isPuedeVolverATirar();
    }

    @Override
    public boolean puedeSacarFicha() {
        return ultimoResultadoDado != null && ultimoResultadoDado.isPuedeSacarFicha();
    }

    @Override
    public boolean tieneMovimientoValido() {
        return ultimoResultadoDado != null && ultimoResultadoDado.isTieneMovimientoValido();
    }

    @Override
    public int getIdFichaSeleccionada() {
        return ultimoResultadoFicha != null ? ultimoResultadoFicha.getIdFicha() : -1;
    }

    @Override
    public int getPosicionFicha() {
        return ultimoResultadoFicha != null ? ultimoResultadoFicha.getPosicionFicha() : -1;
    }

    @Override
    public boolean isCapturo() {
        return ultimoResultadoFicha != null && ultimoResultadoFicha.isCapturo();
    }

    @Override
    public boolean isLlegoAMeta() {
        return ultimoResultadoFicha != null && ultimoResultadoFicha.isLlegoAMeta();
    }

    @Override
    public boolean isCambioTurno() {
        return ultimoResultadoFicha != null && ultimoResultadoFicha.isCambioTurno();
    }

    @Override
    public int getIdJugadorTurnoActual() {
        return idJugadorTurnoActual;
    }

    @Override
    public boolean isDadoLanzadoEnTurno() {
        return dadoLanzadoEnTurno;
    }

    @Override
    public int getIdJugadorUltimaJugada() {
        return idJugadorUltimaJugada;
    }
}