package com.example.umad_music.modelo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReproductorTest {

    private final Cancion a = new Cancion(1, "A", "Artista", 100);
    private final Cancion b = new Cancion(2, "B", "Artista", 100);
    private final Cancion c = new Cancion(3, "C", "Artista", 100);
    private final Cancion extra = new Cancion(4, "Extra", "Artista", 100);

    @Test
    void laColaTienePrioridadSobreLaPlaylist() {
        Reproductor reproductor = new Reproductor();
        reproductor.cargarPlaylist(new Playlist(1, "Mix"), List.of(a, b, c));
        reproductor.agregarACola(extra);

        reproductor.siguiente();
        assertEquals(extra, reproductor.getActual());

        reproductor.siguiente();
        assertEquals(b, reproductor.getActual());
    }

    @Test
    void regresarUsaElHistorial() {
        Reproductor reproductor = new Reproductor();
        reproductor.cargarPlaylist(new Playlist(1, "Mix"), List.of(a, b, c));
        reproductor.siguiente();
        reproductor.siguiente();

        assertEquals(List.of(b, a), reproductor.getHistorial());
        reproductor.regresar();
        assertEquals(b, reproductor.getActual());
        reproductor.regresar();
        assertEquals(a, reproductor.getActual());
    }

    @Test
    void quitarLaCancionActualNoRompeLaNavegacion() {
        Reproductor reproductor = new Reproductor();
        Playlist mix = new Playlist(1, "Mix");
        reproductor.cargarPlaylist(mix, List.of(a, b, c));
        reproductor.siguiente();

        reproductor.cancionQuitada(1, b);
        assertEquals(List.of(a, c), reproductor.getCanciones());
        reproductor.siguiente();
        assertEquals(c, reproductor.getActual());
    }
}
