package com.example.umad_music.controlador;

import com.example.umad_music.modelo.Cancion;
import com.example.umad_music.modelo.Playlist;
import com.example.umad_music.modelo.Reproductor;
import com.example.umad_music.repositorio.CancionRepository;
import com.example.umad_music.repositorio.PlaylistRepository;
import com.example.umad_music.seguridad.JwtUtil;
import com.example.umad_music.servicio.ReproductorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Todas las acciones regresan el estado completo del reproductor.
@RestController
@RequestMapping("/api/reproductor")
public class ReproductorController {

    private final CancionRepository canciones;
    private final PlaylistRepository playlists;
    private final ReproductorService reproductores;
    private final JwtUtil jwt;

    public ReproductorController(CancionRepository canciones, PlaylistRepository playlists,
                                 ReproductorService reproductores, JwtUtil jwt) {
        this.canciones = canciones;
        this.playlists = playlists;
        this.reproductores = reproductores;
        this.jwt = jwt;
    }

    @GetMapping
    public Reproductor estado(HttpServletRequest request) {
        return reproductores.obtener(jwt.usuarioId(request));
    }

    @PostMapping("/playlist/{id}")
    public Reproductor cargarPlaylist(@PathVariable int id, HttpServletRequest request) {
        int usuarioId = jwt.usuarioId(request);
        Playlist playlist = playlists.buscar(id, usuarioId);
        if (playlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La playlist no existe");
        }

        Reproductor reproductor = reproductores.obtener(usuarioId);
        reproductor.cargarPlaylist(playlist, playlists.canciones(id));
        return reproductor;
    }

    @PostMapping("/reproducir/{cancionId}")
    public Reproductor reproducir(@PathVariable int cancionId, HttpServletRequest request) {
        Reproductor reproductor = reproductores.obtener(jwt.usuarioId(request));
        reproductor.reproducir(buscarCancion(cancionId));
        return reproductor;
    }

    @PostMapping("/cola/{cancionId}")
    public Reproductor agregarACola(@PathVariable int cancionId, HttpServletRequest request) {
        Reproductor reproductor = reproductores.obtener(jwt.usuarioId(request));
        reproductor.agregarACola(buscarCancion(cancionId));
        return reproductor;
    }

    @PostMapping("/siguiente")
    public Reproductor siguiente(HttpServletRequest request) {
        Reproductor reproductor = reproductores.obtener(jwt.usuarioId(request));
        reproductor.siguiente();
        return reproductor;
    }

    @PostMapping("/anterior")
    public Reproductor anterior(HttpServletRequest request) {
        Reproductor reproductor = reproductores.obtener(jwt.usuarioId(request));
        reproductor.anterior();
        return reproductor;
    }

    @PostMapping("/regresar")
    public Reproductor regresar(HttpServletRequest request) {
        Reproductor reproductor = reproductores.obtener(jwt.usuarioId(request));
        reproductor.regresar();
        return reproductor;
    }

    private Cancion buscarCancion(int id) {
        Cancion cancion = canciones.buscarPorId(id);
        if (cancion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La canción no existe");
        }
        return cancion;
    }
}
