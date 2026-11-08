package com.example.umad_music.estructuras;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstructurasTest {

    @Test
    void listaDobleNavegaHaciaAmbosLados() {
        DoublyLinkedList<String> lista = new DoublyLinkedList<>();
        lista.insertLast("B");
        lista.insertLast("C");
        lista.insertFirst("A");

        assertEquals("A", lista.getFirst());
        assertEquals("B", lista.getNextOf("A"));
        assertEquals("C", lista.getNextOf("B"));
        assertNull(lista.getNextOf("C"));
        assertEquals("B", lista.getPrevOf("C"));
        assertNull(lista.getPrevOf("A"));
    }

    @Test
    void listaDobleEliminaYReconectaNodos() {
        DoublyLinkedList<String> lista = new DoublyLinkedList<>();
        lista.insertLast("A");
        lista.insertLast("B");
        lista.insertLast("C");

        assertTrue(lista.remove("B"));
        assertEquals(List.of("A", "C"), lista.toList());
        assertEquals("C", lista.getNextOf("A"));
        assertEquals("A", lista.getPrevOf("C"));

        assertTrue(lista.remove("A"));
        assertTrue(lista.remove("C"));
        assertTrue(lista.isEmpty());
        assertFalse(lista.remove("X"));
    }

    @Test
    void pilaEsLifo() {
        LinkedStack<Integer> pila = new LinkedStack<>();
        pila.push(1);
        pila.push(2);
        pila.push(3);

        assertEquals("[1, 2, 3]", pila.toString());
        assertEquals(List.of(3, 2, 1), pila.toList());
        assertEquals(3, pila.pop());
        assertEquals(2, pila.peek());
        assertEquals(2, pila.size());
    }

    @Test
    void colaEsFifo() {
        LinkedQueue<Integer> cola = new LinkedQueue<>();
        cola.enqueue(1);
        cola.enqueue(2);
        cola.enqueue(3);

        assertEquals("[1, 2, 3]", cola.toString());
        assertEquals(1, cola.dequeue());
        assertEquals(2, cola.dequeue());
        assertEquals(3, cola.dequeue());
        assertTrue(cola.isEmpty());
        assertThrows(RuntimeException.class, cola::dequeue);
    }
}
