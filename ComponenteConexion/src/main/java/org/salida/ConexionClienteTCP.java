package org.salida;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

public final class ConexionClienteTCP implements AutoCloseable {
    private static final int MAX_BYTES_POR_MENSAJE = 1_048_576;
    private static final int TIEMPO_CONEXION_MS = 5000;

    private final Socket socket;
    private final DataInputStream entrada;
    private final DataOutputStream salida;
    private final Consumer<byte[]> receptor;
    private final Consumer<IOException> manejadorErrores;
    private volatile boolean cerrada;

    public ConexionClienteTCP(
            String host,
            int puerto,
            Consumer<byte[]> receptor,
            Consumer<IOException> manejadorErrores) throws IOException {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("La dirección del servidor es obligatoria.");
        }
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 1 y 65535.");
        }
        if (receptor == null || manejadorErrores == null) {
            throw new IllegalArgumentException("Los manejadores de mensajes son obligatorios.");
        }

        Socket nuevaConexion = new Socket();
        DataInputStream nuevaEntrada;
        DataOutputStream nuevaSalida;
        try {
            nuevaConexion.connect(new InetSocketAddress(host, puerto), TIEMPO_CONEXION_MS);
            nuevaEntrada = new DataInputStream(nuevaConexion.getInputStream());
            nuevaSalida = new DataOutputStream(nuevaConexion.getOutputStream());
        } catch (IOException | RuntimeException e) {
            try {
                nuevaConexion.close();
            } catch (IOException errorCierre) {
                e.addSuppressed(errorCierre);
            }
            throw e;
        }

        this.socket = nuevaConexion;
        this.entrada = nuevaEntrada;
        this.salida = nuevaSalida;
        this.receptor = receptor;
        this.manejadorErrores = manejadorErrores;
        Thread hiloRecepcion = new Thread(this::recibirMensajes, "ClienteTCP-Recepcion");
        hiloRecepcion.setDaemon(true);
        hiloRecepcion.start();
    }

    public synchronized void enviar(byte[] bytes) throws IOException {
        if (cerrada || socket.isClosed()) {
            throw new IOException("La conexión con el servidor está cerrada.");
        }
        validarMensaje(bytes);
        salida.writeInt(bytes.length);
        salida.write(bytes);
        salida.flush();
    }

    private void recibirMensajes() {
        try {
            while (!cerrada) {
                int longitud;
                try {
                    longitud = entrada.readInt();
                } catch (EOFException e) {
                    throw new IOException("El servidor cerró la conexión.", e);
                }
                if (longitud < 1 || longitud > MAX_BYTES_POR_MENSAJE) {
                    throw new IOException("Longitud de mensaje inválida: " + longitud);
                }

                byte[] bytes = new byte[longitud];
                entrada.readFully(bytes);
                try {
                    receptor.accept(bytes);
                } catch (RuntimeException e) {
                    manejadorErrores.accept(new IOException("No se pudo procesar la respuesta.", e));
                }
            }
        } catch (IOException e) {
            if (!cerrada) {
                cerrada = true;
                manejadorErrores.accept(e);
                cerrarSocket();
            }
        }
    }

    private static void validarMensaje(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0 || bytes.length > MAX_BYTES_POR_MENSAJE) {
            throw new IOException("El mensaje debe contener entre 1 y "
                    + MAX_BYTES_POR_MENSAJE + " bytes.");
        }
    }

    @Override
    public void close() {
        cerrada = true;
        cerrarSocket();
    }

    private void cerrarSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            manejadorErrores.accept(e);
        }
    }
}
