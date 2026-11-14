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
    private Jugador ganador;
    private final Random random;
    private final int numeroParaSacarFicha;
    private final int casillasSegurasPorAla;
    private final int totalCasillas = 68;
    private final int limiteMetaJugador = 76;

    public Tablero(List<Jugador> jugadores, int numeroParaSacarFicha, int casillasSegurasPorAla) {
        this(jugadores, numeroParaSacarFicha, casillasSegurasPorAla, new Random());
    }

    Tablero(List<Jugador> jugadores, int numeroParaSacarFicha, int casillasSegurasPorAla, Random random) {
        if (jugadores == null || jugadores.size() < 2 || jugadores.size() > 4) {
            throw new IllegalArgumentException("El tablero requiere entre 2 y 4 jugadores.");
        }
        if (random == null) {
            throw new IllegalArgumentException("El generador del dado no puede ser nulo.");
        }
        if (numeroParaSacarFicha < 1 || numeroParaSacarFicha > 6) {
            throw new IllegalArgumentException("El número para sacar ficha debe estar entre 1 y 6.");
        }
        if (casillasSegurasPorAla < 1 || casillasSegurasPorAla > 5) {
            throw new IllegalArgumentException("El número de casillas seguras debe estar entre 1 y 5.");
        }

        this.random = random;
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
            casillas.add(new Casilla(i, esCasillaSegura(i)));
        }
    }

    private boolean esCasillaSegura(int posicion) {
        int casillasPorJugador = totalCasillas / jugadores.size();
        for (int i = 0; i < jugadores.size(); i++) {
            if (posicion == i * casillasPorJugador + 1) {
                return true;
            }
        }

        for (int brazo = 0; brazo < 4; brazo++) {
            for (int fila = 8 - casillasSegurasPorAla; fila < 8; fila++) {
                int base = 35 + fila;
                int casillaSegura = Math.floorMod(39 - base + 17 * brazo, totalCasillas) + 1;
                if (posicion == casillaSegura) {
                    return true;
                }
            }
        }
        return false;
    }

    private void agregarJugador(Jugador jugador) {
        this.jugadores.add(jugador);
    }

    public Jugador getJugadorTurnoActual() {
        if (jugadores.isEmpty()) return null;
        return jugadores.get(jugadorTurno);
    }

    public ResultadoLanzarDadoDominio lanzarDado(int idJugador) {
        validarPartidaActiva();
        Jugador jugador = obtenerJugador(idJugador);
        validarTurno(jugador);

        if (dadoLanzadoEnTurno) {
            throw new IllegalStateException("Ya lanzaste el dado en este turno.");
        }

        resultadoDado = random.nextInt(6) + 1;
        dadoLanzadoEnTurno = true;

        if (resultadoDado == 6) {
            turnosConsecutivosSeis++;
        } else {
            turnosConsecutivosSeis = 0;
        }

        int idFichaRegresada = -1;
        if (turnosConsecutivosSeis == 3) {
            if (ultimaFichaMovida != null && !ultimaFichaMovida.estaEnMeta()) {
                idFichaRegresada = ultimaFichaMovida.getId();
                int posOriginal = ultimaFichaMovida.getPosicion();
                if (posOriginal > 0 && posOriginal <= totalCasillas) {
                    casillas.get(posOriginal - 1).retirarFicha(ultimaFichaMovida);
                }
                ultimaFichaMovida.regresarASalida();
            }
            cambiarTurno();
            return new ResultadoLanzarDadoDominio(
                    jugador.getId(), resultadoDado, false, false, false, idFichaRegresada);
        }

        boolean puedeSacarFicha = resultadoDado == numeroParaSacarFicha
                && jugador.tieneFichasEnSalida()
                && puedeAterrizarEnSalida(jugador);
        boolean tieneMovimientoValido = puedeSacarFicha;
        if (!tieneMovimientoValido) {
            for (Ficha ficha : jugador.getFichas()) {
                if (ficha.estaEnJuego() && puedeMover(ficha, resultadoDado, jugador, true)) {
                    tieneMovimientoValido = true;
                    break;
                }
            }
        }
        boolean puedeVolverATirar = resultadoDado == 6
                && turnosConsecutivosSeis < 3
                && tieneMovimientoValido;
        if (!tieneMovimientoValido) {
            cambiarTurno();
        }

        return new ResultadoLanzarDadoDominio(
                jugador.getId(),
                resultadoDado,
                puedeVolverATirar,
                puedeSacarFicha,
                tieneMovimientoValido,
                -1
        );
    }

    public ResultadoSeleccionarFichaDominio seleccionarFicha(int idJugador, int idFicha) {
        validarPartidaActiva();
        Jugador jugador = obtenerJugador(idJugador);
        validarTurno(jugador);

        if (!dadoLanzadoEnTurno) {
            throw new IllegalStateException("Debes lanzar el dado antes de seleccionar una ficha.");
        }

        Ficha ficha = jugador.obtenerFicha(idFicha);
        if (ficha == null) {
            throw new IllegalArgumentException("La ficha no pertenece al jugador.");
        }

        Ficha fichaCapturada = null;
        boolean llegoAMeta = false;

        int casillaSalidaJugador = obtenerCasillaSalidaJugador(jugador);

        if (ficha.estaEnSalida()) {
            if (resultadoDado != numeroParaSacarFicha) {
                throw new IllegalStateException("Se necesita obtener " + numeroParaSacarFicha + " para sacar la ficha.");
            }
            Casilla casillaSalida = casillas.get(casillaSalidaJugador - 1);
            if (!puedeAterrizar(casillaSalida, jugador, true)) {
                throw new IllegalStateException("La casilla de salida no admite otra ficha.");
            }
            fichaCapturada = capturarRival(casillaSalida, jugador);
            ficha.sacarAlTablero(casillaSalidaJugador);
            casillaSalida.agregarFicha(ficha);
        } else {
            if (!ficha.estaEnJuego() || !puedeMover(ficha, resultadoDado, jugador, true)) {
                throw new IllegalStateException("La ficha no puede realizar este movimiento.");
            }
            fichaCapturada = moverFichaEnTablero(ficha, resultadoDado, jugador, true);
            
            if (ficha.estaEnMeta()) {
                llegoAMeta = true;
            } else if (fichaCapturada != null && puedeMover(ficha, 20, jugador, false)) {
                moverFichaEnTablero(ficha, 20, jugador, false);
            }
        }

        ultimaFichaMovida = ficha;

        if (llegoAMeta && jugador.tieneFichaEnJuego()) {
            Ficha otraFicha = jugador.obtenerFichaEnJuegoDistintaDe(ficha);
            if (otraFicha != null && puedeMover(otraFicha, 10, jugador, false)) {
                moverFichaEnTablero(otraFicha, 10, jugador, false);
            }
        }

        boolean esGanador = jugador.todasLlegaronAMeta();
        boolean capturo = fichaCapturada != null;

        boolean cambioTurno = !puedeVolverATirar() || esGanador;
        if (esGanador) {
            ganador = jugador;
            dadoLanzadoEnTurno = false;
            turnosConsecutivosSeis = 0;
        } else if (cambioTurno) {
            cambiarTurno();
        } else {
            dadoLanzadoEnTurno = false;
        }

        return new ResultadoSeleccionarFichaDominio(
                jugador.getId(),
                ficha.getId(),
                ficha.getPosicion(),
                capturo,
                capturo ? fichaCapturada.getJugador().getId() : -1,
                capturo ? fichaCapturada.getId() : -1,
                llegoAMeta,
                cambioTurno,
                esGanador
        );
    }

    private void validarPartidaActiva() {
        if (ganador != null) {
            throw new IllegalStateException("La partida ya terminó.");
        }
    }

    private Ficha moverFichaEnTablero(Ficha ficha, int pasos, Jugador jugador, boolean puedeCapturar) {
        if (!puedeMover(ficha, pasos, jugador, puedeCapturar)) {
            throw new IllegalStateException("La ficha no puede realizar este movimiento.");
        }

        int posOrigen = ficha.getPosicion();
        int posDestino = ficha.calcularPosicionDespuesDeAvanzar(pasos, limiteMetaJugador);
        Casilla casillaDestino = posDestino > 0 && posDestino <= totalCasillas
                ? casillas.get(posDestino - 1)
                : null;
        Ficha fichaCapturada = puedeCapturar && casillaDestino != null
                ? capturarRival(casillaDestino, jugador)
                : null;

        if (posOrigen > 0 && posOrigen <= totalCasillas) {
            casillas.get(posOrigen - 1).retirarFicha(ficha);
        }

        ficha.avanzarORebotar(pasos, limiteMetaJugador);

        if (casillaDestino != null) {
            casillaDestino.agregarFicha(ficha);
        }
        return fichaCapturada;
    }

    private boolean puedeMover(Ficha ficha, int pasos, Jugador jugador, boolean puedeCapturar) {
        if (!ficha.estaEnJuego()) {
            return false;
        }

        int posDestino = ficha.calcularPosicionDespuesDeAvanzar(pasos, limiteMetaJugador);
        if (posDestino > totalCasillas) {
            return true;
        }
        return puedeAterrizar(casillas.get(posDestino - 1), jugador, puedeCapturar);
    }

    private boolean puedeAterrizarEnSalida(Jugador jugador) {
        int casillaSalida = obtenerCasillaSalidaJugador(jugador);
        return puedeAterrizar(casillas.get(casillaSalida - 1), jugador, true);
    }

    private boolean puedeAterrizar(Casilla casilla, Jugador jugador, boolean puedeCapturar) {
        if (casilla.formaBarrera()) {
            return false;
        }
        if (!casilla.estaSegura() && casilla.tieneFichaRival(jugador)) {
            return puedeCapturar;
        }
        return casilla.tieneEspacio();
    }

    private Ficha capturarRival(Casilla casilla, Jugador jugador) {
        if (casilla.estaSegura()) {
            return null;
        }
        Ficha fichaRival = casilla.obtenerFichaRival(jugador);
        if (fichaRival == null) {
            return null;
        }
        casilla.retirarFicha(fichaRival);
        fichaRival.regresarASalida();
        return fichaRival;
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
            int idJugador, int resultadoDado, boolean puedeVolverATirar, boolean puedeSacarFicha,
            boolean tieneMovimientoValido, int idFichaRegresada) {}

    public static record ResultadoSeleccionarFichaDominio(
            int idJugador, int idFicha, int posicionFicha, boolean capturo, int idJugadorFichaCapturada,
            int idFichaCapturada, boolean llegoAMeta, boolean cambioTurno, boolean esGanador) {}
}