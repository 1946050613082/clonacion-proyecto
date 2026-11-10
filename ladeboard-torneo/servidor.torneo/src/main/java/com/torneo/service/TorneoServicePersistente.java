package com.torneo.service;

import com.torneo.config.DatabaseConfig;
import com.torneo.model.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TorneoServicePersistente {

    private final TorneoService torneoServiceOriginal = new TorneoService();

    public synchronized void agregarJugador(Jugador jugador) {
        torneoServiceOriginal.agregarJugador(jugador);
        guardarJugadorEnBD(jugador);
    }

    public synchronized void inicializarTorneo() {
        torneoServiceOriginal.inicializarTorneo();
    }

    public synchronized boolean registrarGanador(String idPartida, String idGanador) {
        boolean exito = torneoServiceOriginal.registrarGanador(idPartida, idGanador);
        if (exito) {
            for (Jugador j : torneoServiceOriginal.obtenerLeaderboard()) {
                guardarJugadorEnBD(j);
            }
        }
        return exito;
    }

    public synchronized List<Jugador> obtenerLeaderboard() {
        return torneoServiceOriginal.obtenerLeaderboard();
    }

    public synchronized Map<String, Object> obtenerEstadoTorneoDTO() {
        return torneoServiceOriginal.obtenerEstadoTorneoDTO();
    }

    private void guardarJugadorEnBD(Jugador jugador) {
        String sql = "INSERT INTO jugadores (id, nombre, puntos, victorias, derrotas) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET nombre = EXCLUDED.nombre, puntos = EXCLUDED.puntos, " +
                "victorias = EXCLUDED.victorias, derrotas = EXCLUDED.derrotas";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jugador.getId());
            stmt.setString(2, jugador.getNombre());
            stmt.setInt(3, jugador.getPuntos());
            stmt.setInt(4, jugador.getVictorias());
            stmt.setInt(5, jugador.getDerrotas());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}