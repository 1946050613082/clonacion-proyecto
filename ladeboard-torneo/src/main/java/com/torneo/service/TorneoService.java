package com.torneo.service;

import com.torneo.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TorneoService {
    private final Map<String, Jugador> jugadores = new ConcurrentHashMap<>();
    private final Map<String, Partida> partidas = new ConcurrentHashMap<>();

    public synchronized void agregarJugador(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser nulo.");
        }
        if (jugadores.containsKey(jugador.getId())) {
            throw new IllegalArgumentException("Ya existe un jugador registrado con ID: " + jugador.getId());
        }
        jugadores.put(jugador.getId(), jugador);
    }

    public synchronized void inicializarTorneo() {
        List<Jugador> lista = new ArrayList<>(jugadores.values());
        if (lista.size() < 2) {
            throw new IllegalStateException("Se requieren al menos 2 jugadores para inicializar el torneo.");
        }

        Collections.shuffle(lista);
        partidas.clear();

        int numPartidasRonda1 = lista.size() / 2;
        for (int i = 0; i < numPartidasRonda1; i++) {
            String idPartida = "R1-P" + (i + 1);
            Partida partida = new Partida(idPartida, 1);
            partida.setJugador1(lista.get(i * 2));

            if ((i * 2 + 1) < lista.size()) {
                partida.setJugador2(lista.get(i * 2 + 1));
                partida.setEstado(EstadoPartida.EN_PROGRESO);
            } else {
                partida.setGanador(lista.get(i * 2));
                partida.setEstado(EstadoPartida.FINALIZADA);
            }
            partidas.put(idPartida, partida);
        }
    }

    public synchronized boolean registrarGanador(String idPartida, String idGanador) {
        Partida partida = partidas.get(idPartida);
        Jugador ganador = jugadores.get(idGanador);

        if (partida == null || ganador == null) {
            return false;
        }

        if (partida.getEstado() == EstadoPartida.FINALIZADA) {
            throw new IllegalStateException("La partida " + idPartida + " ya se encuentra finalizada.");
        }

        if (!ganador.equals(partida.getJugador1()) && !ganador.equals(partida.getJugador2())) {
            throw new IllegalArgumentException("El jugador " + idGanador + " no participa en la partida " + idPartida);
        }

        partida.setGanador(ganador);
        partida.setEstado(EstadoPartida.FINALIZADA);

        Jugador perdedor = ganador.equals(partida.getJugador1()) ? partida.getJugador2() : partida.getJugador1();
        ganador.registrarVictoria(100);
        if (perdedor != null) {
            perdedor.registrarDerrota();
        }

        verificarYGenerarSiguienteRonda(partida.getRonda());
        return true;
    }

    private synchronized void verificarYGenerarSiguienteRonda(int rondaActual) {
        boolean todasFinalizadas = partidas.values().stream()
                .filter(p -> p.getRonda() == rondaActual)
                .allMatch(p -> p.getEstado() == EstadoPartida.FINALIZADA);

        if (!todasFinalizadas) return;

        List<Jugador> ganadoresRonda = partidas.values().stream()
                .filter(p -> p.getRonda() == rondaActual)
                .map(Partida::getGanador)
                .filter(Objects::nonNull)
                .toList();

        if (ganadoresRonda.size() <= 1) {
            return;
        }

        int siguienteRonda = rondaActual + 1;
        int numNuevasPartidas = ganadoresRonda.size() / 2;

        for (int i = 0; i < numNuevasPartidas; i++) {
            String idPartida = "R" + siguienteRonda + "-P" + (i + 1);
            Partida nuevaPartida = new Partida(idPartida, siguienteRonda);
            nuevaPartida.setJugador1(ganadoresRonda.get(i * 2));

            if ((i * 2 + 1) < ganadoresRonda.size()) {
                nuevaPartida.setJugador2(ganadoresRonda.get(i * 2 + 1));
                nuevaPartida.setEstado(EstadoPartida.EN_PROGRESO);
            } else {
                nuevaPartida.setGanador(ganadoresRonda.get(i * 2));
                nuevaPartida.setEstado(EstadoPartida.FINALIZADA);
            }
            partidas.put(idPartida, nuevaPartida);
        }
    }

    public synchronized List<Jugador> obtenerLeaderboard() {
        List<Jugador> lista = new ArrayList<>(jugadores.values());
        lista.sort((j1, j2) -> Integer.compare(j2.getPuntos(), j1.getPuntos()));
        return lista;
    }

    public synchronized Map<String, Object> obtenerEstadoTorneoDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("leaderboard", obtenerLeaderboard());
        dto.put("partidas", partidas.values());
        return dto;
    }
}