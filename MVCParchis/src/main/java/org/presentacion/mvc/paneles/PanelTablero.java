/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc.paneles;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.presentacion.mvc.ControladorJuego;

/**
 * 
 * @author Equipo 1
 */
public class PanelTablero extends JPanel {
    private final Color COLOR_MARCO = new Color(0x7A5230);
    private final boolean MOSTRAR_NUMEROS = true;
    
    private final Color[] coloresJugadores;
    private final int fichasPorJugador;
    private final int casillasSegurasPorAla;
    private final int numeroSalidaDado;

    private List<FichaVista> fichas;
    private ControladorJuego controlador;
    private int idJugadorHumano = 1;
    private double offsetX = 0;
    private double offsetY = 0;

    public PanelTablero(Color[] coloresJugadores, int fichasPorJugador, int casillasSegurasPorAla, int numeroSalidaDado) {
        this.coloresJugadores = coloresJugadores;
        this.fichasPorJugador = Math.max(3, Math.min(6, fichasPorJugador));
        this.casillasSegurasPorAla = Math.max(1, Math.min(5, casillasSegurasPorAla));
        this.numeroSalidaDado = numeroSalidaDado;
        this.fichas = new ArrayList<>();

        setPreferredSize(new Dimension(620, 620));
        setOpaque(false);

        inicializarFichas();
        configurarInteraccionRaton();
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    private void inicializarFichas() {
        fichas.clear();
        for (int i = 0; i < coloresJugadores.length; i++) {
            int idJugador = i + 1;
            int baseFichaId = idJugador * 10; 
            for (int f = 1; f <= fichasPorJugador; f++) {
                int idFicha = baseFichaId + f;
                FichaVista ficha = new FichaVista(idFicha, idJugador, coloresJugadores[i]);
                fichas.add(ficha);
            }
        }
    }

    private void configurarInteraccionRaton() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controlador == null) return;
                double mx = e.getX() - offsetX;
                double my = e.getY() - offsetY;
                FichaVista elegida = null;
                double mejorDist = Double.MAX_VALUE;
                for (FichaVista f : fichas) {
                    if (f.getIdJugador() == idJugadorHumano) {
                        double dist = Math.hypot(mx - f.getX(), my - f.getY());
                        if (dist <= f.getRadio() + 4 && dist < mejorDist) { 
                            mejorDist = dist;
                            elegida = f;
                        }
                    }
                }

