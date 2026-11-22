package org.dtos;

public class PartidaCreadaDTO implements IDTO {
    private static final long serialVersionUID = 1L;

    private final int fichasPorJugador;
    private final int casillasSegurasPorAla;
    private final int numeroSalidaDado;
    private final int numeroJugadores;

    public PartidaCreadaDTO(
            int fichasPorJugador,
            int casillasSegurasPorAla,
            int numeroSalidaDado,
            int numeroJugadores) {
        this.fichasPorJugador = fichasPorJugador;
        this.casillasSegurasPorAla = casillasSegurasPorAla;
        this.numeroSalidaDado = numeroSalidaDado;
        this.numeroJugadores = numeroJugadores;
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

    public int getNumeroJugadores() {
        return numeroJugadores;
    }
}
