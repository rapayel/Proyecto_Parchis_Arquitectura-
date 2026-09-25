/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc.paneles;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.presentacion.mvc.ControladorJuego;

/**
 * 
 * @author Equipo 1
 */
public class PanelControles extends JPanel{
    private JButton btnLanzarDado;
    private ControladorJuego controlador;

    public PanelControles(ControladorJuego controlador) {
        this.controlador = controlador;
        this.btnLanzarDado = new JButton("Lanzar Dado");

        btnLanzarDado.addActionListener(e -> {
            int idJugadorPrueba = 1; 
            this.controlador.lanzarDado(idJugadorPrueba);
        });

        this.add(btnLanzarDado);
    }
}
