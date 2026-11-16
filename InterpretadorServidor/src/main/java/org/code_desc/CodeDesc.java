package org.code_desc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.io.ObjectInputFilter;
import org.dtos.IDTO;
import org.dtos.LanzarDadoDTO;
import org.dtos.SeleccionarFichaDTO;

public final class CodeDesc {

    private CodeDesc() {
    }

    public static byte[] objetoABytes(IDTO objeto) {
        if (objeto == null) {
            return null;
        }

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream salida = new ObjectOutputStream(bytes)) {
            salida.writeObject(objeto);
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo serializar el objeto.", e);
        }
    }

    public static IDTO bytesAObjeto(byte[] datos) {
        if (datos == null || datos.length == 0) {
            return null;
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(new ByteArrayInputStream(datos))) {
            entrada.setObjectInputFilter(info -> {
                if (info.depth() > 8 || info.references() > 32
                        || info.streamBytes() > 1_048_576) {
                    return ObjectInputFilter.Status.REJECTED;
                }

                Class<?> tipo = info.serialClass();
                if (tipo == null) {
                    return ObjectInputFilter.Status.UNDECIDED;
                }
                if (tipo == String.class
                        || tipo == LanzarDadoDTO.class
                        || tipo == SeleccionarFichaDTO.class) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
                return ObjectInputFilter.Status.REJECTED;
            });
            Object objeto = entrada.readObject();
            if (!(objeto instanceof IDTO dto)) {
                throw new IllegalArgumentException("El mensaje no contiene un DTO.");
            }
            return dto;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudieron deserializar los datos.", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("No se encontró la clase serializada.", e);
        }
    }
}
