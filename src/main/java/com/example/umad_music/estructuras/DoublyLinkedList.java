package com.example.umad_music.estructuras;

import java.util.ArrayList;
import java.util.List;

public class DoublyLinkedList<T> {
    // Es el inicio de la lista
    private DoublyNode<T> head;
    // Es el final de la lista
    private DoublyNode<T> tail;
    // Cantidad de nodos en la lista
    private int size;

    public DoublyLinkedList() {
        // Como es una lista vacía, no hay primer ni último nodo
        head = null;
        tail = null;
        size = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int getSize() {
        return size;
    }

    // Recorre la lista de inicio a fin y muestra sus datos
    public void traverse() {
        // Variable de trabajo LOCAL para empezar en el primer nodo
        DoublyNode<T> current = head;

        // Recorremos hasta pasar el último nodo
        while (current != null) {
            System.out.print(current + ", ");
            // Avanzamos current al siguiente nodo
            current = current.getNext();
        }

        System.out.println();
    }

    // Recorre la lista de fin a inicio (esto es lo nuevo de la doble)
    public void traverseBackward() {
        // Variable de trabajo LOCAL para empezar en el último nodo
        DoublyNode<T> current = tail;

        // Recorremos hacia atrás hasta pasar el primer nodo
        while (current != null) {
            System.out.print(current + ", ");
            // Retrocedemos current al nodo anterior
            current = current.getPrev();
        }

        System.out.println();
    }

    // Insertar un nodo al inicio
    public void insertFirst(T data) {
        DoublyNode<T> newNode = new DoublyNode<>(data);

        // Si la lista está vacía, el nuevo nodo es head y tail a la vez
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            // El nuevo nodo apunta hacia adelante al que antes era el primero
            newNode.setNext(head);
            // El que antes era el primero ahora apunta hacia atrás al nuevo
            head.setPrev(newNode);
            // Ahora el nuevo nodo es el primero
            head = newNode;
        }
        size++;
    }

    // Insertar un nodo al final
    public void insertLast(T data) {
        DoublyNode<T> newNode = new DoublyNode<>(data);

        // Si la lista está vacía, el nuevo nodo es head y tail a la vez
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            // Ya no hay que recorrer: tail nos da el último nodo directo
            tail.setNext(newNode);   // El último nodo ahora apunta al nuevo
            newNode.setPrev(tail);   // El nuevo apunta hacia atrás al que era último
            tail = newNode;          // Ahora el nuevo nodo es el último
        }
        size++;
    }

    // Busca un dato en la lista
    public boolean contains(T data) {
        DoublyNode<T> current = head;

        while (current != null) {
            // Comparamos objetos con equals, no con ==
            if (current.getData().equals(data)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    // Eliminamos la primera aparición del dato y regresa true si lo encuentra.
    public boolean remove(T data) {
        // Buscamos el nodo que tiene el dato
        // Ya no necesitamos "previous" porque cada nodo conoce a su anterior
        DoublyNode<T> current = head;
        while (current != null && !current.getData().equals(data)) {
            current = current.getNext();
        }

        // Si current llegó a null significa que no estaba el dato (o la lista estaba vacía)
        if (current == null) {
            return false;
        }

        // Reconectamos el lado izquierdo
        if (current.getPrev() == null) {
            // current era el head, así que el siguiente pasa a ser el primero
            head = current.getNext();
        } else {
            // El anterior ahora apunta al que venía después de current
            current.getPrev().setNext(current.getNext());
        }

        // Reconectamos el lado derecho
        if (current.getNext() == null) {
            // current era el tail, así que el anterior pasa a ser el último
            tail = current.getPrev();
        } else {
            // El siguiente ahora apunta hacia atrás al que venía antes de current
            current.getNext().setPrev(current.getPrev());
        }

        size--;
        return true;
    }

    // Vacía la lista
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // ----- Métodos que agregamos para usar la lista en el reproductor -----

    // Regresa el primer dato, o null si la lista está vacía
    public T getFirst() {
        return isEmpty() ? null : head.getData();
    }

    // Regresa el dato que va después de "data", o null si es el último o no está
    public T getNextOf(T data) {
        DoublyNode<T> node = findNode(data);
        if (node == null || node.getNext() == null) {
            return null;
        }
        return node.getNext().getData();
    }

    // Regresa el dato que va antes de "data", o null si es el primero o no está
    public T getPrevOf(T data) {
        DoublyNode<T> node = findNode(data);
        if (node == null || node.getPrev() == null) {
            return null;
        }
        return node.getPrev().getData();
    }

    // Copia los datos a una List de Java para poder regresarlos como JSON
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        DoublyNode<T> current = head;
        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }
        return result;
    }

    private DoublyNode<T> findNode(T data) {
        DoublyNode<T> current = head;
        while (current != null && !current.getData().equals(data)) {
            current = current.getNext();
        }
        return current;
    }
}
