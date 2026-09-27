/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.code_desc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 
 * @author Equipo 1
 * @param <T> 
 */
class Serializador<T extends Serializable> implements ISerializador<T>{

    @Override
    public byte[] objetoABytes(T objeto) {
        if (objeto == null) return null;
        
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            
            oos.writeObject(objeto);
            oos.flush();
            return bos.toByteArray();
            
        } catch (IOException e) {
            System.err.println("Error en Serializador: " + e.getMessage());
            return null;
        }
    }
}