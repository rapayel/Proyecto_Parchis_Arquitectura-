package org.dtos;

public class AsignacionJugadorDTO implements IDTO {
    private static final long serialVersionUID = 1L;

    private final int idJugador;
    private final int numeroJugadores;
    private final boolean partidaIniciada;

    public AsignacionJugadorDTO(int idJugador, int numeroJugadores, boolean partidaIniciada) {
        this.idJugador = idJugador;
        this.numeroJugadores = numeroJugadores;
        this.partidaIniciada = partidaIniciada;
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
}
