/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.presentacion.mvc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.dominio.entidades.Ficha;
import org.dominio.entidades.Jugador;
import org.dominio.entidades.Tablero;
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

            List<Jugador> listaJugadoresDominio = new ArrayList<>();
            for (int idx = 1; idx <= 4; idx++) {
                List<Ficha> fichasJugador = new ArrayList<>();
                int baseFichaId = idx * 10;
                for (int i = 1; i <= fichasPorJugador; i++) {
                    fichasJugador.add(new Ficha(baseFichaId + i, null));
                }
                Jugador jugador = new Jugador(idx, "Jugador " + idx, "Avatar" + idx, fichasJugador);
                listaJugadoresDominio.add(jugador);
            }

            Tablero tablero = new Tablero(listaJugadoresDominio, numeroSalidaDado, casillasSegurasPorAla);
            IFachadaJuego fachadaJuego = new FachadaJuego(tablero);

            ModeloJuego modeloJuego = new ModeloJuego(fachadaJuego);
            ControladorJuego controladorJuego = new ControladorJuego(modeloJuego);

            BufferedImage avatar1 = cargarImagen("/avatar1.jpg");
            BufferedImage avatar2 = cargarImagen("/avatar2.jpg");
            BufferedImage avatar3 = cargarImagen("/avatar3.jpg");
            BufferedImage avatar4 = cargarImagen("/avatar4.jpg");

            int idJugadorVentana = 1;

            PanelControles panelControles = new PanelControles(controladorJuego, idJugadorVentana);
            PanelTablero panelTablero = new PanelTablero(coloresJugadores, fichasPorJugador, casillasSegurasPorAla, numeroSalidaDado);

            PanelJugador panelJ1 = new PanelJugador(1, "Jugador 1", avatar1, colorJ1);
            PanelJugador panelJ2 = new PanelJugador(2, "Jugador 2", avatar2, colorJ2);
            PanelJugador panelJ3 = new PanelJugador(3, "Jugador 3", avatar3, colorJ3);
            PanelJugador panelJ4 = new PanelJugador(4, "Jugador 4", avatar4, colorJ4);

            PanelJugador[] panelesJugadores = {panelJ1, panelJ2, panelJ3, panelJ4};

            FrmTableroJuego ventana = new FrmTableroJuego(
                    controladorJuego,
                    modeloJuego,
                    panelTablero,
                    panelControles,
                    panelesJugadores
            );

            ventana.setTitle("Juego de Parchís - Jugador " + idJugadorVentana);
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