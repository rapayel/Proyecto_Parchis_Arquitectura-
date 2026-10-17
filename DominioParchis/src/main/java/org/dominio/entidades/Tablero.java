/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.entidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Las casillas del recorrido común se numeran del 1 al 68 como en el tablero de
 * referencia y las fichas avanzan en sentido antihorario (después del 68 sigue el 1).
 * Cada jugador ocupa un ala del tablero (0 = superior, 1 = derecha, 2 = inferior,
 * 3 = izquierda) que define su casilla de salida y la casilla por la que entra a su
 * pasillo final (69..75) para llegar a la meta (76).
 *
 * @author Equipo 1
 */
public class Tablero {
    private static final int[] CASILLAS_SEGURAS_ALA = {39, 29, 34, 42, 26};

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
    private final int casillasPorAla = 17;
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
            casillas.add(new Casilla(i, esCasillaSegura(i)));
        }
    }

    /**
     * Lleva la casilla a su equivalente en el ala superior (26..42) y la compara con
     * las primeras casillas seguras configuradas: salida, frente a la salida, entrada
     * al pasillo y las dos junto al centro.
     */
    private boolean esCasillaSegura(int numeroCasilla) {
        int enAlaSuperior = numeroCasilla;
        while (enAlaSuperior < 26) {
            enAlaSuperior += casillasPorAla;
        }
        while (enAlaSuperior > 42) {
            enAlaSuperior -= casillasPorAla;
        }
        for (int i = 0; i < casillasSegurasPorAla; i++) {
            if (CASILLAS_SEGURAS_ALA[i] == enAlaSuperior) {
                return true;
            }
        }
        return false;
    }

    private void agregarJugador(Jugador jugador) {
        int ala = this.jugadores.size();
        int casillaSalida = Math.floorMod(38 - casillasPorAla * ala, totalCasillas) + 1;
        int casillaEntradaPasillo = Math.floorMod(33 - casillasPorAla * ala, totalCasillas) + 1;
        jugador.asignarRecorrido(casillaSalida, casillaEntradaPasillo);
        this.jugadores.add(jugador);
    }

    public Jugador getJugadorTurnoActual() {
        if (jugadores.isEmpty()) return null;
        return jugadores.get(jugadorTurno);
    }

    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
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
            if (ultimaFichaMovida != null && ultimaFichaMovida.getJugador() == jugador && ultimaFichaMovida.puedeMoverse()) {
                regresarFichaASalida(ultimaFichaMovida);
            }
            cambiarTurno();
            return new ResultadoLanzarDadoDominio(jugador.getId(), resultadoDado, false, false, false);
        }

        boolean puedeVolverATirar = (resultadoDado == 6);
        boolean puedeSacarFicha = (resultadoDado == numeroParaSacarFicha)
                && jugador.tieneFichasEnSalida()
                && !obtenerCasilla(jugador.getCasillaSalida()).estaLlena();
        boolean tieneMovimientoValido = puedeSacarFicha || tieneFichaQuePuedaAvanzar(jugador, resultadoDado);

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

        if (ficha.estaEnSalida()) {
            if (resultadoDado != numeroParaSacarFicha) {
                throw new IllegalStateException("Se necesita obtener " + numeroParaSacarFicha + " para sacar la ficha.");
            }
            sacarFicha(ficha, jugador);
        } else if (ficha.estaEnMeta()) {
            throw new IllegalStateException("La ficha ya llegó a la meta.");
        } else {
            capturo = moverFichaEnTablero(ficha, resultadoDado, jugador);
            llegoAMeta = ficha.estaEnMeta();
            otorgarRecompensas(ficha, capturo, llegoAMeta, jugador);
        }

        ultimaFichaMovida = ficha;

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

    private void sacarFicha(Ficha ficha, Jugador jugador) {
        Casilla casillaSalida = obtenerCasilla(jugador.getCasillaSalida());
        if (casillaSalida.estaLlena()) {
            throw new IllegalStateException("La casilla de salida (" + jugador.getCasillaSalida() + ") está llena.");
        }
        ficha.sacarAlTablero(jugador.getCasillaSalida());
        casillaSalida.agregarFicha(ficha);
    }

    /**
     * Mueve la ficha a la casilla destino de su recorrido. Primero valida todo el
     * recorrido para no dejar el tablero a medias si el movimiento no es posible.
     *
     * @return true si la ficha capturó a una ficha rival al caer en la casilla destino
     */
    private boolean moverFichaEnTablero(Ficha ficha, int pasos, Jugador jugador) {
        int destino = calcularDestino(ficha, pasos);
        if (destino == -1) {
            throw new IllegalStateException("La ficha " + ficha.getId() + " no puede avanzar " + pasos
                    + " casillas: hay una barrera en el camino o la casilla destino está llena.");
        }

        int origen = ficha.getPosicion();
        if (esCasillaComun(origen)) {
            obtenerCasilla(origen).retirarFicha(ficha);
        }
        ficha.moverA(destino, estadoSegunPosicion(destino));

        if (!esCasillaComun(destino)) {
            return false;
        }

        Casilla casillaDestino = obtenerCasilla(destino);
        boolean capturo = false;
        if (!casillaDestino.estaSegura() && casillaDestino.tieneFichaRival(jugador)) {
            Ficha fichaRival = casillaDestino.obtenerFichaRival(jugador);
            regresarFichaASalida(fichaRival);
            capturo = true;
        }
        casillaDestino.agregarFicha(ficha);
        return capturo;
    }

    /**
     * Recorre casilla por casilla el camino de la ficha sin moverla. Al pasar por la
     * casilla de entrada de su jugador entra al pasillo final; si se excede la meta,
     * rebota hacia atrás dentro del pasillo.
     *
     * @return la posición destino o -1 si el movimiento no es posible
     */
    private int calcularDestino(Ficha ficha, int pasos) {
        Jugador jugador = ficha.getJugador();
        int posicion = ficha.getPosicion();
        boolean rebotando = false;

        for (int paso = 1; paso <= pasos; paso++) {
            if (posicion == limiteMetaJugador) {
                rebotando = true;
            }

            if (rebotando) {
                posicion--;
                if (posicion <= totalCasillas) {
                    return -1;
                }
            } else if (posicion == jugador.getCasillaEntradaPasillo()) {
                posicion = totalCasillas + 1;
            } else if (posicion > totalCasillas) {
                posicion++;
            } else {
                posicion = posicion % totalCasillas + 1;
            }

            if (hayBarrera(posicion, ficha)) {
                return -1;
            }
        }

        if (esCasillaComun(posicion) && obtenerCasilla(posicion).estaLlena()) {
            return -1;
        }
        return posicion;
    }

    private boolean hayBarrera(int posicion, Ficha fichaQueAvanza) {
        if (esCasillaComun(posicion)) {
            return obtenerCasilla(posicion).formaBarrera();
        }
        if (posicion == limiteMetaJugador) {
            return false;
        }
        int fichasEnCasilla = 0;
        for (Ficha ficha : fichaQueAvanza.getJugador().getFichas()) {
            if (ficha != fichaQueAvanza && ficha.estaEnPasilloFinal() && ficha.getPosicion() == posicion) {
                fichasEnCasilla++;
            }
        }
        return fichasEnCasilla >= 2;
    }

    private boolean puedeAvanzar(Ficha ficha, int pasos) {
        return ficha.puedeMoverse() && calcularDestino(ficha, pasos) != -1;
    }

    private boolean tieneFichaQuePuedaAvanzar(Jugador jugador, int pasos) {
        for (Ficha ficha : jugador.getFichas()) {
            if (puedeAvanzar(ficha, pasos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Al capturar, la ficha que capturó avanza 20 casillas; al llegar a la meta, otra
     * ficha del jugador avanza 10. Si la ficha no puede avanzar, la recompensa se pierde.
     */
    private void otorgarRecompensas(Ficha ficha, boolean capturo, boolean llegoAMeta, Jugador jugador) {
        if (capturo && puedeAvanzar(ficha, 20)) {
            boolean capturoOtra = moverFichaEnTablero(ficha, 20, jugador);
            otorgarRecompensas(ficha, capturoOtra, ficha.estaEnMeta(), jugador);
        }
        if (llegoAMeta) {
            for (Ficha otraFicha : jugador.getFichas()) {
                if (otraFicha != ficha && puedeAvanzar(otraFicha, 10)) {
                    boolean capturoOtra = moverFichaEnTablero(otraFicha, 10, jugador);
                    otorgarRecompensas(otraFicha, capturoOtra, otraFicha.estaEnMeta(), jugador);
                    break;
                }
            }
        }
    }

    private void regresarFichaASalida(Ficha ficha) {
        if (esCasillaComun(ficha.getPosicion())) {
            obtenerCasilla(ficha.getPosicion()).retirarFicha(ficha);
        }
        ficha.regresarASalida();
    }

    private Ficha.Estado estadoSegunPosicion(int posicion) {
        if (posicion <= totalCasillas) {
            return Ficha.Estado.EN_JUEGO;
        }
        if (posicion < limiteMetaJugador) {
            return Ficha.Estado.PASILLO_FINAL;
        }
        return Ficha.Estado.META;
    }

    private boolean esCasillaComun(int posicion) {
        return posicion >= 1 && posicion <= totalCasillas;
    }

    private Casilla obtenerCasilla(int posicion) {
        return casillas.get(posicion - 1);
    }

    private void cambiarTurno() {
        jugadorTurno = (jugadorTurno + 1) % jugadores.size();
        turnosConsecutivosSeis = 0;
        dadoLanzadoEnTurno = false;
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
