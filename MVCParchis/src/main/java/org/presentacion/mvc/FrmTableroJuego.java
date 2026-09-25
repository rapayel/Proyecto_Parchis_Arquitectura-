/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.presentacion.interfaces.IModeloJuego;
import org.presentacion.interfaces.Observador;
import org.presentacion.mvc.paneles.PanelControles;
import org.presentacion.mvc.paneles.PanelTablero;

/**
 * 
 * @author Equipo 1
 */
public class FrmTableroJuego extends JFrame implements Observador{
    private ControladorJuego controlador;
    private PanelTablero panelTablero;
    private PanelControles panelControles;  
    
    public FrmTableroJuego(ControladorJuego controlador, IModeloJuego modelo) {
        this.controlador = controlador;
        modelo.registrarObservador(this);
        this.panelTablero = new PanelTablero();
        this.panelControles = new PanelControles(this.controlador);
        this.setLayout(new BorderLayout());
        this.add(panelTablero, BorderLayout.CENTER);
        this.add(panelControles, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void update(IModeloJuego modelo) {
        actualizarVistaPrivado(modelo);
    }

    private void actualizarVistaPrivado(IModeloJuego modelo) {
        this.panelTablero.actualizarEstadoTablero(modelo);
        this.repaint();
    }
}
