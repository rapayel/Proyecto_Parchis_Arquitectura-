/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.dominio.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.dtos.*;
import org.dominio.entidades.*;
import org.dominio.fachada.*;

/**
 * 
 * @author Equipo 1
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Jugador> listaJugadores = new ArrayList<>();

        for (int idx = 1; idx <= 4; idx++) {
            List<Ficha> fichasJugador = new ArrayList<>();
            int baseFichaId = idx * 10;
            for (int i = 1; i <= 4; i++) {
                fichasJugador.add(new Ficha(baseFichaId + i, null));
            }
            
            Jugador jugador = new Jugador(idx, "Jugador " + idx, "Avatar" + idx, fichasJugador);
            listaJugadores.add(jugador);
        }

        int numParaSalir = 5;
        int casillasSeguras = 2;
        Tablero tablero = new Tablero(listaJugadores, numParaSalir, casillasSeguras);
        IFachadaJuego juego = new FachadaJuego(tablero);
        boolean juegoActivo = true;

        System.out.println("=== PARCHIS INTERACTIVO EN TERMINAL (4 JUGADORES) ===");

        while (juegoActivo) {
            Jugador jugadorActual = tablero.getJugadorTurnoActual();
            int idJugadorActual = jugadorActual.getId();

            System.out.println("\n==================================================");
            System.out.println("TURNO DEL JUGADOR: " + jugadorActual.getPerfilCompleto() + " (ID: " + idJugadorActual + ")");
            System.out.println("--------------------------------------------------");
            System.out.println("Presiona ENTER para lanzar el dado...");
            scanner.nextLine();

            try {
                LanzarDadoDTO lanzarDto = new LanzarDadoDTO(idJugadorActual);
                ResultadoLanzarDadoDTO resDado = juego.lanzarDado(lanzarDto);

                System.out.println("--> Dado obtenido: [" + resDado.getResultadoDado() + "]");

                if (!resDado.isTieneMovimientoValido()) {
                    System.out.println("\n[!] No tienes movimientos válidos posibles.");
                    System.out.println("--> Pierdes el turno automáticamente.");
                    continue;
                }

                boolean seleccionValida = false;

                while (!seleccionValida) {
                    System.out.print("Ingresa el ID de la ficha a mover: ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Por favor ingresa un número entero válido.");
                        scanner.next();
                        continue;
                    }

                    int idFicha = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        SeleccionarFichaDTO selecDto = new SeleccionarFichaDTO(idJugadorActual, idFicha);
                        ResultadoSeleccionarFichaDTO resMover = juego.seleccionarFicha(selecDto);

                        System.out.println("\n--- RESULTADO DEL MOVIMIENTO ---");
                        System.out.println("Jugador ID: " + resMover.getIdJugador());
                        System.out.println("Ficha ID: " + resMover.getIdFicha());
                        System.out.println("Nueva Posición: " + resMover.getPosicionFicha());
                        System.out.println("¿Capturó ficha rival?: " + resMover.isCapturo());
                        System.out.println("¿Llegó a la meta?: " + resMover.isLlegoAMeta());
                        System.out.println("¿Cambió el turno?: " + resMover.isCambioTurno());

                        seleccionValida = true;

                        if (jugadorActual.todasLlegaronAMeta()) {
                            System.out.println("\n ¡EL JUGADOR " + jugadorActual.getNombre() + " HA GANADO LA PARTIDA!");
                            juegoActivo = false;
                            break;
                        }

                        if (!resMover.isCambioTurno()) {
                            System.out.println("\n¡Repites turno por sacar 6!");
                        }

                    } catch (IllegalStateException | IllegalArgumentException e) {
                        System.out.println("Error en la jugada: " + e.getMessage());
                        System.out.println("Intenta seleccionando otra ficha válida.");
                    }
                }

            } catch (IllegalStateException e) {
                System.out.println("Error en el dado: " + e.getMessage());
            }
        }

        scanner.close();
    }
}