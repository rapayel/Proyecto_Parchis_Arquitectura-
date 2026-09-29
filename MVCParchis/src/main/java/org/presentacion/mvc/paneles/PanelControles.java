/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc.paneles;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.presentacion.mvc.ControladorJuego;

/**
 * 
 * @author lagar
 */
public class PanelControles extends JPanel {
    private JButton btnLanzarDado;
    private ControladorJuego controlador;
    private ComponenteDado componenteDado;
    private Timer timerAnimacion;
    private Random random;

    public PanelControles(ControladorJuego controlador) {
        this.controlador = controlador;
        this.random = new Random();

        setLayout(new BorderLayout(15, 15));
        setOpaque(false);

        componenteDado = new ComponenteDado();
        this.add(componenteDado, BorderLayout.CENTER);
        btnLanzarDado = new JButton("Lanzar Dado") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(220, 100, 30));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 140, 0));
                } else if (!isEnabled()) {
                    g2.setColor(new Color(180, 180, 180));
                } else {
                    g2.setColor(new Color(245, 120, 20));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                int anchoTexto = g2.getFontMetrics().stringWidth(getText());
                int altoTexto = g2.getFontMetrics().getAscent();
                g2.drawString(getText(), (getWidth() - anchoTexto) / 2, (getHeight() + altoTexto) / 2 - 3);

                g2.dispose();
            }
        };

        btnLanzarDado.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnLanzarDado.setPreferredSize(new Dimension(150, 45));
        btnLanzarDado.setContentAreaFilled(false);
        btnLanzarDado.setBorderPainted(false);
        btnLanzarDado.setFocusPainted(false);
        btnLanzarDado.setCursor(new Cursor(Cursor.HAND_CURSOR));

        timerAnimacion = new Timer(80, e -> {
            int caraAleatoria = random.nextInt(6) + 1;
            componenteDado.setCara(caraAleatoria);
        });

        btnLanzarDado.addActionListener(e -> {
            iniciarAnimacionYLanzar();
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.add(btnLanzarDado);

        this.add(panelBoton, BorderLayout.SOUTH);
    }

    private void iniciarAnimacionYLanzar() {
        btnLanzarDado.setEnabled(false);
        timerAnimacion.start();

        Timer timerDetener = new Timer(2000, e -> {
            timerAnimacion.stop();

            int idJugadorPrueba = 1;
            this.controlador.lanzarDado(idJugadorPrueba);

            btnLanzarDado.setEnabled(true);
        });
        
        timerDetener.setRepeats(false);
        timerDetener.start();
    }

    public void setHabilitarBotonLanzar(boolean habilitar) {
        btnLanzarDado.setEnabled(habilitar);
    }

    public void setCaraDado(int cara) {
        componenteDado.setCara(cara);
    }

    private class ComponenteDado extends JPanel {

        private int caraActual = 1;

        public ComponenteDado() {
            setOpaque(false);
            setPreferredSize(new Dimension(110, 110));
        }

        public void setCara(int cara) {
            if (cara >= 1 && cara <= 6) {
                this.caraActual = cara;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int ancho = getWidth();
            int alto = getHeight();
            int tamanoDado = Math.min(ancho, alto) - 20;

            if (tamanoDado <= 0) {
                g2.dispose();
                return;
            }

            int x = (ancho - tamanoDado) / 2;
            int y = (alto - tamanoDado) / 2;

            RoundRectangle2D dado = new RoundRectangle2D.Float(x, y, tamanoDado, tamanoDado, 20, 20);
            g2.setColor(Color.WHITE);
            g2.fill(dado);

            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(dado);

            g2.setColor(new Color(30, 30, 30));
            int radioPunto = tamanoDado / 8;

            int xIzq = x + tamanoDado / 4;
            int xCentro = x + tamanoDado / 2;
            int xDer = x + (tamanoDado * 3) / 4;

            int ySup = y + tamanoDado / 4;
            int yCentro = y + tamanoDado / 2;
            int yInf = y + (tamanoDado * 3) / 4;

            switch (caraActual) {
                case 1 -> dibujarPunto(g2, xCentro, yCentro, radioPunto);
                case 2 -> {
                    dibujarPunto(g2, xIzq, ySup, radioPunto);
                    dibujarPunto(g2, xDer, yInf, radioPunto);
                }
                case 3 -> {
                    dibujarPunto(g2, xIzq, ySup, radioPunto);
                    dibujarPunto(g2, xCentro, yCentro, radioPunto);
                    dibujarPunto(g2, xDer, yInf, radioPunto);
                }
                case 4 -> {
                    dibujarPunto(g2, xIzq, ySup, radioPunto);
                    dibujarPunto(g2, xDer, ySup, radioPunto);
                    dibujarPunto(g2, xIzq, yInf, radioPunto);
                    dibujarPunto(g2, xDer, yInf, radioPunto);
                }
                case 5 -> {
                    dibujarPunto(g2, xIzq, ySup, radioPunto);
                    dibujarPunto(g2, xDer, ySup, radioPunto);
                    dibujarPunto(g2, xCentro, yCentro, radioPunto);
                    dibujarPunto(g2, xIzq, yInf, radioPunto);
                    dibujarPunto(g2, xDer, yInf, radioPunto);
                }
                case 6 -> {
                    dibujarPunto(g2, xIzq, ySup, radioPunto);
                    dibujarPunto(g2, xDer, ySup, radioPunto);
                    dibujarPunto(g2, xIzq, yCentro, radioPunto);
                    dibujarPunto(g2, xDer, yCentro, radioPunto);
                    dibujarPunto(g2, xIzq, yInf, radioPunto);
                    dibujarPunto(g2, xDer, yInf, radioPunto);
                }
            }

            g2.dispose();
        }

        private void dibujarPunto(Graphics2D g2, int x, int y, int radio) {
            g2.fillOval(x - radio / 2, y - radio / 2, radio, radio);
        }
    }
}