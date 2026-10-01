/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.presentacion.mvc.paneles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

/**
 * 
 * @author Equipo 1
 */
public class FichaVista {
    private int idFicha;
    private int idJugador;
    private Color color;
    private int casillaActual;
    private double x;
    private double y;
    private double radio;

    public FichaVista(int idFicha, int idJugador, Color color) {
        this.idFicha = idFicha;
        this.idJugador = idJugador;
        this.color = color;
        this.casillaActual = -1;
    }

    public void dibujar(Graphics2D g2) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Ellipse2D fichaShape = new Ellipse2D.Double(x - radio, y - radio, radio * 2, radio * 2);
        g.setColor(color);
        g.fill(fichaShape);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(fichaShape);

        double radioInner = radio * 0.5;
        Ellipse2D innerShape = new Ellipse2D.Double(x - radioInner, y - radioInner, radioInner * 2, radioInner * 2);
        g.setColor(new Color(255, 255, 255, 180));
        g.fill(innerShape);
        g.draw(innerShape);

        g.dispose();
    }

    public int getIdFicha() {
        return idFicha;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public Color getColor() {
        return color;
    }

    public int getCasillaActual() {
        return casillaActual;
    }

    public void setCasillaActual(int casillaActual) {
        this.casillaActual = casillaActual;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosicion(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }
}