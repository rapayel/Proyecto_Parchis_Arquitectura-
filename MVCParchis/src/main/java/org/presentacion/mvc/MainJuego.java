/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.presentacion.mvc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
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
            IFachadaJuego fachadaJuego = new FachadaJuego(null);
            ModeloJuego modeloJuego = new ModeloJuego(fachadaJuego);
            ControladorJuego controladorJuego = new ControladorJuego(modeloJuego);
            PanelTablero panelTablero = new PanelTablero();
            PanelControles panelControles = new PanelControles(controladorJuego);
            BufferedImage avatar1 = cargarImagen("/avatar1.jpg"); 
            BufferedImage avatar2 = cargarImagen("/avatar2.jpg");
            BufferedImage avatar3 = cargarImagen("/avatar3.jpg");
            BufferedImage avatar4 = cargarImagen("/avatar4.jpg");

            PanelJugador panelJugador1 = new PanelJugador("Jugador 1", avatar1, new Color(0xFF3B30));
            PanelJugador panelJugador2 = new PanelJugador("Jugador 2", avatar2, new Color(0x5AA0F5));
            PanelJugador panelJugador3 = new PanelJugador("Jugador 3", avatar3, new Color(0xF7F03C));
            PanelJugador panelJugador4 = new PanelJugador("Jugador 4", avatar4, new Color(0x6CCB4B));
            FrmTableroJuego ventanaPrincipal = new FrmTableroJuego(
                    controladorJuego,
                    modeloJuego,
                    panelTablero,
                    panelControles,
                    panelJugador1,
                    panelJugador2,
                    panelJugador3,
                    panelJugador4
            );

            ventanaPrincipal.setVisible(true);
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