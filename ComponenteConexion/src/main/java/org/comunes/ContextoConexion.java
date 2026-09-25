/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.comunes;

/**
 * 
 * @author Equipo 1
 */
public class ContextoConexion {

    private final String host;
    private final int puerto;
    private final byte[] bytes;

    public ContextoConexion(String host, int puerto, byte[] bytes) {
        this.host = host;
        this.puerto = puerto;
        this.bytes = bytes;
    }

    public String getHost() {
        return host;
    }

    public int getPuerto() {
        return puerto;
    }

    public byte[] getBytes() {
        return bytes;
    }
}