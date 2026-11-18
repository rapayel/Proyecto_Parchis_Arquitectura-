package org.interpretar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.UncheckedIOException;
import org.dominio.entidades.Ficha;
import org.dominio.entidades.Jugador;
import org.dominio.entidades.Tablero;
import org.dominio.fachada.FachadaJuego;
import org.code_desc.CodeDesc;
import org.dtos.AsignacionJugadorDTO;
import org.entrada.Receptor;
import org.entrada.ServidorTCP;

public final class InterpretadorServidor {
    private static final int PUERTO_PREDETERMINADO = 5000;
    private static final int NUMERO_PARA_SACAR_FICHA = 5;
    private static final int CASILLAS_SEGURAS_POR_ALA = 1;
    private static final int NUMERO_JUGADORES_PREDETERMINADO = 2;
    private static final int FICHAS_POR_JUGADOR = 4;

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

        List<Jugador> jugadores = crearJugadores(numeroJugadores);
        Tablero tablero = new Tablero(jugadores, NUMERO_PARA_SACAR_FICHA, CASILLAS_SEGURAS_POR_ALA);

        ServidorTCP servidor = new ServidorTCP(
                puerto,
                numeroJugadores,
                (idJugador, partidaIniciada) -> CodeDesc.objetoABytes(
                        new AsignacionJugadorDTO(idJugador, numeroJugadores, partidaIniciada))
        );
        Interpretador interpretador = new Interpretador(
                new FachadaJuego(tablero),
                servidor::enviarATodos,
                (idJugador, bytes) -> {
                    try {
                        servidor.enviarAJugador(idJugador, bytes);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
        );
        servidor.addObserver(new Receptor(interpretador::procesarEntrada));
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener, "Detener-ServidorTCP"));

        servidor.iniciar();
        System.out.println("[InterpretadorServidor] Partida lista en el puerto "
                + servidor.getPuertoEscucha() + " para " + numeroJugadores + " jugadores.");
    }

    private static List<Jugador> crearJugadores(int numeroJugadores) {
        List<Jugador> jugadores = new ArrayList<>();
        for (int idJugador = 1; idJugador <= numeroJugadores; idJugador++) {
            List<Ficha> fichas = new ArrayList<>();
            for (int numeroFicha = 1; numeroFicha <= FICHAS_POR_JUGADOR; numeroFicha++) {
                fichas.add(new Ficha(idJugador * 10 + numeroFicha, null));
            }
            jugadores.add(new Jugador(idJugador, "Jugador " + idJugador, "Avatar" + idJugador, fichas));
        }
        return jugadores;
    }
}
