/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc;
import java.util.ArrayList;
import java.util.List;
import org.presentacion.interfaces.*;

/**
 * 
 * @author Equipo 1
 */
public class ModeloJuego implements IModeloJuego{
    private List<Observador> observadores;
    private int ultimoDado;
    private int idJugadorTurno;
    
    public ModeloJuego() {
        this.observadores = new ArrayList<>();
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
        for (Observador obs : observadores) {
            obs.update(this);
        }
    }

    @Override
    public void solicitarLanzarDado(int idJugador) {
        //en construccion
        notificarObservadores();
    }

    @Override
    public void solicitarSeleccionarFicha(int idJugador, int idFicha) {
        //en construccion
        
        notificarObservadores();
    }

    @Override
    public int getUltimoDado() {
        return ultimoDado;
    }

    @Override
    public int getIdJugadorTurno() {
        return idJugadorTurno;
    }
}
