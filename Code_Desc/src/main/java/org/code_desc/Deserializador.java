/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.code_desc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * @author Equipo 1
 * @param <T>
 */
class Deserializador<T extends Serializable> implements IDeserializador<T>{

    @SuppressWarnings("unchecked")
    @Override
    public T bytesAObjeto(byte[] datos) {
        if (datos == null || datos.length == 0) return null;

        try (ByteArrayInputStream bis = new ByteArrayInputStream(datos);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
            
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Error en Deserializador: " + e.getMessage());
            return null;
        }
    }
}