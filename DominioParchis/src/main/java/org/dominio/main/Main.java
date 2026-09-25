/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.dominio.main;

import java.util.Scanner;
import org.dtos.LanzarDadoDTO;
import org.dtos.ResultadoLanzarDadoDTO;
import org.dtos.ResultadoSeleccionarFichaDTO;
import org.dtos.SeleccionarFichaDTO;
import org.dominio.entidades.Ficha;
import org.dominio.entidades.Jugador;
import org.dominio.entidades.Tablero;
import org.dominio.fachada.FachadaJuego;
import org.dominio.fachada.IFachadaJuego;

/**
 * 
 * @author Equipo 1
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numParaSalir = 5;
        int casillasSeguras = 2;
        Tablero tablero = new Tablero(numParaSalir, casillasSeguras);

        Jugador j1 = new Jugador(1, "Jugador 1", "Avatar1");
        Jugador j2 = new Jugador(2, "Jugador 2", "Avatar2");
        Jugador j3 = new Jugador(3, "Jugador 3", "Avatar3");
        Jugador j4 = new Jugador(4, "Jugador 4", "Avatar4");

        Jugador[] jugadores = {j1, j2, j3, j4};

        for (int idx = 0; idx < jugadores.length; idx++) {
            Jugador j = jugadores[idx];
            int baseFichaId = (idx + 1) * 10;
            for (int i = 1; i <= 4; i++) {
                j.agregarFicha(new Ficha(baseFichaId + i, j));
            }
            tablero.agregarJugador(j);
        }

        IFachadaJuego juego = new FachadaJuego(tablero);
        boolean juegoActivo = true;

        System.out.println("=== PARCHIS INTERACTIVO EN TERMINAL (4 JUGADORES) ===");

        while (juegoActivo) {
            Jugador jugadorActual = tablero.getJugadorTurnoActual();
            int idJugadorActual = jugadorActual.getId();

            System.out.println("\n==================================================");
            System.out.println("TURNO DEL JUGADOR: " + jugadorActual.getNombre() + " (ID Jugador: " + idJugadorActual + ")");
            System.out.print("Fichas de este jugador -> ");
            for (Ficha f : jugadorActual.getFichas()) {
                System.out.print("[ID: " + f.getId() + " | Pos: " + f.getPosicion() + " | Estado: " + f.getEstado() + "] ");
            }
            System.out.println("\n--------------------------------------------------");
            System.out.println("Presiona ENTER para lanzar el dado...");
            scanner.nextLine();

            try {
                LanzarDadoDTO lanzarDto = new LanzarDadoDTO(idJugadorActual);
                ResultadoLanzarDadoDTO resDado = juego.lanzarDado(lanzarDto);

                System.out.println("--> Dado obtenido: [" + resDado.getResultadoDado() + "]");
                System.out.println("¿Puede sacar ficha del círculo?: " + resDado.isPuedeSacarFicha());
                System.out.println("¿Puede repetir tiro por sacarle 6?: " + resDado.isPuedeVolverATirar());

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

                        if (!resMover.isCambioTurno()) {
                            System.out.println("\n¡Repites turno por sacarle 6!");
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