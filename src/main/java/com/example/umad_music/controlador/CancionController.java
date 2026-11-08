package com.example.umad_music.controlador;

import com.example.umad_music.modelo.Cancion;
import com.example.umad_music.repositorio.CancionRepository;
import com.example.umad_music.seguridad.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    private final CancionRepository canciones;
    private final JwtUtil jwt;

    public CancionController(CancionRepository canciones, JwtUtil jwt) {
        this.canciones = canciones;
        this.jwt = jwt;
    }

    @GetMapping
    public List<Cancion> todas(HttpServletRequest request) {
        jwt.usuarioId(request);
        return canciones.todas();
    }
}
