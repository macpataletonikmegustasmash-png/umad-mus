package com.example.umad_music.estructuras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinkedStack<T> {

    // El nodo del tope (el último que entró)
    private Node<T> top;
    // Cantidad de elementos en la pila
    private int size;

    public LinkedStack() {
        top = null;
        size = 0;
    }

    // Agrega un elemento en el tope: el nuevo nodo apunta al que era el tope
    public void push(T data) {
        top = new Node<>(data, top);
        size++;
    }

    // Quita y regresa el elemento del tope
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("La pila está vacía");
        }

        T data = top.getData();
        // El tope baja al siguiente nodo; el de arriba queda sin referencias
        top = top.getNext();
        size--;
        return data;
    }

    // Regresa el elemento del tope sin quitarlo
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("La pila está vacía");
        }
        return top.getData();
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    // Muestra los elementos del fondo al tope, igual que ArrayStack: [a, b, c]
    @Override
    public String toString() {
        Object[] items = new Object[size];
        Node<T> current = top;
        // El tope va al final del arreglo, así que lo llenamos de atrás hacia adelante
        for (int i = size - 1; i >= 0; i--) {
            items[i] = current.getData();
            current = current.getNext();
        }
        return Arrays.toString(items);
    }

    // ----- Método que agregamos para usar la pila en el reproductor -----

    // Copia los datos a una List de Java, del tope al fondo (el más reciente primero)
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        Node<T> current = top;
        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }
        return result;
    }
}
