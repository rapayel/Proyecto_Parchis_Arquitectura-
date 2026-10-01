/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
    private PanelJugador[] panelesJugadores;

    public FrmTableroJuego(
            ControladorJuego controlador, 
            IModeloLectura modelo,
            PanelTablero panelTablero,
            PanelControles panelControles,
            PanelJugador[] panelesJugadores) {

        this.controlador = controlador;
        modelo.registrarObservador(this);

        this.panelTablero = panelTablero;
        this.panelControles = panelControles;
        this.panelesJugadores = panelesJugadores;

        this.setTitle("Juego de Parchís");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        PanelFondo panelPrincipal = new PanelFondo("/fondoparchis.png");
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        this.setContentPane(panelPrincipal);

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        panelNorte.setOpaque(false);
        if (panelesJugadores.length > 0) panelNorte.add(this.panelesJugadores[0]);
        if (panelesJugadores.length > 1) panelNorte.add(this.panelesJugadores[1]);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        panelSur.setOpaque(false);
        if (panelesJugadores.length > 2) panelSur.add(this.panelesJugadores[2]);
        if (panelesJugadores.length > 3) panelSur.add(this.panelesJugadores[3]);

        JPanel panelEste = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panelEste.setOpaque(false);
        panelEste.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        panelEste.add(this.panelControles);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(this.panelTablero, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
        panelPrincipal.add(panelEste, BorderLayout.EAST);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
    }

    @Override
    public void update(IModeloLectura modelo) {
        actualizarVistaPrivado(modelo);
    }

    private void actualizarVistaPrivado(IModeloLectura modelo) {
    }

    private class PanelFondo extends JPanel {
        private Image imagenFondo;

        public PanelFondo(String rutaImagen) {
            try {
                this.imagenFondo = ImageIO.read(getClass().getResource(rutaImagen));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("No se pudo cargar la imagen de fondo: " + rutaImagen);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagenFondo != null) {
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}