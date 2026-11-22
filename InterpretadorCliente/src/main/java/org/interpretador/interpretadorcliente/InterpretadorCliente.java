package org.interpretador.interpretadorcliente;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.code_desc.CodeDesc;
import org.dtos.IDTO;
import org.dtos.LanzarDadoDTO;
import org.dtos.SeleccionarFichaDTO;
import org.dtos.CrearPartidaDTO;
import org.presentacion.interfaces.IInterpretadorCliente;
import org.presentacion.mvc.ControladorJuego;
import org.presentacion.mvc.FrmConfiguracionPartida;
import org.presentacion.mvc.MainJuego;
import org.presentacion.mvc.ModeloJuego;
import org.salida.ConexionClienteTCP;

public final class InterpretadorCliente implements IInterpretadorCliente, AutoCloseable {
    private static final int PUERTO_PREDETERMINADO = 5000;

    private final ModeloJuego modelo;
    private final ConexionClienteTCP conexion;

    private InterpretadorCliente(ModeloJuego modelo, String host, int puerto) throws IOException {
        this.modelo = modelo;
        this.conexion = new ConexionClienteTCP(
                host,
                puerto,
                this::procesarRespuesta,
                error -> modelo.reportarErrorConexion(error.getMessage()));
    }

    public static void main(String[] args) {
        if (args.length > 2) {
            mostrarError("Uso: InterpretadorCliente [host del servidor] [puerto].");
            return;
        }
        if (args.length == 0 && GraphicsEnvironment.isHeadless()) {
            mostrarError("Indica el host del servidor al iniciar el cliente.");
            return;
        }

        String host = args.length >= 1 ? args[0] : solicitarHost();
        if (host == null || host.isBlank()) {
            return;
        }

        int puerto;
        try {
            puerto = args.length == 2 ? Integer.parseInt(args[1]) : solicitarPuerto();
        } catch (NumberFormatException e) {
            mostrarError("El puerto debe ser un número válido.");
            return;
        }
        if (puerto < 1 || puerto > 65535) {
            mostrarError("El puerto debe estar entre 1 y 65535.");
            return;
        }

        ModeloJuego modelo = new ModeloJuego();
        try {
            InterpretadorCliente interpretador = new InterpretadorCliente(modelo, host.trim(), puerto);
            if (!GraphicsEnvironment.isHeadless()) {
                int crear = JOptionPane.showConfirmDialog(
                        null,
                        "¿Deseas crear una partida nueva?",
                        "Crear partida",
                        JOptionPane.YES_NO_OPTION);
                if (crear == JOptionPane.YES_OPTION) {
                    int[] configuracion = FrmConfiguracionPartida.solicitarConfiguracion();
                    if (configuracion != null) {
                        interpretador.solicitarCrearPartida(
                                configuracion[0],
                                configuracion[1],
                                configuracion[2]);
                    }
                }
            }
            SwingUtilities.invokeLater(() -> {
                JFrame ventana = MainJuego.crearVentana(
                        new ControladorJuego(interpretador),
                        modelo);
                ventana.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        interpretador.close();
                    }
                });
                ventana.setVisible(true);
            });
        } catch (IOException e) {
            mostrarError("No se pudo conectar con " + host + ":" + puerto + ". " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private static String solicitarHost() {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }
        return JOptionPane.showInputDialog(null, "Dirección del servidor:", "localhost");
    }

    private static int solicitarPuerto() {
        if (GraphicsEnvironment.isHeadless()) {
            return PUERTO_PREDETERMINADO;
        }
        String texto = JOptionPane.showInputDialog(
                null,
                "Puerto del servidor:",
                String.valueOf(PUERTO_PREDETERMINADO));
        if (texto == null || texto.isBlank()) {
            return PUERTO_PREDETERMINADO;
        }
        return Integer.parseInt(texto.trim());
    }

    private static void mostrarError(String mensaje) {
        System.err.println(mensaje);
        if (!GraphicsEnvironment.isHeadless()) {
            JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarRespuesta(byte[] bytes) {
        try {
            IDTO dto = CodeDesc.bytesAObjeto(bytes);
            if (dto == null) {
                throw new IllegalArgumentException("La respuesta del servidor está vacía.");
            }
            modelo.recibirDTO(dto);
        } catch (RuntimeException e) {
            modelo.cancelarSolicitud("No se pudo interpretar la respuesta del servidor: " + e.getMessage());
        }
    }

    @Override
    public void solicitarLanzarDado() {
        enviarSolicitud(true, -1);
    }

    @Override
    public void solicitarSeleccionarFicha(int idFicha) {
        enviarSolicitud(false, idFicha);
    }

    @Override
    public void solicitarCrearPartida(
            int fichasPorJugador,
            int casillasSegurasPorAla,
            int numeroSalidaDado) {
        try {
            conexion.enviar(CodeDesc.objetoABytes(new CrearPartidaDTO(
                    fichasPorJugador,
                    casillasSegurasPorAla,
                    numeroSalidaDado)));
        } catch (IOException e) {
            modelo.reportarErrorConexion(e.getMessage());
            close();
        } catch (RuntimeException e) {
            modelo.cancelarSolicitud(e.getMessage());
        }
    }

    private void enviarSolicitud(boolean lanzarDado, int idFicha) {
        try {
            if (lanzarDado) {
                modelo.prepararLanzamiento();
            } else {
                modelo.prepararSeleccion(idFicha);
            }

            IDTO solicitud = lanzarDado
                    ? new LanzarDadoDTO(modelo.getIdJugadorLocal())
                    : new SeleccionarFichaDTO(modelo.getIdJugadorLocal(), idFicha);
            conexion.enviar(CodeDesc.objetoABytes(solicitud));
        } catch (IOException e) {
            modelo.reportarErrorConexion(e.getMessage());
            close();
        } catch (RuntimeException e) {
            modelo.cancelarSolicitud(e.getMessage());
        }
    }

    @Override
    public void close() {
        conexion.close();
    }
}
