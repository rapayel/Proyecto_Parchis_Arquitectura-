/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.dominio.fachada;
import org.dtos.*;

/**
 * 
 * @author Equipo 1
 */
public interface IFachadaJuego {
    public ResultadoLanzarDadoDTO lanzarDado(LanzarDadoDTO dto);
    public ResultadoSeleccionarFichaDTO seleccionarFicha(SeleccionarFichaDTO dto);
}
