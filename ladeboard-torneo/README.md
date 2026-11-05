
<img width="3664" height="6790" alt="Smash Bros Tournament Integration Flow-2026-10-16-214405" src="https://github.com/user-attachments/assets/2d0d38e8-de65-4247-a443-ddad50cac285" />

# 🏆 Smash Bros Tournament System — Backend (`torneo-core`)

![Java 17](https://img.shields.io/badge/Java-17_LTS-orange?style=for-the-badge&logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apachemaven)
![Concurrency](https://img.shields.io/badge/Concurrency-Thread--Safe-brightgreen?style=for-the-badge)
![Architecture](https://img.shields.io/badge/Architecture-MVC%20%2F%20DTO-blue?style=for-the-badge)

> 📘 **Guía Práctica de Integración**: Este documento explica de forma sencilla qué realizó el **Integrante 1**, cómo funciona el módulo `torneo-core`, qué necesita cada integrante en su PC y el paso a paso exacto para conectar todas las partes del proyecto.

---

## 🛠️ Requisitos Mínimos en las PCs del Equipo

Para que el proyecto compile y funcione en las computadoras de todos:

* **Java JDK 17** o superior instalado.
* **VS Code** con la extensión *"Extension Pack for Java"* (o Apache NetBeans).
* Conexión a la **misma red Wi-Fi o punto de acceso móvil** para el día de las pruebas locales.

---

## 📌 ¿Qué hizo el Integrante 1 (Backend)?

Construí el **motor lógico del torneo** (`torneo-core`). Mi código es la **Única Fuente de Verdad**:

1. **Jugadores y Puntos:** Asigna +100 puntos y +1 victoria al ganador, y +1 derrota al perdedor.
2. **Tabla de Posiciones (`Leaderboard`):** Se ordena automáticamente (Puntos -> Victorias -> Derrotas).
3. **Avanzar Rondas:** Cuando termina la última partida de la ronda, crea automáticamente los enfrentamientos de la siguiente ronda.
4. **Seguridad (`Thread-Safe`):** Sincronizado para que no falle aunque lleguen varias peticiones seguidas por Wi-Fi.

---

## 🔄 Orden de Trabajo e Integración del Equipo

```text
+--------------------------------+      +--------------------------------+      +--------------------------------+
| PASO 1: Integrante 1 (Backend) | ---> | PASO 2: Integrante 2 (Servidor)| ---> | PASO 3: Integrante 3 (Frontend)|
|  Lógica Pura (COMPLETADO)      |      |  Javalin + WebSockets + API    |      |  Interfaz HTML / CSS / JS      |
+--------------------------------+      +--------------------------------+      +--------------------------------+
```

---

## 🌐 Módulo Servidor & Despliegue en AWS (`torneo-server`) — **Integrante 2**

![AWS EC2](https://img.shields.io/badge/AWS-EC2_Ubuntu_24.04_LTS-FF9900?style=for-the-badge&logo=amazonaws)
![Javalin](https://img.shields.io/badge/Javalin-Microframework-009688?style=for-the-badge)
![WebSockets](https://img.shields.io/badge/WebSockets-Real--Time-red?style=for-the-badge)
![Java 26](https://img.shields.io/badge/Java-26_Temurin-orange?style=for-the-badge&logo=openjdk)

> 🛰️ **Servidor HTTP & Comunicación en Tiempo Real**: Este módulo expone la lógica de `torneo-core` a través de una API REST y conexiones WebSocket, alojado en un servidor público de AWS para acceso universal mediante código QR.

---

### 📌¿Qué realizó el Integrante 2 (Servidor & Cloud)?

1. **Servidor HTTP con Javalin**:
   - Configuración de microservicios en Java para recibir registros de jugadores y actualizaciones de resultados.
   - Enrutamiento de recursos estáticos en la carpeta `/public` para hospedar la interfaz web (HTML/CSS/JS).

2. **Sockets en Tiempo Real (`WebSockets`)**:
   - Endpoint `/ws-torneo` habilitado para emisión automática (*broadcast*) del estado del árbol y la tabla de posiciones a todos los dispositivos conectados simultáneamente.

3. **Infraestructura Cloud en AWS**:
   - Instancia **AWS EC2 (Ubuntu 24.04 LTS)** configurada en la capa gratuita.
   - Entorno configurado con **Java 26 (Temurin)** gestionado con SDKMAN!.
   - Reglas de red y grupos de seguridad (Security Groups) configurados para permitir tráfico web continuo.
   - **IP Pública del Servidor**: `http://98.84.104.169:8080` (Disponible 24/7 sin depender de la red Wi-Fi universitaria ni la laptop local).

---

## 📡 Endpoints de la API REST & WebSockets

| Método | Endpoint | Descripción | Formato / Body |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/jugadores` | Obtiene la lista completa de jugadores registrados | JSON Array |
| **POST** | `/api/jugadores` | Registra un nuevo participante en el torneo | `{"nombre": "Jugador1", "tag": "SmashKing"}` |
| **POST** | `/api/resultado` | Notifica el ganador de una partida y avanza el árbol | `{"idPartida": 1, "idGanador": 3}` |
| **WS** | `/ws-torneo` | Canal WebSocket para actualización en tiempo real | JSON Broadcast |

---

# 🚀 Guía de despliegue y pruebas locales

> ⚠️ **Importante:** La clave privada `torneo-keys.pem` no debe subirse al repositorio ni compartirse públicamente. Obténla únicamente por el canal oficial (WhatsApp xd)

## 1. Conexión al servidor mediante SSH

Para acceder al servidor necesitas tener la clave privada `torneo-keys.pem`.

### Dar permisos a la clave (Mac/Linux)

```bash
chmod 400 ~/Downloads/torneo-keys.pem
```

### Conectarse al servidor (recuerda utilizar la ruta donde tengas la key)

```bash
ssh -i ~/Downloads/torneo-keys.pem ubuntu@torneo-etitc.duckdns.org
```

---

## 2. Gestión del servidor en AWS

Una vez conectado al servidor por SSH, usa los siguientes comandos:

### Iniciar el servidor en segundo plano

```bash
nohup java -jar servidor.torneo-1.0-SNAPSHOT.jar > salida.log 2>&1 &
```

### Detener el servidor

```bash
killall -9 java
```

### Ver los registros en tiempo real

```bash
tail -f salida.log
```

Para salir del visor de registros, presiona `Ctrl + C`.

---

## 3. Compilar y desplegar cambios del backend

Si hiciste cambios en Java o en los archivos de la interfaz, sigue estos pasos.

### 1. Generar el archivo `.jar`

En NetBeans o VS Code, ejecuta **Clean and Build** para generar el nuevo archivo compilado.

### 2. Subir el ejecutable al servidor

Ejecuta este comando desde la carpeta raíz del proyecto en tu equipo local:

```bash
scp -i ~/Downloads/torneo-keys.pem target/servidor.torneo-1.0-SNAPSHOT.jar ubuntu@torneo-etitc.duckdns.org:~/
```

### 3. Aplicar los cambios en AWS

Conéctate nuevamente por SSH y ejecuta los comandos de detener e iniciar el servidor:

```bash
killall -9 java
nohup java -jar servidor.torneo-1.0-SNAPSHOT.jar > salida.log 2>&1 &
```

---

## 4. Desarrollo y pruebas locales

### Actualizar el repositorio

```bash
git pull origin main
```

### Ubicación del frontend

Los archivos de la interfaz gráfica —HTML, CSS, JavaScript e imágenes— deben ubicarse en:

```text
src/main/resources/public/
```

### Ejecutar el servidor localmente

En NetBeans:

1. Haz clic derecho sobre `ServidorTorneo.java`.
2. Selecciona **Run File**.
3. También puedes usar el atajo `Shift + F6`.

### Abrir la aplicación

Con el servidor ejecutándose, abre en el navegador:

```text
http://localhost:8080
```

Desde allí podrás probar la plataforma y la conexión mediante WebSocket de forma local.