                if (elegida != null) {
                    try {
                        controlador.seleccionarFicha(idJugadorHumano, elegida.getIdFicha());
                    } catch (Exception ex) {
                        System.out.println("Jugada no válida: " + ex.getMessage());
                    }
                }
            }
        });
    }

    public void moverFicha(int idJugador, int idFicha, int nuevaPosicion) {
        for (FichaVista f : fichas) {
            if (f.getIdJugador() == idJugador && f.getIdFicha() == idFicha) {
                f.setCasillaActual(nuevaPosicion);
                break;
            }
        }
        repaint();
    }

    private int numeroCasilla(int base, int k) {
        return Math.floorMod(39 - base + 17 * k, 68) + 1;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
 
        double S = Math.min(getWidth(), getHeight()) - 20; 
        offsetX = (getWidth() - S) / 2.0;   
        offsetY = (getHeight() - S) / 2.0;  
        g.translate(offsetX, offsetY);
 
        double c = S * 0.33;        
        double a = (S - c) / 2.0;   
        double[][] posCasas = {{0, 0}, {S - a, 0}, {S - a, S - a}, {0, S - a}};

        for (int i = 0; i < 4; i++) {
            dibujarCasa(g, posCasas[i][0], posCasas[i][1], a, i, S);
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

        actualizarCoordenadasFichas(S, a, c, posCasas);
        dibujarFichas(g);

        g.dispose();
    }

    private void actualizarCoordenadasFichas(double S, double tamCasa, double c, double[][] posCasas) {
        double radioFicha = (S / 620.0) * 11;
        double radioDistribucion = tamCasa * 0.28;

        for (FichaVista f : fichas) {
            f.setRadio(radioFicha);
            int pos = f.getCasillaActual();

            if (pos <= 0) { 
                int idxJugador = f.getIdJugador() - 1;
                double cx = posCasas[idxJugador][0] + tamCasa / 2.0;
                double cy = posCasas[idxJugador][1] + tamCasa / 2.0;

                List<FichaVista> enCasa = obtenerFichasEnCasa(f.getIdJugador());
                int orden = enCasa.indexOf(f);
                int totalEnCasa = Math.max(1, enCasa.size());

                double angulo = Math.toRadians((360.0 / totalEnCasa) * orden - 90);
                f.setPosicion(cx + Math.cos(angulo) * radioDistribucion, cy + Math.sin(angulo) * radioDistribucion);
            } else { 
                Point2D coord = calcularCoordenadaCasilla(pos, S, tamCasa, c);
                if (coord != null) {
                    f.setPosicion(coord.getX(), coord.getY());
                }
            }
        }
        separarFichasSuperpuestas(c);
    }

    private void separarFichasSuperpuestas(double c) {
        Map<Integer, List<FichaVista>> porCasilla = new LinkedHashMap<>();
        for (FichaVista f : fichas) {
            if (f.getCasillaActual() > 0) {
                porCasilla.computeIfAbsent(f.getCasillaActual(), k -> new ArrayList<>()).add(f);
            }
        }

        double largoCasilla = c / 3.0; 

        for (Map.Entry<Integer, List<FichaVista>> entrada : porCasilla.entrySet()) {
            List<FichaVista> grupo = entrada.getValue();
            int n = grupo.size();
            if (n < 2) continue;
            boolean horizontal = (brazoDeCasilla(entrada.getKey()) % 2 == 0);

            double radio = Math.min(grupo.get(0).getRadio() * 0.85, (largoCasilla * 0.9) / (2.0 * n));
            double paso = radio * 2;
            double cx = grupo.get(0).getX();
            double cy = grupo.get(0).getY();

            for (int i = 0; i < n; i++) {
                double desplazamiento = (i - (n - 1) / 2.0) * paso;
                FichaVista f = grupo.get(i);
                f.setRadio(radio);
                if (horizontal) {
                    f.setPosicion(cx + desplazamiento, cy);
                } else {
                    f.setPosicion(cx, cy + desplazamiento);
                }
            }
        }
    }
    private int brazoDeCasilla(int casilla) {
        for (int k = 0; k < 4; k++) {
            for (int r = 0; r < 8; r++) {
                for (int j = 0; j < 3; j++) {
                    if (j == 1 && r >= 1) continue;
                    int base = (j == 0) ? 35 + r : (j == 1 ? 34 : 33 - r);
                    if (numeroCasilla(base, k) == casilla) {
                        return k;
                    }
                }
            }
        }
        return 0;
    }

    private List<FichaVista> obtenerFichasEnCasa(int idJugador) {
        List<FichaVista> lista = new ArrayList<>();
        for (FichaVista f : fichas) {
            if (f.getIdJugador() == idJugador && f.getCasillaActual() <= 0) {
                lista.add(f);
            }
        }
        return lista;
    }

    private Point2D calcularCoordenadaCasilla(int casilla, double S, double a, double c) {
        double cw = c / 3.0;
        double ch = a / 8.0;

        for (int k = 0; k < 4; k++) {
            for (int r = 0; r < 8; r++) {
                for (int j = 0; j < 3; j++) {
                  
                    if (j == 1 && r >= 1) continue; 

                    
                    int base = (j == 0) ? 35 + r : (j == 1 ? 34 : 33 - r);
                    int numCasilla = numeroCasilla(base, k);

                    if (numCasilla == casilla) {
                        double localX = a + j * cw + cw / 2.0;
                        double localY = r * ch + ch / 2.0;
                        double cx = S / 2.0;
                        double cy = S / 2.0;
                        double rad = k * Math.PI / 2.0;

                        double rx = cx + (localX - cx) * Math.cos(rad) - (localY - cy) * Math.sin(rad);
                        double ry = cy + (localX - cx) * Math.sin(rad) + (localY - cy) * Math.cos(rad);

                        return new Point2D.Double(rx, ry);
                    }
                }
            }
        }
        return new Point2D.Double(S / 2.0, S / 2.0); 
    }

    private void dibujarBrazo(Graphics2D g, int k, double S, double a, double c) {
        double cw = c / 3.0; 
        double ch = a / 8.0; 
        Color color = coloresJugadores[k];
        float grosor = (float) Math.max(1, S / 400);
        Font fuente = new Font("SansSerif", Font.PLAIN, Math.max(6, (int) (ch * 0.5)));
 
        for (int r = 0; r < 8; r++) {         
            for (int j = 0; j < 3; j++) {     
                double x = a + j * cw;
                double y = r * ch;
 
                boolean carril = (j == 1 && r >= 1);
                boolean salida = (j == 0 && r == 4);
                boolean esSeguraConfigurada = (j == 0 && r >= (8 - casillasSegurasPorAla));

                if (carril || salida) {
                    g.setColor(color);
                } else if (esSeguraConfigurada) {
                    g.setColor(new Color(210, 210, 210));
                } else {
                    g.setColor(Color.WHITE);
                }

                g.fill(new Rectangle2D.Double(x, y, cw, ch));
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(grosor));
                g.draw(new Rectangle2D.Double(x, y, cw, ch));
 
                if (salida) {
                    dibujarNumero(g, "S:" + numeroSalidaDado, x + cw / 2, y + ch / 2, k, fuente);
                } else if (MOSTRAR_NUMEROS && !carril) {
                    int base = (j == 0) ? 35 + r : (j == 1 ? 34 : 33 - r);
                    int numero = numeroCasilla(base, k); // NUEVO
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
 
        triangulo(g, p1, p2, ce, coloresJugadores[0]); 
        triangulo(g, p2, p3, ce, coloresJugadores[1]); 
        triangulo(g, p3, p4, ce, coloresJugadores[2]); 
        triangulo(g, p4, p1, ce, coloresJugadores[3]); 
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
 
        g.setColor(coloresJugadores[indice]);
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
            do { otro = coloresJugadores[n++ % 4]; } while (otro == coloresJugadores[indice]);
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

    private void dibujarFichas(Graphics2D g) {
        for (FichaVista ficha : fichas) {
            ficha.dibujar(g);
        }
    }

    public List<FichaVista> getFichas() {
        return fichas;
    }
}