package org.presentacion.mvc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;
import org.dtos.AsignacionJugadorDTO;
import org.dtos.ErrorJuegoDTO;
import org.dtos.IDTO;
import org.dtos.ResultadoLanzarDadoDTO;
import org.dtos.ResultadoSeleccionarFichaDTO;
import org.presentacion.interfaces.IModeloLectura;
import org.presentacion.interfaces.Observador;

public class ModeloJuego implements IModeloLectura {
    private final List<Observador> observadores = new CopyOnWriteArrayList<>();
    private int numeroJugadores;
    private int idJugadorLocal = -1;
    private int idJugadorTurnoActual = 1;
    private int idJugadorUltimaJugada = -1;
    private int idJugadorGanador = -1;
    private int idFichaSeleccionada = -1;
    private int posicionFicha = -1;
    private int idFichaRegresada = -1;
    private int idJugadorFichaCapturada = -1;
    private int idFichaCapturada = -1;
    private int valorDado;
    private boolean puedeVolverATirar;
    private boolean puedeSacarFicha;
    private boolean tieneMovimientoValido;
    private boolean capturo;
    private boolean llegoAMeta;
    private boolean cambioTurno;
    private boolean juegoTerminado;
    private boolean partidaIniciada;
    private boolean conexionActiva = true;
    private boolean dadoLanzadoEnTurno;
    private boolean solicitudPendiente;
    private String mensajeEstado = "Conectando con el servidor...";
    private String mensajeError = "";
    private long versionMensajeError;

    public synchronized void recibirDTO(IDTO dto) {
        if (dto instanceof AsignacionJugadorDTO asignacion) {
            aplicarAsignacion(asignacion);
        } else if (dto instanceof ErrorJuegoDTO error) {
            registrarErrorJuego(error.getMensaje());
        } else if (dto instanceof ResultadoLanzarDadoDTO resultado) {
            aplicarResultadoDado(resultado);
        } else if (dto instanceof ResultadoSeleccionarFichaDTO resultado) {
            aplicarResultadoSeleccion(resultado);
        } else {
            throw new IllegalArgumentException("Tipo de respuesta no reconocido.");
        }
        notificarEnEdt();
    }

    private void aplicarAsignacion(AsignacionJugadorDTO asignacion) {
        if (asignacion.getNumeroJugadores() < 2 || asignacion.getNumeroJugadores() > 4
                || asignacion.getIdJugador() < 1
                || asignacion.getIdJugador() > asignacion.getNumeroJugadores()) {
            throw new IllegalArgumentException("El servidor envió una asignación de jugador inválida.");
        }
        if (idJugadorLocal != -1 && idJugadorLocal != asignacion.getIdJugador()) {
            throw new IllegalStateException("El servidor cambió el jugador asignado a esta conexión.");
        }

        numeroJugadores = asignacion.getNumeroJugadores();
        idJugadorLocal = asignacion.getIdJugador();
        partidaIniciada = asignacion.isPartidaIniciada();
        mensajeEstado = partidaIniciada
                ? "Partida iniciada"
                : "Esperando a que se conecten los demás jugadores...";
    }

    private void aplicarResultadoDado(ResultadoLanzarDadoDTO resultado) {
        validarJugadorEnPartida(resultado.getIdJugador());
        idJugadorUltimaJugada = resultado.getIdJugador();
        idFichaSeleccionada = -1;
        idFichaRegresada = resultado.getIdFichaRegresada();
        idJugadorFichaCapturada = -1;
        idFichaCapturada = -1;
        capturo = false;
        llegoAMeta = false;
        cambioTurno = false;
        valorDado = resultado.getResultadoDado();
        puedeVolverATirar = resultado.isPuedeVolverATirar();
        puedeSacarFicha = resultado.isPuedeSacarFicha();
        tieneMovimientoValido = resultado.isTieneMovimientoValido();
        solicitudPendiente = false;

        if (tieneMovimientoValido) {
            idJugadorTurnoActual = resultado.getIdJugador();
            dadoLanzadoEnTurno = true;
        } else {
            idJugadorTurnoActual = siguienteJugador(resultado.getIdJugador());
            dadoLanzadoEnTurno = false;
        }
    }

    private void aplicarResultadoSeleccion(ResultadoSeleccionarFichaDTO resultado) {
        validarJugadorEnPartida(resultado.getIdJugador());
        idJugadorUltimaJugada = resultado.getIdJugador();
        idFichaSeleccionada = resultado.getIdFicha();
        posicionFicha = resultado.getPosicionFicha();
        idFichaRegresada = -1;
        capturo = resultado.isCapturo();
        idJugadorFichaCapturada = resultado.getIdJugadorFichaCapturada();
        idFichaCapturada = resultado.getIdFichaCapturada();
        llegoAMeta = resultado.isLlegoAMeta();
        cambioTurno = resultado.isCambioTurno();
        juegoTerminado = resultado.isGanador();
        solicitudPendiente = false;
        dadoLanzadoEnTurno = false;

        if (juegoTerminado) {
            idJugadorGanador = resultado.getIdJugador();
            mensajeEstado = "Ganó el jugador " + idJugadorGanador;
        } else if (cambioTurno) {
            idJugadorTurnoActual = siguienteJugador(resultado.getIdJugador());
        } else {
            idJugadorTurnoActual = resultado.getIdJugador();
        }
    }

