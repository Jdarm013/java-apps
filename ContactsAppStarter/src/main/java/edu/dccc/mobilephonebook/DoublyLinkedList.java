package edu.dccc.mobilephonebook;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<E> implements Iterable<E>{
    private Node<E> head;
    private Node<E> tail;

    private int size = 0;

    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;

        Node(E data) { this.data = data; }
    }

    // Standard Add (to the tail)
    public void add(E element) {
        Node<E> newNode = new Node<>(element);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public boolean remove(E data) {
        Node<E> current = head;

        while (current != null) {
            if (current.data.equals(data)) {
                // CASE 1: The "Lone Node"
                if (current == head && current == tail) {
                    head = tail = null;
                }
                // CASE 2: The "Front Door"
                else if (current == head) {
                    head = head.next;
                    head.prev = null;
                }
                // CASE 3: The "Back Door"
                else if (current == tail) {
                    tail = tail.prev;
                    tail.next = null;
                }
                // CASE 4: The "Middle Link"
                else {
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                }
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // FORWARD ITERATOR
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;
            @Override
            public boolean hasNext() { return current != null; }
            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    // BACKWARD ITERATOR
    public Iterable<E> backwards() {
        return () -> new Iterator<E>() {
            private Node<E> current = tail;
            @Override
            public boolean hasNext() { return current != null; }
            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E data = current.data;
                current = current.prev;
                return data;
            }
        };
    }

    public boolean contains(E data) {
        Node<E> current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() { return size; }
}