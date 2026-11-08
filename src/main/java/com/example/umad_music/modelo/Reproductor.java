package com.example.umad_music.modelo;

import com.example.umad_music.estructuras.DoublyLinkedList;
import com.example.umad_music.estructuras.LinkedQueue;
import com.example.umad_music.estructuras.LinkedStack;

import java.util.List;

// Cada usuario tiene su propio reproductor (ver ReproductorService).
//  - DoublyLinkedList: la playlist cargada, para ir a la siguiente o a la anterior.
//  - LinkedQueue: canciones agregadas con "reproducir despues", tienen prioridad sobre la playlist.
//  - LinkedStack: historial de lo que se ha escuchado, para el boton "regresar".
public class Reproductor {

    private final DoublyLinkedList<Cancion> lista = new DoublyLinkedList<>();
    private final LinkedQueue<Cancion> cola = new LinkedQueue<>();
    private final LinkedStack<Cancion> historial = new LinkedStack<>();
    private Cancion actual;
    // En que cancion de la lista vamos. Puede ser distinta de "actual" si esta sonando algo de la cola.
    private Cancion posicionEnLista;
    private Playlist playlist;

    public void cargarPlaylist(Playlist playlist, List<Cancion> canciones) {
        this.playlist = playlist;
        lista.clear();
        for (Cancion c : canciones) {
            lista.insertLast(c);
        }

        posicionEnLista = lista.getFirst();
        if (posicionEnLista != null) {
            cambiarA(posicionEnLista);
        }
    }

    public void reproducir(Cancion cancion) {
        cambiarA(cancion);
        if (lista.contains(cancion)) {
            posicionEnLista = cancion;
        }
    }

    public void agregarACola(Cancion cancion) {
        cola.enqueue(cancion);
    }

    public void siguiente() {
        Cancion nueva;
        if (!cola.isEmpty()) {
            nueva = cola.dequeue();
        } else {
            nueva = posicionEnLista == null ? lista.getFirst() : lista.getNextOf(posicionEnLista);
            if (nueva != null) {
                posicionEnLista = nueva;
            }
        }

        if (nueva != null) {
            cambiarA(nueva);
        }
    }

    public void anterior() {
        if (posicionEnLista == null) {
            return;
        }
        Cancion nueva = lista.getPrevOf(posicionEnLista);
        if (nueva != null) {
            posicionEnLista = nueva;
            cambiarA(nueva);
        }
    }

    public void regresar() {
        if (historial.isEmpty()) {
            return;
        }
        actual = historial.pop();
        if (lista.contains(actual)) {
            posicionEnLista = actual;
        }
    }

    // Si el usuario modifica la playlist que esta sonando, la lista doble tambien se actualiza.
    public void cancionAgregada(int playlistId, Cancion cancion) {
        if (playlist != null && playlist.id() == playlistId) {
            lista.insertLast(cancion);
        }
    }

    public void cancionQuitada(int playlistId, Cancion cancion) {
        if (playlist == null || playlist.id() != playlistId) {
            return;
        }
        // Si quitamos la cancion donde vamos, nos movemos a la anterior
        // para que "siguiente" lleve a la que seguia.
        if (cancion.equals(posicionEnLista)) {
            posicionEnLista = lista.getPrevOf(cancion);
        }
        lista.remove(cancion);
    }

    private void cambiarA(Cancion nueva) {
        if (actual != null) {
            historial.push(actual);
        }
        actual = nueva;
    }

    // Los getters son los que Spring convierte a JSON en la respuesta.

    public Cancion getActual() {
        return actual;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public List<Cancion> getCanciones() {
        return lista.toList();
    }

    public Cancion getPosicionEnLista() {
        return posicionEnLista;
    }

    public List<Cancion> getCola() {
        return cola.toList();
    }

    public List<Cancion> getHistorial() {
        return historial.toList();
    }
}