    private void validarJugadorEnPartida(int idJugador) {
        if (!partidaIniciada || idJugador < 1 || idJugador > numeroJugadores) {
            throw new IllegalStateException("Se recibió una jugada antes de iniciar la partida o de un jugador inválido.");
        }
    }

    private int siguienteJugador(int idJugador) {
        return idJugador % numeroJugadores + 1;
    }

    public synchronized void prepararLanzamiento() {
        validarConexionYPartida();
        if (idJugadorTurnoActual != idJugadorLocal || dadoLanzadoEnTurno || solicitudPendiente) {
            throw new IllegalStateException("No puedes lanzar el dado en este momento.");
        }
        idFichaSeleccionada = -1;
        solicitudPendiente = true;
        notificarEnEdt();
    }

    public synchronized void prepararSeleccion(int idFicha) {
        validarConexionYPartida();
        if (idJugadorTurnoActual != idJugadorLocal || !dadoLanzadoEnTurno || solicitudPendiente) {
            throw new IllegalStateException("No puedes seleccionar una ficha en este momento.");
        }
        if (idFicha < idJugadorLocal * 10 + 1 || idFicha > idJugadorLocal * 10 + 4) {
            throw new IllegalArgumentException("La ficha seleccionada no pertenece a este jugador.");
        }
        idFichaSeleccionada = -1;
        solicitudPendiente = true;
        notificarEnEdt();
    }

    private void validarConexionYPartida() {
        if (!conexionActiva) {
            throw new IllegalStateException("No hay conexión activa con el servidor.");
        }
        if (!partidaIniciada) {
            throw new IllegalStateException(mensajeEstado);
        }
        if (juegoTerminado) {
            throw new IllegalStateException("La partida ya terminó.");
        }
    }

    public synchronized void cancelarSolicitud(String mensaje) {
        solicitudPendiente = false;
        idFichaSeleccionada = -1;
        idFichaRegresada = -1;
        idFichaCapturada = -1;
        mensajeError = mensaje;
        versionMensajeError++;
        notificarEnEdt();
    }

    private void registrarErrorJuego(String mensaje) {
        solicitudPendiente = false;
        idFichaSeleccionada = -1;
        idFichaRegresada = -1;
        idFichaCapturada = -1;
        mensajeError = mensaje == null || mensaje.isBlank()
                ? "El servidor rechazó la solicitud."
                : mensaje;
        versionMensajeError++;
    }

    public synchronized void reportarErrorConexion(String mensaje) {
        conexionActiva = false;
        partidaIniciada = false;
        solicitudPendiente = false;
        mensajeError = mensaje;
        mensajeEstado = "Conexión con el servidor interrumpida";
        versionMensajeError++;
        notificarEnEdt();
    }

    private void notificarEnEdt() {
        Runnable notificar = () -> {
            for (Observador observador : observadores) {
                observador.update(this);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            notificar.run();
        } else {
            SwingUtilities.invokeLater(notificar);
        }
    }

    @Override
    public void registrarObservador(Observador observador) {
        if (observador != null) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(Observador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores() {
        notificarEnEdt();
    }

    @Override
    public synchronized int getValorDado() { return valorDado; }
    @Override
    public synchronized boolean puedeVolverATirar() { return puedeVolverATirar; }
    @Override
    public synchronized boolean puedeSacarFicha() { return puedeSacarFicha; }
    @Override
    public synchronized boolean tieneMovimientoValido() { return tieneMovimientoValido; }
    @Override
    public synchronized int getIdFichaSeleccionada() { return idFichaSeleccionada; }
    @Override
    public synchronized int getPosicionFicha() { return posicionFicha; }
    @Override
    public synchronized boolean isCapturo() { return capturo; }
    @Override
    public synchronized boolean isLlegoAMeta() { return llegoAMeta; }
    @Override
    public synchronized boolean isCambioTurno() { return cambioTurno; }
    @Override
    public synchronized int getIdJugadorTurnoActual() { return idJugadorTurnoActual; }
    @Override
    public synchronized boolean isDadoLanzadoEnTurno() { return dadoLanzadoEnTurno; }
    @Override
    public synchronized int getIdJugadorUltimaJugada() { return idJugadorUltimaJugada; }
    @Override
    public synchronized int getIdFichaRegresada() { return idFichaRegresada; }
    @Override
    public synchronized int getIdJugadorFichaCapturada() { return idJugadorFichaCapturada; }
    @Override
    public synchronized int getIdFichaCapturada() { return idFichaCapturada; }
    @Override
    public synchronized boolean isJuegoTerminado() { return juegoTerminado; }
    @Override
    public synchronized int getIdJugadorGanador() { return idJugadorGanador; }
    @Override
    public synchronized int getIdJugadorLocal() { return idJugadorLocal; }
    @Override
    public synchronized int getNumeroJugadores() { return numeroJugadores; }
    @Override
    public synchronized boolean isPartidaIniciada() { return partidaIniciada; }
    @Override
    public synchronized boolean isConexionActiva() { return conexionActiva; }
    @Override
    public synchronized boolean isSolicitudPendiente() { return solicitudPendiente; }
    @Override
    public synchronized String getMensajeEstado() { return mensajeEstado; }
    @Override
    public synchronized String getMensajeError() { return mensajeError; }
    @Override
    public synchronized long getVersionMensajeError() { return versionMensajeError; }
}
