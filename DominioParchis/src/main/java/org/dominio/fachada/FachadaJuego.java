/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.fachada;

import java.util.ArrayList;
import java.util.List;
import org.dtos.FichaDTO;
import org.dtos.ResultadoLanzarDadoDTO;
import org.dtos.SeleccionarFichaDTO;
import org.dtos.LanzarDadoDTO;
import org.dtos.ResultadoSeleccionarFichaDTO;
import org.dominio.entidades.Ficha;
import org.dominio.entidades.Jugador;
import org.dominio.entidades.Tablero;

/**
 *
 * @author Equipo 1
 */
public class FachadaJuego implements IFachadaJuego {
    private Tablero tablero;

    public FachadaJuego(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public ResultadoLanzarDadoDTO lanzarDado(LanzarDadoDTO dto) {
        Tablero.ResultadoLanzarDadoDominio res = tablero.lanzarDado(dto.getIdJugador());

        return new ResultadoLanzarDadoDTO(
                res.idJugador(),
                res.resultadoDado(),
                res.puedeVolverATirar(),
                res.puedeSacarFicha(),
                res.tieneMovimientoValido(),
                obtenerFichasDTO()
        );
    }

    @Override
    public ResultadoSeleccionarFichaDTO seleccionarFicha(SeleccionarFichaDTO dto) {
        Tablero.ResultadoSeleccionarFichaDominio res = tablero.seleccionarFicha(dto.getIdJugador(), dto.getIdFicha());

        return new ResultadoSeleccionarFichaDTO(
                res.idJugador(),
                res.idFicha(),
                res.posicionFicha(),
                res.capturo(),
                res.llegoAMeta(),
                res.cambioTurno(),
                obtenerFichasDTO()
        );
    }

    private List<FichaDTO> obtenerFichasDTO() {
        List<FichaDTO> fichas = new ArrayList<>();
        for (Jugador jugador : tablero.getJugadores()) {
            for (Ficha ficha : jugador.getFichas()) {
                fichas.add(new FichaDTO(jugador.getId(), ficha.getId(), ficha.getPosicion()));
            }
        }
        return fichas;
    }
}
