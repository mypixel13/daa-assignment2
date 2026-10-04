package ds;

import metrics.Metrics;

public class MinHeap {

    private int[] data;
    private int size;

    public MinHeap() {
        this(16);
    }

    public MinHeap(int initialCapacity) {
        data = new int[Math.max(initialCapacity, 1)];
        size = 0;
    }

    public void insert(int x, Metrics metrics) {
        ensureCapacity(metrics);
        data[size] = x;
        metrics.move();
        size++;
        bubbleUp(size - 1, metrics);
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }
        return data[0];
    }

    public int extractMin(Metrics metrics) {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }
        int min = data[0];
        size--;
        data[0] = data[size]; // move the last element to the root, then fix it up
        metrics.move();
        bubbleDown(0, metrics);
        return min;
    }

    public int size() {
        return size;
    }

    private void bubbleUp(int index, Metrics metrics) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            metrics.step();
            metrics.compare();
            if (data[parent] <= data[index]) {
                break; // parent is already smaller, heap property holds, we're done
            }
            swap(index, parent, metrics);
            index = parent;
        }
    }

    private void bubbleDown(int index, Metrics metrics) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size) {
                metrics.step();
                metrics.compare();
                if (data[left] < data[smallest]) {
                    smallest = left;
                }
            }
            if (right < size) {
                metrics.step();
                metrics.compare();
                if (data[right] < data[smallest]) {
                    smallest = right;
                }
            }

            if (smallest == index) {
                break; // both children (if any) are bigger, we're in the right spot
            }
            swap(index, smallest, metrics);
            index = smallest;
        }
    }

    private void swap(int i, int j, Metrics metrics) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
        metrics.move();
        metrics.move();
    }

    private void ensureCapacity(Metrics metrics) {
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = data[i];
                metrics.move();
            }
            data = bigger;
        }
    }
}
