/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.entidades;

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
    private final int limiteMetaJugador = 76;

    public Tablero(List<Jugador> jugadores, int numeroParaSacarFicha, int casillasSegurasPorAla) {
        if (jugadores == null || jugadores.size() < 2 || jugadores.size() > 4) {
            throw new IllegalArgumentException("El tablero requiere entre 2 y 4 jugadores.");
        }
        if (numeroParaSacarFicha < 1 || numeroParaSacarFicha > 6) {
            throw new IllegalArgumentException("El número para sacar ficha debe estar entre 1 y 6.");
        }
        if (casillasSegurasPorAla < 1 || casillasSegurasPorAla > 5) {
            throw new IllegalArgumentException("El número de casillas seguras debe estar entre 1 y 5.");
        }

        this.numeroParaSacarFicha = numeroParaSacarFicha;
        this.casillasSegurasPorAla = casillasSegurasPorAla;

        this.jugadores = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            agregarJugador(jugador);
        }

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

    private void agregarJugador(Jugador jugador) {
        this.jugadores.add(jugador);
    }

    public Jugador getJugadorTurnoActual() {
        if (jugadores.isEmpty()) return null;
        return jugadores.get(jugadorTurno);
    }

    public ResultadoLanzarDadoDominio lanzarDado(int idJugador) {
        Jugador jugador = obtenerJugador(idJugador);
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
            return new ResultadoLanzarDadoDominio(jugador.getId(), resultadoDado, false, false, false);
        }

        boolean puedeVolverATirar = (resultadoDado == 6);
        boolean puedeSacarFicha = (resultadoDado == numeroParaSacarFicha) && jugador.tieneFichasEnSalida();
        boolean tieneFichasEnJuego = jugador.tieneFichaEnJuego();

        boolean tieneMovimientoValido = tieneFichasEnJuego || puedeSacarFicha;
        if (!tieneMovimientoValido) {
            cambiarTurno();
        }

        return new ResultadoLanzarDadoDominio(
                jugador.getId(),
                resultadoDado,
                puedeVolverATirar,
                puedeSacarFicha,
                tieneMovimientoValido
        );
    }

    public ResultadoSeleccionarFichaDominio seleccionarFicha(int idJugador, int idFicha) {
        Jugador jugador = obtenerJugador(idJugador);
        validarTurno(jugador);

        if (!dadoLanzadoEnTurno) {
            throw new IllegalStateException("Debes lanzar el dado antes de seleccionar una ficha.");
        }

        Ficha ficha = jugador.obtenerFicha(idFicha);
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
            moverFichaEnTablero(ficha, resultadoDado, jugador);
            
            if (ficha.estaEnMeta()) {
                llegoAMeta = true;
            } else {
                int posDestino = ficha.getPosicion();
                if (posDestino > 0 && posDestino <= totalCasillas) {
                    Casilla casillaDestino = casillas.get(posDestino - 1);

                    if (casillaDestino.estaOcupada() && casillaDestino.tieneFichaRival(jugador)) {
                        if (!casillaDestino.estaSegura()) {
                            Ficha fichaRival = casillaDestino.obtenerFichaRival(jugador);
                            casillaDestino.retirarFicha(fichaRival);
                            fichaRival.regresarASalida();
                            capturo = true;
                            
                            moverFichaEnTablero(ficha, 20, jugador);
                        }
                    }
                }
            }
        }

        ultimaFichaMovida = ficha;

        if (llegoAMeta && jugador.tieneFichaEnJuego()) {
            Ficha otraFicha = jugador.obtenerFichaEnJuegoDistintaDe(ficha);
            if (otraFicha != null) {
                moverFichaEnTablero(otraFicha, 10, jugador);
            }
        }

        boolean esGanador = jugador.todasLlegaronAMeta();

        boolean cambioTurno = !puedeVolverATirar() || esGanador;
        if (cambioTurno) {
            cambiarTurno();
        } else {
            dadoLanzadoEnTurno = false;
        }

        return new ResultadoSeleccionarFichaDominio(
                jugador.getId(),
                ficha.getId(),
                ficha.getPosicion(),
                capturo,
                llegoAMeta,
                cambioTurno
        );
    }

    private void moverFichaEnTablero(Ficha ficha, int pasos, Jugador jugador) {
        int posOrigen = ficha.getPosicion();
        if (posOrigen > 0 && posOrigen <= totalCasillas) {
            casillas.get(posOrigen - 1).retirarFicha(ficha);
        }

        ficha.avanzarORebotar(pasos, limiteMetaJugador);

        int posDestino = ficha.getPosicion();
        if (posDestino > 0 && posDestino <= totalCasillas) {
            Casilla casillaDestino = casillas.get(posDestino - 1);
            if (casillaDestino.formaBarrera()) {
                // Revertir posición si hay barrera
                ficha.setPosicion(posOrigen);
                if (posOrigen > 0 && posOrigen <= totalCasillas) {
                    casillas.get(posOrigen - 1).agregarFicha(ficha);
                }
                throw new IllegalStateException("La casilla destino (" + posDestino + ") tiene una barrera.");
            }
            casillaDestino.agregarFicha(ficha);
        }
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

    public static record ResultadoLanzarDadoDominio(
            int idJugador, int resultadoDado, boolean puedeVolverATirar, boolean puedeSacarFicha, boolean tieneMovimientoValido) {}

    public static record ResultadoSeleccionarFichaDominio(
            int idJugador, int idFicha, int posicionFicha, boolean capturo, boolean llegoAMeta, boolean cambioTurno) {}
}