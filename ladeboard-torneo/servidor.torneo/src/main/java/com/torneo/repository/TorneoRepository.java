package com.torneo.repository;

import com.torneo.config.DatabaseConfig;
import com.torneo.model.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TorneoRepository {

    public void guardarJugador(Jugador jugador) {
        String sql = "INSERT INTO jugadores (id, nombre, puntos, victorias, derrotas) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET nombre = EXCLUDED.nombre";
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

    public List<Jugador> cargarJugadores() {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT * FROM jugadores ORDER BY puntos DESC, victorias DESC, derrotas ASC";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Jugador j = new Jugador(rs.getString("id"), rs.getString("nombre"));
                j.registrarVictoria(rs.getInt("puntos"));
                lista.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}