/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc.paneles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * 
 * @author Equipo 1
 */
public class PanelJugador extends JPanel {
    private int idJugador; 
    private String nombreJugador;
    private BufferedImage avatar;
    private Color colorJugador;
    private boolean esTurnoActivo;

    public PanelJugador(int idJugador, String nombreJugador, BufferedImage avatar, Color colorJugador) {
        this.idJugador = idJugador;
        this.nombreJugador = nombreJugador;
        this.avatar = avatar;
        this.colorJugador = colorJugador != null ? colorJugador : new Color(70, 130, 180);
        this.esTurnoActivo = false;

        setOpaque(false);
        setPreferredSize(new Dimension(240, 80));
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
        repaint();
    }

    public void setAvatar(BufferedImage avatar) {
        this.avatar = avatar;
        repaint();
    }

    public void setColorJugador(Color colorJugador) {
        this.colorJugador = colorJugador;
        repaint();
    }

    public void setEsTurnoActivo(boolean esTurnoActivo) {
        this.esTurnoActivo = esTurnoActivo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int ancho = getWidth();
        int alto = getHeight();
        RoundRectangle2D tarjeta = new RoundRectangle2D.Float(4, 4, ancho - 8, alto - 8, 20, 20);
        g2.setColor(new Color(255, 255, 255, 230));
        g2.fill(tarjeta);
        if (esTurnoActivo) {
            g2.setColor(colorJugador);
            g2.setStroke(new BasicStroke(3.5f));
        } else {
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(1.5f));
        }
        g2.draw(tarjeta);
        int tamanoColor = 14;
        int xColor = ancho - tamanoColor - 16;
        int yColor = (alto - tamanoColor) / 2;
        g2.setColor(colorJugador);
        g2.fillOval(xColor, yColor, tamanoColor, tamanoColor);
        int diametroAvatar = 52;
        int xAvatar = 14;
        int yAvatar = (alto - diametroAvatar) / 2;

        if (avatar != null) {
            Graphics2D gAvatar = (Graphics2D) g2.create();
            Ellipse2D clipCircular = new Ellipse2D.Float(xAvatar, yAvatar, diametroAvatar, diametroAvatar);
            gAvatar.setClip(clipCircular);
            gAvatar.drawImage(avatar, xAvatar, yAvatar, diametroAvatar, diametroAvatar, null);
            gAvatar.dispose();
        } else {
            g2.setColor(new Color(200, 200, 200));
            g2.fillOval(xAvatar, yAvatar, diametroAvatar, diametroAvatar);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            String inicial = (nombreJugador != null && !nombreJugador.isEmpty()) ? nombreJugador.substring(0, 1).toUpperCase() : "?";
            int xInicial = xAvatar + (diametroAvatar - g2.getFontMetrics().stringWidth(inicial)) / 2;
            int yInicial = yAvatar + ((diametroAvatar - g2.getFontMetrics().getHeight()) / 2) + g2.getFontMetrics().getAscent();
            g2.drawString(inicial, xInicial, yInicial);
        }
        g2.setColor(colorJugador);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(xAvatar, yAvatar, diametroAvatar, diametroAvatar);
        g2.setColor(new Color(40, 40, 40));
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        int xTexto = xAvatar + diametroAvatar + 14;
        int yTexto = (alto / 2) + 5;
        String textoMostrar = nombreJugador != null ? nombreJugador : "Jugador";
        int anchoMaximo = xColor - xTexto - 8;
        if (g2.getFontMetrics().stringWidth(textoMostrar) > anchoMaximo) {
            while (textoMostrar.length() > 3 && g2.getFontMetrics().stringWidth(textoMostrar + "...") > anchoMaximo) {
                textoMostrar = textoMostrar.substring(0, textoMostrar.length() - 1);
            }
            textoMostrar += "...";
        }

        g2.drawString(textoMostrar, xTexto, yTexto);

        g2.dispose();
    }
}