/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.presentacion.mvc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.dominio.fachada.FachadaJuego;
import org.dominio.fachada.IFachadaJuego;
import org.presentacion.mvc.paneles.PanelControles;
import org.presentacion.mvc.paneles.PanelJugador;
import org.presentacion.mvc.paneles.PanelTablero;

/**
 * 
 * @author Equipo 1
 */
public class MainJuego {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int fichasPorJugador = 4;
            int casillasSegurasPorAla = 1;
            int numeroSalidaDado = 5;

            Color colorJ1 = new Color(0x795548); 
            Color colorJ2 = new Color(0x78909C); 
            Color colorJ3 = new Color(0x8E24AA); 
            Color colorJ4 = new Color(0xEC407A); 
            Color[] coloresJugadores = {colorJ1, colorJ2, colorJ3, colorJ4};

            IFachadaJuego fachadaJuego = new FachadaJuego(null);
            ModeloJuego modeloJuego = new ModeloJuego(fachadaJuego);
            ControladorJuego controladorJuego = new ControladorJuego(modeloJuego);

            BufferedImage avatar1 = cargarImagen("/avatar1.jpg");
            BufferedImage avatar2 = cargarImagen("/avatar2.jpg");
            BufferedImage avatar3 = cargarImagen("/avatar3.jpg");
            BufferedImage avatar4 = cargarImagen("/avatar4.jpg");
            
            PanelTablero panelTablero = new PanelTablero(coloresJugadores, fichasPorJugador, casillasSegurasPorAla, numeroSalidaDado);
            PanelControles panelControles = new PanelControles(controladorJuego);

            PanelJugador panelJ1 = new PanelJugador("Jugador 1", avatar1, colorJ1);
            PanelJugador panelJ2 = new PanelJugador("Jugador 2", avatar2, colorJ2);
            PanelJugador panelJ3 = new PanelJugador("Jugador 3", avatar3, colorJ3);
            PanelJugador panelJ4 = new PanelJugador("Jugador 4", avatar4, colorJ4);

            PanelJugador[] panelesJugadores = {panelJ1, panelJ2, panelJ3, panelJ4};

            FrmTableroJuego ventana = new FrmTableroJuego(
                    controladorJuego,
                    modeloJuego,
                    panelTablero,
                    panelControles,
                    panelesJugadores
            );

            ventana.setTitle("Juego de Parchís");
            ventana.setSize(1000, 750);
            ventana.setLocationRelativeTo(null); 
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);
        });
    }
    
    private static BufferedImage cargarImagen(String ruta) {
        try {
            return ImageIO.read(MainJuego.class.getResource(ruta));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen: " + ruta);
            return null;
        }
    }
}