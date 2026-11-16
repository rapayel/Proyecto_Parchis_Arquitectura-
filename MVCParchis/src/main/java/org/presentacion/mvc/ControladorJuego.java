package org.presentacion.mvc;

import org.presentacion.interfaces.IInterpretadorCliente;

public class ControladorJuego {
    private final IInterpretadorCliente interpretador;

    public ControladorJuego(IInterpretadorCliente interpretador) {
        if (interpretador == null) {
            throw new IllegalArgumentException("El intérprete cliente es obligatorio.");
        }
        this.interpretador = interpretador;
    }

    public void lanzarDado() {
        interpretador.solicitarLanzarDado();
    }

    public void seleccionarFicha(int idFicha) {
        interpretador.solicitarSeleccionarFicha(idFicha);
    }
}
