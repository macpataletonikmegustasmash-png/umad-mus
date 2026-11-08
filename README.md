# UMAD Music

App de música para ver cómo se usan estructuras de datos en un proyecto real.
Backend en Spring Boot con base de datos PostgreSQL en Neon.

## ¿Dónde están las estructuras?

Están en `src/main/java/com/example/umad_music/estructuras` y están hechas a mano con nodos (`DoublyNode` para la lista doble y `Node` para la pila y la cola):

| Estructura | Clase | Uso en la app |
|---|---|---|
| Lista doblemente ligada | `DoublyLinkedList` | La playlist que está sonando. Cada nodo apunta al anterior y al siguiente, por eso se puede ir hacia adelante y hacia atrás. |
| Cola | `LinkedQueue` | "Reproducir después". Lo que entra primero sale primero, y tiene prioridad sobre la playlist. |
| Pila | `LinkedStack` | Historial. El botón "Regresar" saca la última canción que se escuchó. |

Al final de cada clase hay una sección con los métodos que se agregaron para poder usarla en el reproductor
(por ejemplo `toList()`, para regresar los datos como JSON).

Las tres se juntan en `modelo/Reproductor.java`. Cada usuario tiene su propio reproductor, guardado en un mapa
dentro de `servicio/ReproductorService.java` (la llave es el id del usuario).

La base de datos guarda usuarios, canciones y playlists. Las estructuras viven en memoria mientras el usuario usa la app.

## Cómo se organiza el código

```
controlador/   reciben las peticiones HTTP (las URLs /api/...)
repositorio/   consultas SQL a la base de datos
modelo/        Cancion, Playlist, Usuario y el Reproductor
servicio/      guarda el reproductor de cada usuario
seguridad/     JwtUtil: crea y valida los tokens
config/        CORS para el frontend
estructuras/   DoublyLinkedList, LinkedStack, LinkedQueue y sus nodos
database/script.sql  crea las tablas y carga canciones de ejemplo
```

El flujo siempre es: **petición HTTP → controlador → repositorio → base de datos**.

## Configuración

### 1. Crear la base de datos en Neon

1. Crea un proyecto en [neon.tech](https://neon.tech).
2. Entra al **SQL Editor**, pega el contenido de `database/script.sql` y ejecútalo.
   El script borra las tablas si ya existían, así que también sirve para reiniciar la base.

### 2. Conectar el backend

En Neon, en **Connect**, copia los datos de conexión. Copia `.env.example` a `.env` y llénalo:

```
DB_URL=jdbc:postgresql://<host-de-neon>/neondb?sslmode=require
DB_USER=<usuario>
DB_PASSWORD=<password>
JWT_SECRET=<una clave larga inventada, minimo 32 caracteres>
FRONTEND_URL=<opcional, dominio del frontend publicado>
```

La URL debe empezar con `jdbc:postgresql://` y **no** lleva el usuario ni la contraseña.
El archivo `.env` está en `.gitignore` para no subir la contraseña a GitHub.

### 3. Correr

```bash
./gradlew bootRun
```

La API queda en http://localhost:8080.

Para correr las pruebas (no necesitan base de datos):

```bash
./gradlew test
```

## Autenticación

Se usa JWT (JSON Web Token):

1. `registro` o `login` regresan un `token`.
2. En cada petición se manda ese token en el header `Authorization: Bearer <token>`.
3. `seguridad/JwtUtil.java` revisa que el token esté firmado con nuestra clave (`JWT_SECRET`) y que no haya vencido
   (dura 8 horas). Si algo falla responde 401.

El token guarda el id y el username del usuario, así el servidor no necesita recordar quién inició sesión.
Para "cerrar sesión" el frontend solo borra el token.

Las contraseñas no se guardan tal cual, se guardan encriptadas con BCrypt.

### Probar con Postman

1. Haz `POST /api/auth/login` con body raw JSON `{"username": "ana", "password": "1234"}` y copia el `token` de la respuesta.
2. En las demás peticiones, en la pestaña **Authorization**, elige **Bearer Token** y pega el token.

Tip: si creas una colección, pon el token en la pestaña **Authorization** de la colección y en cada request deja
**Inherit auth from parent**. Así solo lo pegas una vez.

## Conectar un frontend

`config/CorsConfig.java` decide desde qué páginas se puede llamar a la API:

- `localhost` y `127.0.0.1` en cualquier puerto siempre están permitidos (para desarrollo).
- Si el frontend se publica (por ejemplo en Vercel), se agrega su dominio en el `.env`:
  ```
  FRONTEND_URL=https://umad-music.vercel.app
  ```
  Cualquier otro dominio es bloqueado por el navegador. Esto no afecta a Postman, porque CORS solo lo revisa el navegador.

Para que un frontend publicado funcione, el backend también tiene que estar publicado (Render, Railway, etc.):
una página en internet no puede llamar al `localhost` de tu computadora.

El frontend guarda el token (por ejemplo en `localStorage`) y lo manda en cada petición:

```js
const respuesta = await fetch("http://localhost:8080/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: "ana", password: "1234" })
});
const datos = await respuesta.json();
localStorage.setItem("token", datos.token);

const canciones = await fetch("http://localhost:8080/api/canciones", {
    headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
});
```

## Dependencias

| Dependencia | Para qué |
|---|---|
| `spring-boot-starter-webmvc` | Crear la API REST. |
| `spring-boot-starter-jdbc` | `JdbcTemplate`, para ejecutar SQL desde Java. |
| `spring-security-crypto` | Solo la clase `BCryptPasswordEncoder` para encriptar contraseñas. No activa Spring Security. |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` | Librería [JJWT](https://github.com/jwtk/jjwt) para crear y validar los tokens. Solo usamos `jjwt-api` en el código; las otras dos las necesita por dentro. |
| `postgresql` | Driver para conectarse a PostgreSQL (Neon). |

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| POST | `/api/auth/registro` | Crear cuenta `{username, password}`, regresa el token |
| POST | `/api/auth/login` | Iniciar sesión `{username, password}`, regresa el token |
| GET | `/api/auth/yo` | Usuario actual (necesita token) |

Todos los de abajo necesitan el header `Authorization: Bearer <token>`.

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/canciones` | Catálogo |
| GET | `/api/playlists` | Mis playlists |
| POST | `/api/playlists` | Crear playlist `{nombre}` |
| DELETE | `/api/playlists/{id}` | Borrar playlist |
| GET | `/api/playlists/{id}/canciones` | Canciones de una playlist |
| POST | `/api/playlists/{id}/canciones/{cancionId}` | Agregar canción |
| DELETE | `/api/playlists/{id}/canciones/{cancionId}` | Quitar canción |
| GET | `/api/reproductor` | Estado del reproductor |
| POST | `/api/reproductor/playlist/{id}` | Cargar playlist en la lista doble |
| POST | `/api/reproductor/reproducir/{cancionId}` | Reproducir una canción |
| POST | `/api/reproductor/cola/{cancionId}` | Agregar a la cola |
| POST | `/api/reproductor/siguiente` | Siguiente (primero la cola, luego la lista) |
| POST | `/api/reproductor/anterior` | Anterior en la lista doble |
| POST | `/api/reproductor/regresar` | Sacar de la pila de historial |
