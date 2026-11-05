package com.torneo.model;

public class Partida {
    private final String idPartida;
    private final int ronda;
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador ganador;
    private EstadoPartida estado;

    public Partida(String idPartida, int ronda) {
        if (idPartida == null || idPartida.isBlank()) {
            throw new IllegalArgumentException("El ID de la partida no puede ser nulo o vacío.");
        }
        this.idPartida = idPartida;
        this.ronda = ronda;
        this.estado = EstadoPartida.PENDIENTE;
    }

    public String getIdPartida() { return idPartida; }
    public int getRonda() { return ronda; }

    public synchronized Jugador getJugador1() { return jugador1; }
    public synchronized void setJugador1(Jugador jugador1) { this.jugador1 = jugador1; }

    public synchronized Jugador getJugador2() { return jugador2; }
    public synchronized void setJugador2(Jugador jugador2) { this.jugador2 = jugador2; }

    public synchronized Jugador getGanador() { return ganador; }
    public synchronized void setGanador(Jugador ganador) { this.ganador = ganador; }

    public synchronized EstadoPartida getEstado() { return estado; }
    public synchronized void setEstado(EstadoPartida estado) { this.estado = estado; }
}