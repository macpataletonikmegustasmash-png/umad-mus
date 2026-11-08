package com.example.umad_music.servicio;

import com.example.umad_music.modelo.Reproductor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Con JWT el servidor no guarda sesiones, asi que el reproductor de cada usuario
// se guarda aqui, en memoria, usando su id como llave.
@Service
public class ReproductorService {

    // ConcurrentHashMap porque varios usuarios pueden hacer peticiones al mismo tiempo
    private final Map<Integer, Reproductor> reproductores = new ConcurrentHashMap<>();

    public Reproductor obtener(int usuarioId) {
        return reproductores.computeIfAbsent(usuarioId, id -> new Reproductor());
    }
}
