package org.dtos;

public class CrearPartidaDTO implements IDTO {
    private static final long serialVersionUID = 1L;

    private final int fichasPorJugador;
    private final int casillasSegurasPorAla;
    private final int numeroSalidaDado;

    public CrearPartidaDTO(int fichasPorJugador, int casillasSegurasPorAla, int numeroSalidaDado) {
        this.fichasPorJugador = fichasPorJugador;
        this.casillasSegurasPorAla = casillasSegurasPorAla;
        this.numeroSalidaDado = numeroSalidaDado;
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
