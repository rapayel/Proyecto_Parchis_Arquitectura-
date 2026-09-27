/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.code_desc;

import java.io.Serializable;

/**
 *
 * @author Equipo 1
 * @param <T>
 */
public interface ISerializador<T extends Serializable> {
    byte[] objetoABytes(T objeto);
}
