package org.presentacion.interfaces;

public interface IInterpretadorCliente {
    void solicitarLanzarDado();
    void solicitarSeleccionarFicha(int idFicha);
    void solicitarCrearPartida(int fichasPorJugador, int casillasSegurasPorAla, int numeroSalidaDado);
}
