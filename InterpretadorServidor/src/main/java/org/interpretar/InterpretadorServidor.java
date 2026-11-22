package org.interpretar;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.dominio.fachada.FachadaJuego;
import org.code_desc.CodeDesc;
import org.dtos.AsignacionJugadorDTO;
import org.entrada.Receptor;
import org.entrada.ServidorTCP;

public final class InterpretadorServidor {
    private static final int PUERTO_PREDETERMINADO = 5000;
    private static final int NUMERO_JUGADORES_PREDETERMINADO = 4;

    private InterpretadorServidor() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            throw new IllegalArgumentException("Uso: InterpretadorServidor [jugadores 2-4] [puerto].");
        }
        int numeroJugadores = args.length == 0
                ? NUMERO_JUGADORES_PREDETERMINADO
                : Integer.parseInt(args[0]);
        int puerto = args.length < 2 ? PUERTO_PREDETERMINADO : Integer.parseInt(args[1]);
        if (numeroJugadores < 2 || numeroJugadores > 4) {
            throw new IllegalArgumentException("La partida requiere entre 2 y 4 jugadores.");
        }
        int[] configuracion = {4, 1, 5};

        ServidorTCP servidor = new ServidorTCP(
                puerto,
                numeroJugadores,
                (idJugador, partidaIniciada) -> CodeDesc.objetoABytes(
                        new AsignacionJugadorDTO(
                                idJugador,
                                numeroJugadores,
                                partidaIniciada,
                                configuracion[0],
                                configuracion[1],
                                configuracion[2]))
        );
        Interpretador interpretador = new Interpretador(
                new FachadaJuego(numeroJugadores),
                servidor::enviarATodos,
                (idJugador, bytes) -> {
                    try {
                        servidor.enviarAJugador(idJugador, bytes);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                },
                creada -> {
                    configuracion[0] = creada.getFichasPorJugador();
                    configuracion[1] = creada.getCasillasSegurasPorAla();
                    configuracion[2] = creada.getNumeroSalidaDado();
                }
        );
        servidor.addObserver(new Receptor(interpretador::procesarEntrada));
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener, "Detener-ServidorTCP"));

        servidor.iniciar();
        System.out.println("[InterpretadorServidor] Servidor disponible en el puerto "
                + servidor.getPuertoEscucha() + " para " + numeroJugadores + " jugadores.");
    }
}
