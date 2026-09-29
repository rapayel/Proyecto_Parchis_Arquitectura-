/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.dominio.fachada.FachadaJuego;
import org.dominio.fachada.IFachadaJuego;
import org.presentacion.interfaces.IModeloLectura;
import org.presentacion.interfaces.Observador;
import org.presentacion.mvc.paneles.PanelControles;
import org.presentacion.mvc.paneles.PanelJugador;
import org.presentacion.mvc.paneles.PanelTablero;

/**
 * 
 * @author Equipo 1
 */
public class FrmTableroJuego extends JFrame implements Observador {
    private ControladorJuego controlador;
    private PanelTablero panelTablero;
    private PanelControles panelControles;
    private PanelJugador panelJugador1;
    private PanelJugador panelJugador2;
    private PanelJugador panelJugador3;
    private PanelJugador panelJugador4;

    public FrmTableroJuego(ControladorJuego controlador, IModeloLectura modelo) {
        this.controlador = controlador;
        modelo.registrarObservador(this);

        this.setTitle("Juego de Parchís");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        this.setLayout(new BorderLayout(10, 10));

        this.panelTablero = new PanelTablero();
        this.panelControles = new PanelControles(this.controlador);

        this.panelJugador1 = new PanelJugador("Jugador 1", null, new Color(0xFF3B30));
        this.panelJugador2 = new PanelJugador("Jugador 2", null, new Color(0x5AA0F5));
        this.panelJugador3 = new PanelJugador("Jugador 3", null, new Color(0xF7F03C));
        this.panelJugador4 = new PanelJugador("Jugador 4", null, new Color(0x6CCB4B));

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        panelNorte.add(panelJugador1);
        panelNorte.add(panelJugador2);

        JPanel panelSur = new JPanel(new BorderLayout());
        JPanel panelJugadoresSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        panelJugadoresSur.add(panelJugador3);
        panelJugadoresSur.add(panelJugador4);
        
        panelSur.add(panelJugadoresSur, BorderLayout.NORTH);
        panelSur.add(panelControles, BorderLayout.SOUTH);

        this.add(panelNorte, BorderLayout.NORTH);
        this.add(panelTablero, BorderLayout.CENTER);
        this.add(panelSur, BorderLayout.SOUTH);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void update(IModeloLectura modelo) {
        actualizarVistaPrivado(modelo);
    }

    private void actualizarVistaPrivado(IModeloLectura modelo) {
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IFachadaJuego fachadaJuego = new FachadaJuego(null);
            ModeloJuego modeloJuego = new ModeloJuego(fachadaJuego);
            ControladorJuego controladorJuego = new ControladorJuego(modeloJuego);
            new FrmTableroJuego(controladorJuego, modeloJuego);
        });
    }
}