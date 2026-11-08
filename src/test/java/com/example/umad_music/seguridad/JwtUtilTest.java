package com.example.umad_music.seguridad;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwt = new JwtUtil("una-clave-de-prueba-con-mas-de-32-caracteres");

    @Test
    void leeElUsuarioDelToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwt.generarToken(7, "ana"));

        assertEquals(7, jwt.usuarioId(request));
        assertEquals("ana", jwt.username(request));
    }

    @Test
    void rechazaSinTokenOConTokenFalso() {
        assertThrows(ResponseStatusException.class, () -> jwt.usuarioId(new MockHttpServletRequest()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token.inventado.123");
        assertThrows(ResponseStatusException.class, () -> jwt.usuarioId(request));
    }

    @Test
    void rechazaTokenFirmadoConOtraClave() {
        JwtUtil otro = new JwtUtil("otra-clave-distinta-tambien-de-32-caracteres");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + otro.generarToken(7, "ana"));

        assertThrows(ResponseStatusException.class, () -> jwt.usuarioId(request));
    }
}
