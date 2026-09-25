/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.entidades;

import org.dtos.ResultadoLanzarDadoDTO;
import org.dtos.SeleccionarFichaDTO;
import org.dtos.LanzarDadoDTO;
import org.dtos.ResultadoSeleccionarFichaDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author Equipo 1
 */
public class Tablero {
    private List<Jugador> jugadores;
    private List<Casilla> casillas;
    private int jugadorTurno;
    private int resultadoDado;
    private boolean dadoLanzadoEnTurno;
    private int turnosConsecutivosSeis;
    private Ficha ultimaFichaMovida;
    private final int numeroParaSacarFicha;
    private final int casillasSegurasPorAla;
    private final int totalCasillas = 68;

    public Tablero(int numeroParaSacarFicha, int casillasSegurasPorAla) {
        if (numeroParaSacarFicha < 1 || numeroParaSacarFicha > 6) {
            throw new IllegalArgumentException("El número para sacar ficha debe estar entre 1 y 6.");
        }
        if (casillasSegurasPorAla < 1 || casillasSegurasPorAla > 5) {
            throw new IllegalArgumentException("El número de casillas seguras debe estar entre 1 y 5.");
        }

        this.numeroParaSacarFicha = numeroParaSacarFicha;
        this.casillasSegurasPorAla = casillasSegurasPorAla;

        this.jugadores = new ArrayList<>();
        this.casillas = new ArrayList<>();
        this.jugadorTurno = 0;
        this.resultadoDado = 0;
        this.dadoLanzadoEnTurno = false;
        this.turnosConsecutivosSeis = 0;

        inicializarCasillas();
    }

    private void inicializarCasillas() {
        for (int i = 1; i <= totalCasillas; i++) {
            boolean esSegura = (i % (totalCasillas / 4) <= casillasSegurasPorAla);
            casillas.add(new Casilla(i, esSegura));
        }
    }

    public void agregarJugador(Jugador jugador) {
        this.jugadores.add(jugador);
    }

    public Jugador getJugadorTurnoActual() {
        if (jugadores.isEmpty()) return null;
        return jugadores.get(jugadorTurno);
    }

    public ResultadoLanzarDadoDTO lanzarDado(LanzarDadoDTO dto) {
        Jugador jugador = obtenerJugador(dto.getIdJugador());
        validarTurno(jugador);

        if (dadoLanzadoEnTurno) {
            throw new IllegalStateException("Ya lanzaste el dado en este turno.");
        }

        resultadoDado = new Random().nextInt(6) + 1;
        dadoLanzadoEnTurno = true;

        if (resultadoDado == 6) {
            turnosConsecutivosSeis++;
        } else {
            turnosConsecutivosSeis = 0;
        }

        if (turnosConsecutivosSeis == 3) {
            if (ultimaFichaMovida != null && !ultimaFichaMovida.estaEnMeta()) {
                int posOriginal = ultimaFichaMovida.getPosicion();
                if (posOriginal > 0 && posOriginal <= totalCasillas) {
                    casillas.get(posOriginal - 1).retirarFicha(ultimaFichaMovida);
                }
                ultimaFichaMovida.regresarASalida();
            }
            cambiarTurno();
            return new ResultadoLanzarDadoDTO(jugador.getId(), resultadoDado, false, false, false);
        }

        boolean puedeVolverATirar = (resultadoDado == 6);
        boolean puedeSacarFicha = (resultadoDado == numeroParaSacarFicha) && jugador.tieneFichasEnSalida();
        boolean tieneFichasEnJuego = jugador.tieneFichaEnJuego();

        boolean tieneMovimientoValido = tieneFichasEnJuego || puedeSacarFicha;
        if (!tieneMovimientoValido) {
            cambiarTurno();
        }

        return new ResultadoLanzarDadoDTO(
                jugador.getId(),
                resultadoDado,
                puedeVolverATirar,
                puedeSacarFicha,
                tieneMovimientoValido
        );
    }

