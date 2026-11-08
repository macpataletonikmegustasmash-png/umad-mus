package com.example.umad_music.estructuras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinkedQueue<T> {

    // El primero de la fila (el próximo en salir)
    private Node<T> front;
    // El último de la fila (donde se forman los nuevos)
    private Node<T> rear;
    // Cantidad de elementos en la cola
    private int size;

    public LinkedQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    // Agrega un elemento al final de la fila
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            front = newNode;
        } else {
            // El que era el último ahora apunta al nuevo
            rear.setNext(newNode);
        }
        rear = newNode;
        size++;
    }

    // Quita y regresa el primero de la fila
    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("La cola está vacía");
        }

        T data = front.getData();
        front = front.getNext();
        // Si salió el único, la cola quedó vacía y rear también debe ser null
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    // Regresa el primero de la fila sin quitarlo
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("La cola está vacía");
        }
        return front.getData();
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    // Muestra los elementos del primero al último: [a, b, c]
    @Override
    public String toString() {
        Object[] items = new Object[size];
        Node<T> current = front;
        for (int i = 0; i < size; i++) {
            items[i] = current.getData();
            current = current.getNext();
        }
        return Arrays.toString(items);
    }

    // ----- Método que agregamos para usar la cola en el reproductor -----

    // Copia los datos a una List de Java, del primero al último
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        Node<T> current = front;
        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }
        return result;
    }
}
