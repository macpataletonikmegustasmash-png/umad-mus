package com.example.umad_music.controlador;

import com.example.umad_music.modelo.Usuario;
import com.example.umad_music.repositorio.UsuarioRepository;
import com.example.umad_music.seguridad.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record Credenciales(String username, String password) {
    }

    private final UsuarioRepository usuarios;
    private final JwtUtil jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UsuarioRepository usuarios, JwtUtil jwt) {
        this.usuarios = usuarios;
        this.jwt = jwt;
    }

    @PostMapping("/registro")
    public Map<String, Object> registro(@RequestBody Credenciales datos) {
        if (datos.username() == null || datos.username().isBlank()
                || datos.password() == null || datos.password().length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El usuario es obligatorio y la contraseña debe tener al menos 4 caracteres");
        }
        String username = datos.username().trim();
        if (usuarios.buscarPorUsername(username) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese usuario ya existe");
        }

        int id = usuarios.crear(username, encoder.encode(datos.password()));
        return respuestaConToken(id, username);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Credenciales datos) {
        Usuario usuario = usuarios.buscarPorUsername(datos.username());
        if (usuario == null || !encoder.matches(datos.password(), usuario.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }

        return respuestaConToken(usuario.id(), usuario.username());
    }

    @GetMapping("/yo")
    public Map<String, Object> yo(HttpServletRequest request) {
        return Map.of("id", jwt.usuarioId(request), "username", jwt.username(request));
    }

    private Map<String, Object> respuestaConToken(int id, String username) {
        return Map.of("id", id, "username", username, "token", jwt.generarToken(id, username));
    }
}
