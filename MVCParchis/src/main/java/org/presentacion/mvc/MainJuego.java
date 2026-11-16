package org.presentacion.mvc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.presentacion.mvc.paneles.PanelControles;
import org.presentacion.mvc.paneles.PanelJugador;
import org.presentacion.mvc.paneles.PanelTablero;

public final class MainJuego {
    private static final Color[] COLORES_JUGADORES = {
        new Color(0x795548),
        new Color(0x78909C),
        new Color(0x8E24AA),
        new Color(0xEC407A)
    };

    private MainJuego() {
    }

    public static JFrame crearVentana(ControladorJuego controlador, ModeloJuego modelo) {
        PanelControles panelControles = new PanelControles(controlador);
        PanelTablero panelTablero = new PanelTablero(COLORES_JUGADORES, 4, 1, 5);
        PanelJugador[] panelesJugadores = new PanelJugador[COLORES_JUGADORES.length];
        for (int i = 0; i < panelesJugadores.length; i++) {
            int numeroJugador = i + 1;
            panelesJugadores[i] = new PanelJugador(
                    numeroJugador,
                    "Jugador " + numeroJugador,
                    cargarImagen("/avatar" + numeroJugador + ".jpg"),
                    COLORES_JUGADORES[i]);
        }

        JFrame ventana = new FrmTableroJuego(
                controlador,
                modelo,
                panelTablero,
                panelControles,
                panelesJugadores);
        ventana.setSize(1000, 750);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return ventana;
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
