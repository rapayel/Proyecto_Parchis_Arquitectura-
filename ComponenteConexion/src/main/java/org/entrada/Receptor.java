/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.entrada;

import org.comunes.ContextoConexion;
import org.comunes.Observer;

/**
 * 
 * @author Equipo 1
 */
public class Receptor implements Observer {
    private final IReceptorExterno receptorExterno;

    public Receptor(IReceptorExterno receptorExterno) {
        this.receptorExterno = receptorExterno;
    }

    @Override
    public void update(ContextoConexion contexto) {
        if (contexto == null || contexto.getBytes() == null || contexto.getBytes().length == 0) {
            System.out.println("[Receptor] Contexto inválido.");
            return;
        }

        System.out.println("[Receptor] Bytes recibidos desde el mecanismo de entrada.");
        receptorExterno.recibir(contexto.getBytes());
    }
}