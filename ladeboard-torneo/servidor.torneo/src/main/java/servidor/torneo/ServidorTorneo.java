package servidor.torneo;

import com.google.gson.Gson;
import com.torneo.model.Jugador;
import com.torneo.repository.AuthDAO;
import com.torneo.service.TorneoServicePersistente;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorTorneo {

    private static final Set<WsContext> pantallasConectadas = ConcurrentHashMap.newKeySet();
    private static final Gson gson = new Gson();

    private static final TorneoServicePersistente torneoService = new TorneoServicePersistente();
    private static final AuthDAO authDAO = new AuthDAO();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(8080);

        System.out.println("🚀 Servidor del Torneo iniciado en http://localhost:8080");

        // 1. Canal WebSocket para tiempo real
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

        // 2. Endpoint de Autenticación / Login de Administrador
        app.post("/api/login", ctx -> {
            String user = ctx.formParam("username");
            String pass = ctx.formParam("password");

            if (user != null && pass != null && authDAO.autenticar(user, pass)) {
                ctx.status(200).result("{\"status\":\"OK\",\"message\":\"Login exitoso\"}");
            } else {
                ctx.status(401).result("{\"status\":\"ERROR\",\"message\":\"Credenciales incorrectas\"}");
            }
        });

        // 3. Endpoint Registro de Jugadores
        app.post("/api/jugadores", ctx -> {
            String nombre = ctx.formParam("nombre");
            String tag = ctx.formParam("tag");

            if (nombre != null && tag != null) {
                Jugador nuevoJugador = new Jugador(tag, nombre);
                torneoService.agregarJugador(nuevoJugador);

                notificarPantallas();
                ctx.result("Jugador " + tag + " registrado con éxito.");
            } else {
                ctx.status(400).result("Faltan parámetros: nombre y tag.");
            }
        });

        // 4. Endpoint Resultado Partida
        app.post("/api/resultado", ctx -> {
            String idPartida = ctx.formParam("idPartida");
            String idGanador = ctx.formParam("idGanador");

            if (idPartida != null && idGanador != null) {
                boolean exito = torneoService.registrarGanador(idPartida, idGanador);
                if (exito) {
                    notificarPantallas();
                    ctx.result("Resultado guardado correctamente.");
                } else {
                    ctx.status(400).result("No se pudo registrar el resultado.");
                }
            } else {
                ctx.status(400).result("Faltan datos de la partida o ganador.");
            }
        });

        // 5. Endpoint Consulta Estado Torneo
        app.get("/api/estado", ctx -> {
            ctx.json(torneoService.obtenerEstadoTorneoDTO());
        });
    }

    private static void notificarPantallas() {
        String estadoJson = gson.toJson(torneoService.obtenerEstadoTorneoDTO());
        for (WsContext pantalla : pantallasConectadas) {
            if (pantalla.session.isOpen()) {
                pantalla.send(estadoJson);
            }
        }
    }
}