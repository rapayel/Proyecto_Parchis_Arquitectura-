/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc.paneles;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * 
 * @author Equipo 1
 */
public class PanelTablero extends JPanel {
    private final Color JUGADOR_1 = new Color(0xFF3B30); 
    private final Color JUGADOR_2 = new Color(0x5AA0F5);
    private final Color JUGADOR_3 = new Color(0xF7F03C); 
    private final Color JUGADOR_4 = new Color(0x6CCB4B);
    private final Color COLOR_MARCO = new Color(0x7A5230);
    private final boolean MOSTRAR_NUMEROS = true;
    private final Color[] colores = {JUGADOR_1, JUGADOR_2, JUGADOR_3, JUGADOR_4};
    
    public PanelTablero() {
        setPreferredSize(new Dimension(620, 620));
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
 
        double S = Math.min(getWidth(), getHeight()) - 20; 
        g.translate((getWidth() - S) / 2.0, (getHeight() - S) / 2.0);
 
        double c = S * 0.33;        
        double a = (S - c) / 2.0;   
        double[][] pos = {{0, 0}, {S - a, 0}, {S - a, S - a}, {0, S - a}};
        for (int i = 0; i < 4; i++) {
            dibujarCasa(g, pos[i][0], pos[i][1], a, i, S);
        }

        for (int k = 0; k < 4; k++) {
            AffineTransform original = g.getTransform();
            g.translate(S / 2, S / 2);
            g.rotate(k * Math.PI / 2);
            g.translate(-S / 2, -S / 2);
            dibujarBrazo(g, k, S, a, c);
            g.setTransform(original);
        }
 
        dibujarCentro(g, S, a, c);
 
        g.setColor(COLOR_MARCO);
        g.setStroke(new BasicStroke((float) Math.max(3, S / 100)));
        g.draw(new Rectangle2D.Double(0, 0, S, S));
        g.dispose();
    }
 
    private void dibujarBrazo(Graphics2D g, int k, double S, double a, double c) {
        double cw = c / 3.0; 
        double ch = a / 8.0; 
        Color color = colores[k];
        float grosor = (float) Math.max(1, S / 400);
        Font fuente = new Font("SansSerif", Font.PLAIN, Math.max(6, (int) (ch * 0.5)));
 
        for (int r = 0; r < 8; r++) {         
            for (int j = 0; j < 3; j++) {     
                double x = a + j * cw;
                double y = r * ch;
 
                boolean carril = (j == 1 && r >= 1);
                boolean salida = (j == 0 && r == 4);
                g.setColor(carril || salida ? color : Color.WHITE);
                g.fill(new Rectangle2D.Double(x, y, cw, ch));
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(grosor));
                g.draw(new Rectangle2D.Double(x, y, cw, ch));
 
                if (MOSTRAR_NUMEROS && !carril) {
                    int base = (j == 0) ? 35 + r : (j == 1 ? 34 : 33 - r);
                    int numero = Math.floorMod(base - 17 * k - 1, 68) + 1;
                    dibujarNumero(g, String.valueOf(numero), x + cw / 2, y + ch / 2, k, fuente);
                }
            }
        }
    }

    private void dibujarNumero(Graphics2D g, String txt, double cx, double cy, int k, Font f) {
        double deseado = (k % 2 == 1) ? -Math.PI / 2 : 0;
        double extra = deseado - k * Math.PI / 2;
        AffineTransform t = g.getTransform();
        g.translate(cx, cy);
        g.rotate(extra);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.BLACK);
        g.drawString(txt, -fm.stringWidth(txt) / 2f, (fm.getAscent() - fm.getDescent()) / 2f);
        g.setTransform(t);
    }
 
    private void dibujarCentro(Graphics2D g, double S, double a, double c) {
        double m = S / 2;
        Point2D.Double p1 = new Point2D.Double(a, a);
        Point2D.Double p2 = new Point2D.Double(a + c, a);
        Point2D.Double p3 = new Point2D.Double(a + c, a + c);
        Point2D.Double p4 = new Point2D.Double(a, a + c);
        Point2D.Double ce = new Point2D.Double(m, m);
 
        triangulo(g, p1, p2, ce, colores[0]); 
        triangulo(g, p2, p3, ce, colores[1]); 
        triangulo(g, p3, p4, ce, colores[2]); 
        triangulo(g, p4, p1, ce, colores[3]); 
    }
 
    private void triangulo(Graphics2D g, Point2D p, Point2D q, Point2D r, Color color) {
        Path2D.Double t = new Path2D.Double();
        t.moveTo(p.getX(), p.getY());
        t.lineTo(q.getX(), q.getY());
        t.lineTo(r.getX(), r.getY());
        t.closePath();
        g.setColor(color);
        g.fill(t);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke((float) Math.max(1, getWidth() / 400.0)));
        g.draw(t);
    }
    
    private void dibujarCasa(Graphics2D g, double x, double y, double tam, int indice, double S) {
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(x, y, tam, tam));

        double cx = x + tam / 2, cy = y + tam / 2;
        double r = tam * 0.43;
        float grosor = (float) Math.max(1, S / 400);
 
        g.setColor(colores[indice]);
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(grosor));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
 
        double r2 = r * 0.82; 
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(grosor * 1.4f));
        g.draw(new Ellipse2D.Double(cx - r2, cy - r2, 2 * r2, 2 * r2));
 
        double r3 = tam * 0.09; 
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(cx - r3, cy - r3, 2 * r3, 2 * r3));
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(grosor));
        g.draw(new Ellipse2D.Double(cx - r3, cy - r3, 2 * r3, 2 * r3));
        
        int n = 0;
        for (int p = 0; p < 8; p++) {
            Color otro;
            do { otro = colores[n++ % 4]; } while (otro == colores[indice]);
            double ang = Math.toRadians(p * 45 - 90);
            double largo = r3 * (p % 2 == 0 ? 0.9 : 0.65);
            double ancho = r3 * 0.28;
            Path2D.Double petalo = new Path2D.Double();
            petalo.moveTo(cx + Math.cos(ang) * largo, cy + Math.sin(ang) * largo);
            petalo.lineTo(cx + Math.cos(ang + Math.PI / 4) * ancho, cy + Math.sin(ang + Math.PI / 4) * ancho);
            petalo.lineTo(cx + Math.cos(ang - Math.PI / 4) * ancho, cy + Math.sin(ang - Math.PI / 4) * ancho);
            petalo.closePath();
            g.setColor(otro);
            g.fill(petalo);
        }
    }

    public void actualizarEstadoTablero() {
    }
}