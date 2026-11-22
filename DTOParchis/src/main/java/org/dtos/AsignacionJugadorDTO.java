package org.dtos;

public class AsignacionJugadorDTO implements IDTO {
    private static final long serialVersionUID = 1L;

    private final int idJugador;
    private final int numeroJugadores;
    private final boolean partidaIniciada;
    private final int fichasPorJugador;
    private final int casillasSegurasPorAla;
    private final int numeroSalidaDado;

    public AsignacionJugadorDTO(int idJugador, int numeroJugadores, boolean partidaIniciada) {
        this(idJugador, numeroJugadores, partidaIniciada, 4, 1, 5);
    }

    public AsignacionJugadorDTO(
            int idJugador,
            int numeroJugadores,
            boolean partidaIniciada,
            int fichasPorJugador,
            int casillasSegurasPorAla,
            int numeroSalidaDado) {
        this.idJugador = idJugador;
        this.numeroJugadores = numeroJugadores;
        this.partidaIniciada = partidaIniciada;
        this.fichasPorJugador = fichasPorJugador;
        this.casillasSegurasPorAla = casillasSegurasPorAla;
        this.numeroSalidaDado = numeroSalidaDado;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public boolean isPartidaIniciada() {
        return partidaIniciada;
    }

    public int getFichasPorJugador() {
        return fichasPorJugador;
    }

    public int getCasillasSegurasPorAla() {
        return casillasSegurasPorAla;
    }

    public int getNumeroSalidaDado() {
        return numeroSalidaDado;
    }
}
