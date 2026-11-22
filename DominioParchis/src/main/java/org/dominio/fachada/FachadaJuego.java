/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.fachada;

import java.util.ArrayList;
import java.util.List;
import org.dominio.entidades.Ficha;
import org.dominio.entidades.Jugador;
import org.dtos.CrearPartidaDTO;
import org.dtos.PartidaCreadaDTO;
import org.dtos.ResultadoLanzarDadoDTO;
import org.dtos.SeleccionarFichaDTO;
import org.dtos.LanzarDadoDTO;
import org.dtos.ResultadoSeleccionarFichaDTO;
import org.dominio.entidades.Tablero;

/**
 * 
 * @author Equipo 1
 */
public class FachadaJuego implements IFachadaJuego {
    private Tablero tablero;
    private int numeroJugadores;

    public FachadaJuego() {
        this.numeroJugadores = 4;
    }

    public FachadaJuego(int numeroJugadores) {
        if (numeroJugadores < 2 || numeroJugadores > 4) {
            throw new IllegalArgumentException("La partida requiere entre 2 y 4 jugadores.");
        }
        this.numeroJugadores = numeroJugadores;
    }

    public FachadaJuego(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public PartidaCreadaDTO crearPartida(CrearPartidaDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("La configuración de la partida es obligatoria.");
        }
        if (dto.getFichasPorJugador() < 3 || dto.getFichasPorJugador() > 6
                || dto.getCasillasSegurasPorAla() < 1 || dto.getCasillasSegurasPorAla() > 5
                || dto.getNumeroSalidaDado() < 1 || dto.getNumeroSalidaDado() > 6) {
            throw new IllegalArgumentException("La configuración de la partida está fuera de los rangos permitidos.");
        }
        if (tablero != null) {
            throw new IllegalStateException("La partida ya fue creada.");
        }

        List<Jugador> jugadores = new ArrayList<>();
        for (int idJugador = 1; idJugador <= numeroJugadores; idJugador++) {
            List<Ficha> fichas = new ArrayList<>();
            for (int numeroFicha = 1; numeroFicha <= dto.getFichasPorJugador(); numeroFicha++) {
                fichas.add(new Ficha(idJugador * 10 + numeroFicha, null));
            }
            jugadores.add(new Jugador(
                    idJugador,
                    "Jugador " + idJugador,
                    "Avatar" + idJugador,
                    fichas));
        }

        tablero = new Tablero(
                jugadores,
                dto.getNumeroSalidaDado(),
                dto.getCasillasSegurasPorAla(),
                dto.getFichasPorJugador());

        return new PartidaCreadaDTO(
                dto.getFichasPorJugador(),
                dto.getCasillasSegurasPorAla(),
                dto.getNumeroSalidaDado(),
                numeroJugadores);
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
                res.idFichaRegresada()
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
                res.idJugadorFichaCapturada(),
                res.idFichaCapturada(),
                res.llegoAMeta(),
                res.cambioTurno(),
                res.esGanador()
        );
    }
}