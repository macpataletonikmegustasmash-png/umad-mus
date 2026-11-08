package com.example.umad_music.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long DURACION_MS = 1000L * 60 * 60 * 8; // 8 horas

    private final SecretKey clave;

    public JwtUtil(@Value("${jwt.secret}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(int usuarioId, String username) {
        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + DURACION_MS))
                .signWith(clave)
                .compact();
    }

    // Si no viene el token o no es valido responde 401
    public int usuarioId(HttpServletRequest request) {
        return Integer.parseInt(leerToken(request).getSubject());
    }

    public String username(HttpServletRequest request) {
        return leerToken(request).get("username", String.class);
    }

    // El token llega en el header:  Authorization: Bearer <token>
    private Claims leerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Primero inicia sesión");
        }

        try {
            return Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(header.substring(7))
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El token no es válido o ya venció");
        }
    }
}
