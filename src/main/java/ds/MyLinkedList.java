package ds;

import metrics.Metrics;

public class MyLinkedList implements IntList {

    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    @Override
    public void add(int x, Metrics metrics) {
        Node node = new Node(x);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            metrics.move(); // linking the old tail to the new node
            tail = node;
        }
        size++;
    }

    @Override
    public void add(int index, int x, Metrics metrics) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }

        if (index == size) {
            add(x, metrics); // appending at the end, tail pointer makes this O(1)
            return;
        }

        Node node = new Node(x);

        if (index == 0) {
            node.next = head;
            metrics.move();
            head = node;
            if (tail == null) {
                tail = node;
            }
            size++;
            return;
        }

        // walk to the node right before where we want to insert
        Node prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
            metrics.step(); // one hop to the next node
        }

        node.next = prev.next;
        metrics.move();
        prev.next = node;
        metrics.move();
        size++;
    }

    @Override
    public int remove(int index, Metrics metrics) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }

        int removed;
        if (index == 0) {
            removed = head.value;
            metrics.step();
            head = head.next;
            metrics.move();
            if (head == null) {
                tail = null;
            }
        } else {
            Node prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
                metrics.step();
            }
            Node target = prev.next;
            metrics.step();
            removed = target.value;
            prev.next = target.next;
            metrics.move();
            if (target == tail) {
                tail = prev;
            }
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index, Metrics metrics) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        // this is the whole point of the assignment - for the array get() is 1
        // step no matter what, but here we have to walk from the head every time
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.step();
        }
        metrics.step(); // reading the value out of the node we landed on
        return current.value;
    }

    @Override
    public boolean contains(int x, Metrics metrics) {
        Node current = head;
        while (current != null) {
            metrics.step();
            metrics.compare();
            if (current.value == x) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }
}
