package com.example.umad_music.repositorio;

import com.example.umad_music.modelo.Cancion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CancionRepository {

    // Convierte una fila de la tabla en un objeto Cancion
    public static final RowMapper<Cancion> MAPPER = (rs, numFila) -> new Cancion(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("artista"),
            rs.getInt("duracion_segundos")
    );

    private final JdbcTemplate jdbc;

    public CancionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Cancion> todas() {
        return jdbc.query("SELECT * FROM canciones ORDER BY artista, titulo", MAPPER);
    }

    public Cancion buscarPorId(int id) {
        List<Cancion> resultado = jdbc.query("SELECT * FROM canciones WHERE id = ?", MAPPER, id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }
}
