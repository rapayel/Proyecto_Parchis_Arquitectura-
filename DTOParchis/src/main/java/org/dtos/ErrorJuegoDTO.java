package org.dtos;

public class ErrorJuegoDTO implements IDTO {
    private static final long serialVersionUID = 1L;
    private final String mensaje;

    public ErrorJuegoDTO(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
