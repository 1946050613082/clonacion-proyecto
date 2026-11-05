package com.torneo.model;

import java.util.Objects;

public class Jugador {
    private final String id;
    private String nombre;
    private int puntos;
    private int victorias;
    private int derrotas;

    public Jugador(String id, String nombre) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del jugador no puede ser nulo o vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador no puede ser nulo o vacío.");
        }
        this.id = id;
        this.nombre = nombre;
        this.puntos = 0;
        this.victorias = 0;
        this.derrotas = 0;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public synchronized int getPuntos() { return puntos; }
    public synchronized int getVictorias() { return victorias; }
    public synchronized int getDerrotas() { return derrotas; }

    public synchronized void registrarVictoria(int puntosOtorgados) {
        this.victorias++;
        this.puntos += puntosOtorgados;
    }

    public synchronized void registrarDerrota() {
        this.derrotas++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(id, jugador.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}