/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dominio.fachada;

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

    public FachadaJuego(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public ResultadoLanzarDadoDTO lanzarDado(LanzarDadoDTO dto) {
        return tablero.lanzarDado(dto);
    }

    @Override
    public ResultadoSeleccionarFichaDTO seleccionarFicha(SeleccionarFichaDTO dto) {
        return tablero.seleccionarFicha(dto);
    }
}
