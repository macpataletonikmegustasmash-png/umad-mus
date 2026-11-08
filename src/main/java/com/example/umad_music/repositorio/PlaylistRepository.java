package com.example.umad_music.repositorio;

import com.example.umad_music.modelo.Cancion;
import com.example.umad_music.modelo.Playlist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlaylistRepository {

    private static final RowMapper<Playlist> MAPPER = (rs, numFila) -> new Playlist(
            rs.getInt("id"),
            rs.getString("nombre")
    );

    private final JdbcTemplate jdbc;

    public PlaylistRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Playlist> delUsuario(int usuarioId) {
        return jdbc.query("SELECT * FROM playlists WHERE usuario_id = ? ORDER BY id", MAPPER, usuarioId);
    }

    // Solo la encuentra si pertenece al usuario
    public Playlist buscar(int id, int usuarioId) {
        List<Playlist> resultado = jdbc.query(
                "SELECT * FROM playlists WHERE id = ? AND usuario_id = ?", MAPPER, id, usuarioId);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public Playlist crear(String nombre, int usuarioId) {
        Integer id = jdbc.queryForObject(
                "INSERT INTO playlists (nombre, usuario_id) VALUES (?, ?) RETURNING id",
                Integer.class, nombre, usuarioId);
        return new Playlist(id, nombre);
    }

    public void eliminar(int id, int usuarioId) {
        jdbc.update("DELETE FROM playlists WHERE id = ? AND usuario_id = ?", id, usuarioId);
    }

    public List<Cancion> canciones(int playlistId) {
        String sql = """
                SELECT c.* FROM canciones c
                JOIN playlist_canciones pc ON pc.cancion_id = c.id
                WHERE pc.playlist_id = ?
                ORDER BY pc.posicion
                """;
        return jdbc.query(sql, CancionRepository.MAPPER, playlistId);
    }

    // Regresa false si la cancion ya estaba en la playlist
    public boolean agregarCancion(int playlistId, int cancionId) {
        String sql = """
                INSERT INTO playlist_canciones (playlist_id, cancion_id, posicion)
                SELECT ?, ?, COALESCE(MAX(posicion), 0) + 1
                FROM playlist_canciones WHERE playlist_id = ?
                ON CONFLICT DO NOTHING
                """;
        return jdbc.update(sql, playlistId, cancionId, playlistId) > 0;
    }

    public boolean quitarCancion(int playlistId, int cancionId) {
        return jdbc.update("DELETE FROM playlist_canciones WHERE playlist_id = ? AND cancion_id = ?",
                playlistId, cancionId) > 0;
    }
}
