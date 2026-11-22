package org.interpretar;

import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.io.UncheckedIOException;
import org.code_desc.CodeDesc;
import org.dominio.fachada.IFachadaJuego;
import org.dtos.ErrorJuegoDTO;
import org.dtos.CrearPartidaDTO;
import org.dtos.PartidaCreadaDTO;
import org.dtos.IDTO;
import org.dtos.LanzarDadoDTO;
import org.dtos.ResultadoLanzarDadoDTO;
import org.dtos.ResultadoSeleccionarFichaDTO;
import org.dtos.SeleccionarFichaDTO;

public class Interpretador {
    private final IFachadaJuego juego;
    private final Consumer<byte[]> difusor;
    private final BiConsumer<Integer, byte[]> enviadorDeErrores;

    public Interpretador(
            IFachadaJuego juego,
            Consumer<byte[]> difusor,
            BiConsumer<Integer, byte[]> enviadorDeErrores) {
        if (juego == null || difusor == null || enviadorDeErrores == null) {
            throw new IllegalArgumentException("La fachada y los canales de respuesta son obligatorios.");
        }
        this.juego = juego;
        this.difusor = difusor;
        this.enviadorDeErrores = enviadorDeErrores;
    }

    public synchronized void procesarEntrada(int idJugadorAsignado, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            enviarError(idJugadorAsignado, "La solicitud no contiene datos.");
            return;
        }

        IDTO solicitud;
        try {
            solicitud = CodeDesc.bytesAObjeto(bytes);
        } catch (IllegalArgumentException | UncheckedIOException e) {
            enviarError(idJugadorAsignado, e.getMessage());
            return;
        }
        if (solicitud == null) {
            enviarError(idJugadorAsignado, "No se pudo reconstruir el DTO de la solicitud.");
            return;
        }

        try {
            IDTO respuesta;
            if (solicitud instanceof CrearPartidaDTO crearPartida) {
                if (juego == null) {
                    throw new IllegalStateException("La fachada del juego no está disponible.");
                }
                PartidaCreadaDTO creada = juego.crearPartida(crearPartida);
                respuesta = creada;
            } else if (solicitud instanceof LanzarDadoDTO lanzarDado) {
                validarJugadorAsignado(idJugadorAsignado, lanzarDado.getIdJugador());
                ResultadoLanzarDadoDTO resultado = juego.lanzarDado(lanzarDado);
                respuesta = resultado;
            } else if (solicitud instanceof SeleccionarFichaDTO seleccionarFicha) {
                validarJugadorAsignado(idJugadorAsignado, seleccionarFicha.getIdJugador());
                ResultadoSeleccionarFichaDTO resultado = juego.seleccionarFicha(seleccionarFicha);
                respuesta = resultado;
            } else {
                throw new IllegalArgumentException(
                        "Tipo de DTO de solicitud no soportado: " + solicitud.getClass().getName());
            }

            byte[] respuestaSerializada = CodeDesc.objetoABytes(respuesta);
            if (respuestaSerializada == null) {
                throw new IllegalStateException("No se pudo crear la respuesta de la solicitud.");
            }
            difusor.accept(respuestaSerializada);
        } catch (IllegalArgumentException | IllegalStateException e) {
            enviarError(idJugadorAsignado, e.getMessage());
        }
    }

    private void enviarError(int idJugador, String mensaje) {
        enviadorDeErrores.accept(
                idJugador,
                CodeDesc.objetoABytes(new ErrorJuegoDTO(mensaje)));
    }

    private void validarJugadorAsignado(int jugadorAsignado, int jugadorSolicitado) {
        if (jugadorAsignado != jugadorSolicitado) {
            throw new IllegalArgumentException(
                    "La conexión pertenece al jugador " + jugadorAsignado
                    + ", no al jugador " + jugadorSolicitado + ".");
        }
    }
}
