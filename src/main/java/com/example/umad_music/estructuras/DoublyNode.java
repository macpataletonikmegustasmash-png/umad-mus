package com.example.umad_music.estructuras;

public class DoublyNode<T> {
    // El dato o datos que guarda el nodo
    private T data;
    // Referencia al nodo siguiente (empieza en null por defecto)
    private DoublyNode<T> next;
    // Referencia al nodo anterior (empieza en null por defecto)
    private DoublyNode<T> prev;

    public DoublyNode(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    DoublyNode<T> getNext() {
        return next;
    }

    void setNext(DoublyNode<T> next) {
        this.next = next;
    }

    DoublyNode<T> getPrev() {
        return prev;
    }

    void setPrev(DoublyNode<T> prev) {
        this.prev = prev;
    }

    @Override
    public String toString() {
        return "[" + data + "]";
    }
}
