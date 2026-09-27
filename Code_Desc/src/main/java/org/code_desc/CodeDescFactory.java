/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.code_desc;

import java.io.Serializable;

/**
 *
 * @author Equipo 1
 */
public class CodeDescFactory {
    public static <T extends Serializable> ISerializador<T> crearSerializador() {
        return new Serializador<>();
    }
    public static <T extends Serializable> IDeserializador<T> crearDeserializador() {
        return new Deserializador<>();
    }
}
