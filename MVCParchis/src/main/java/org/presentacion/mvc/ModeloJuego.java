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
public class ModeloJuego implements IModeloJuego, IModeloLectura{
    private List<Observador> observadores;
    private IFachadaJuego fachadaJuego;
    private ResultadoLanzarDadoDTO ultimoResultadoDado;
    private ResultadoSeleccionarFichaDTO ultimoResultadoFicha;
    
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
        this.ultimoResultadoDado = fachadaJuego.lanzarDado(new LanzarDadoDTO(idJugador));
        notificarObservadores();
    }

    @Override
    public void solicitarSeleccionarFicha(int idJugador, int idFicha) {
        this.ultimoResultadoFicha = fachadaJuego.seleccionarFicha(new SeleccionarFichaDTO(idJugador, idFicha));
        notificarObservadores();
    }
    
    @Override
    public ResultadoLanzarDadoDTO getUltimoResultadoDado() {
        return ultimoResultadoDado;
    }

    @Override
    public ResultadoSeleccionarFichaDTO getUltimoResultadoFicha() {
        return ultimoResultadoFicha;
    }
}