    public ResultadoSeleccionarFichaDTO seleccionarFicha(SeleccionarFichaDTO dto) {
        Jugador jugador = obtenerJugador(dto.getIdJugador());
        validarTurno(jugador);

        if (!dadoLanzadoEnTurno) {
            throw new IllegalStateException("Debes lanzar el dado antes de seleccionar una ficha.");
        }

        Ficha ficha = jugador.obtenerFicha(dto.getIdFicha());
        if (ficha == null) {
            throw new IllegalArgumentException("La ficha no pertenece al jugador.");
        }

        boolean capturo = false;
        boolean llegoAMeta = false;

        int casillaSalidaJugador = obtenerCasillaSalidaJugador(jugador);

        if (ficha.estaEnSalida()) {
            if (resultadoDado != numeroParaSacarFicha) {
                throw new IllegalStateException("Se necesita obtener " + numeroParaSacarFicha + " para sacar la ficha.");
            }
            ficha.sacarAlTablero(casillaSalidaJugador);
            casillas.get(casillaSalidaJugador - 1).agregarFicha(ficha);
        } else {
            int posOrigen = ficha.getPosicion();
            if (posOrigen > 0 && posOrigen <= totalCasillas) {
                casillas.get(posOrigen - 1).retirarFicha(ficha);
            }

            int limiteMetaJugador = 76;
            llegoAMeta = ficha.avanzarORebotar(resultadoDado, limiteMetaJugador);

            int posDestino = ficha.getPosicion();
            if (posDestino > 0 && posDestino <= totalCasillas) {
                Casilla casillaDestino = casillas.get(posDestino - 1);

                if (casillaDestino.estaOcupada() && casillaDestino.tieneFichaRival(jugador)) {
                    if (!casillaDestino.estaSegura()) {
                        Ficha fichaRival = casillaDestino.obtenerFichaRival(jugador);
                        casillaDestino.retirarFicha(fichaRival);
                        fichaRival.regresarASalida();
                        capturo = true;
                        ficha.avanzarORebotar(20, limiteMetaJugador);
                    }
                }
                casillaDestino.agregarFicha(ficha);
            }
        }

        ultimaFichaMovida = ficha;

        if (llegoAMeta && jugador.tieneFichaEnJuego()) {
            for (Ficha otraFicha : jugador.getFichas()) {
                if (otraFicha.estaEnJuego() && otraFicha != ficha) {
                    otraFicha.avanzarORebotar(10, 76);
                    break;
                }
            }
        }

        boolean cambioTurno = !puedeVolverATirar();
        if (cambioTurno) {
            cambiarTurno();
        } else {
            dadoLanzadoEnTurno = false;
        }

        return new ResultadoSeleccionarFichaDTO(
                jugador.getId(),
                ficha.getId(),
                ficha.getPosicion(),
                capturo,
                llegoAMeta,
                cambioTurno
        );
    }

    private void cambiarTurno() {
        jugadorTurno = (jugadorTurno + 1) % jugadores.size();
        turnosConsecutivosSeis = 0;
        dadoLanzadoEnTurno = false;
    }

    private int obtenerCasillaSalidaJugador(Jugador jugador) {
        int indice = jugadores.indexOf(jugador);
        return (indice * (totalCasillas / jugadores.size())) + 1;
    }

    private boolean puedeVolverATirar() {
        return resultadoDado == 6 && turnosConsecutivosSeis < 3;
    }

    private Jugador obtenerJugador(int idJugador) {
        for (Jugador jugador : jugadores) {
            if (jugador.getId() == idJugador) {
                return jugador;
            }
        }
        throw new IllegalArgumentException("El jugador no existe.");
    }

    private void validarTurno(Jugador jugador) {
        if (jugadores.isEmpty() || jugadores.get(jugadorTurno) != jugador) {
            throw new IllegalStateException("No es el turno de este jugador.");
        }
    }
}