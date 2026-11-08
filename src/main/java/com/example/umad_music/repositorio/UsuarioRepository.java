package com.example.umad_music.repositorio;

import com.example.umad_music.modelo.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioRepository {

    private static final RowMapper<Usuario> MAPPER = (rs, numFila) -> new Usuario(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password")
    );

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Usuario buscarPorUsername(String username) {
        List<Usuario> resultado = jdbc.query("SELECT * FROM usuarios WHERE username = ?", MAPPER, username);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public int crear(String username, String passwordEncriptado) {
        return jdbc.queryForObject(
                "INSERT INTO usuarios (username, password) VALUES (?, ?) RETURNING id",
                Integer.class, username, passwordEncriptado);
    }
}
