package com.example.umad_music.controlador;

import com.example.umad_music.modelo.Cancion;
import com.example.umad_music.modelo.Playlist;
import com.example.umad_music.repositorio.CancionRepository;
import com.example.umad_music.repositorio.PlaylistRepository;
import com.example.umad_music.seguridad.JwtUtil;
import com.example.umad_music.servicio.ReproductorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    public record NuevaPlaylist(String nombre) {
    }

    private final PlaylistRepository playlists;
    private final CancionRepository canciones;
    private final ReproductorService reproductores;
    private final JwtUtil jwt;

    public PlaylistController(PlaylistRepository playlists, CancionRepository canciones,
                              ReproductorService reproductores, JwtUtil jwt) {
        this.playlists = playlists;
        this.canciones = canciones;
        this.reproductores = reproductores;
        this.jwt = jwt;
    }

    @GetMapping
    public List<Playlist> misPlaylists(HttpServletRequest request) {
        return playlists.delUsuario(jwt.usuarioId(request));
    }

    @PostMapping
    public Playlist crear(@RequestBody NuevaPlaylist datos, HttpServletRequest request) {
        int usuarioId = jwt.usuarioId(request);
        if (datos.nombre() == null || datos.nombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La playlist necesita un nombre");
        }
        return playlists.crear(datos.nombre().trim(), usuarioId);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id, HttpServletRequest request) {
        playlists.eliminar(id, jwt.usuarioId(request));
    }

    @GetMapping("/{id}/canciones")
    public List<Cancion> canciones(@PathVariable int id, HttpServletRequest request) {
        validarPlaylist(id, jwt.usuarioId(request));
        return playlists.canciones(id);
    }

    @PostMapping("/{id}/canciones/{cancionId}")
    public void agregarCancion(@PathVariable int id, @PathVariable int cancionId, HttpServletRequest request) {
        int usuarioId = jwt.usuarioId(request);
        validarPlaylist(id, usuarioId);
        Cancion cancion = canciones.buscarPorId(cancionId);
        if (cancion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La canción no existe");
        }

        if (playlists.agregarCancion(id, cancionId)) {
            reproductores.obtener(usuarioId).cancionAgregada(id, cancion);
        }
    }

    @DeleteMapping("/{id}/canciones/{cancionId}")
    public void quitarCancion(@PathVariable int id, @PathVariable int cancionId, HttpServletRequest request) {
        int usuarioId = jwt.usuarioId(request);
        validarPlaylist(id, usuarioId);
        Cancion cancion = canciones.buscarPorId(cancionId);

        if (cancion != null && playlists.quitarCancion(id, cancionId)) {
            reproductores.obtener(usuarioId).cancionQuitada(id, cancion);
        }
    }

    private void validarPlaylist(int id, int usuarioId) {
        if (playlists.buscar(id, usuarioId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La playlist no existe");
        }
    }
}
