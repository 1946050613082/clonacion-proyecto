package servidor.torneo;

import com.torneo.service.TorneoService;
import com.torneo.model.Jugador;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import com.google.gson.Gson;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorTorneo {

    private static final Set<WsContext> pantallasConectadas = ConcurrentHashMap.newKeySet();
    private static final Gson gson = new Gson();

    // Servicio con la lógica del Integrante 1
    private static final TorneoService torneoService = new TorneoService();

    public static void main(String[] args) {

        // 1. Iniciar servidor Javalin en el puerto 7000 apuntando a /public
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(8080);

        System.out.println("🚀 Servidor del Torneo iniciado en http://localhost:8080");

        // 2. WebSocket para pantallas secundarias
        app.ws("/ws-torneo", ws -> {
            ws.onConnect(ctx -> {
                pantallasConectadas.add(ctx);
                System.out.println("Nueva pantalla conectada desde: " + ctx.session.getRemoteAddress());
            });

            ws.onClose(ctx -> {
                pantallasConectadas.remove(ctx);
                System.out.println("Pantalla desconectada");
            });

            ws.onError(ctx -> pantallasConectadas.remove(ctx));
        });

        // 3. Endpoint para REGISTRAR JUGADORES
        app.post("/api/jugadores", ctx -> {
            String nombre = ctx.formParam("nombre");
            String tag = ctx.formParam("tag");

            if (nombre != null && tag != null) {
                // Instanciamos el jugador con el modelo del Integrante 1
                Jugador nuevoJugador = new Jugador(nombre, tag);
                
                // NOTA: Si el método en TorneoService tiene otro nombre,
                // simplemente reemplaza .registrarJugador por el nombre exacto que usó tu compañero.
                // torneoService.registrarJugador(nuevoJugador);

                notificarPantallas();
                ctx.result("Jugador " + tag + " registrado con éxito.");
            } else {
                ctx.status(400).result("Faltan parámetros: nombre y tag.");
            }
        });

        // 4. Endpoint para REGISTRAR EL RESULTADO de una partida
        app.post("/api/resultado", ctx -> {
            String idPartida = ctx.formParam("idPartida");
            String idGanador = ctx.formParam("idGanador");

            if (idPartida != null && idGanador != null) {
                // Lógica para registrar resultado
                notificarPantallas();
                ctx.result("Resultado guardado correctamente.");
            } else {
                ctx.status(400).result("Faltan datos de la partida o ganador.");
            }
        });

        // 5. Endpoint GET para consultar el estado del torneo
        app.get("/api/estado", ctx -> {
            ctx.json(torneoService);
        });
    }

    // Método Broadcast para actualizar las pantallas en tiempo real
    private static void notificarPantallas() {
        // Serializa la instancia de TorneoService o sus datos a JSON
        String estadoJson = gson.toJson(torneoService);

        for (WsContext pantalla : pantallasConectadas) {
            if (pantalla.session.isOpen()) {
                pantalla.send(estadoJson);
            }
        }
    }
}
