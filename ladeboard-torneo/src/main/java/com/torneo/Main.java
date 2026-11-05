package com.torneo;

import com.torneo.model.Jugador;
import com.torneo.service.TorneoService;

public class Main {
    public static void main(String[] args) {
        try {
            TorneoService servicio = new TorneoService();

            System.out.println("=== 1. REGISTRANDO JUGADORES ===");
            servicio.agregarJugador(new Jugador("J1", "Mario"));
            servicio.agregarJugador(new Jugador("J2", "Fox"));
            servicio.agregarJugador(new Jugador("J3", "Link"));
            servicio.agregarJugador(new Jugador("J4", "Pikachu"));

            System.out.println("=== 2. INICIALIZANDO TORNEO ===");
            servicio.inicializarTorneo();

            System.out.println("\n--- Estado Inicial del Torneo ---");
            System.out.println(servicio.obtenerEstadoTorneoDTO());

            System.out.println("\n=== 3. REGISTRANDO VICTORIA DE MARIO (J1) ===");
            servicio.registrarGanador("R1-P1", "J1");

            System.out.println("\n--- Leaderboard Actualizado ---");
            for (Jugador j : servicio.obtenerLeaderboard()) {
                System.out
                        .println(j.getNombre() + " -> Puntos: " + j.getPuntos() + " | Victorias: " + j.getVictorias());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}