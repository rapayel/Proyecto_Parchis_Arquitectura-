package org.code_desc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;

public final class CodeDesc {

    private CodeDesc() {
    }

    public static <T extends Serializable> byte[] objetoABytes(T objeto) {
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

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T bytesAObjeto(byte[] datos) {
        if (datos == null || datos.length == 0) {
            return null;
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(new ByteArrayInputStream(datos))) {
            return (T) entrada.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudieron deserializar los datos.", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("No se encontró la clase serializada.", e);
        }
    }
}
