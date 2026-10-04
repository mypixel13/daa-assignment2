package ds;

import metrics.Metrics;

public class DynamicArray implements IntList {

    private int[] data;
    private int size;

    public DynamicArray() {
        this(10);
    }

    public DynamicArray(int initialCapacity) {
        data = new int[Math.max(initialCapacity, 1)];
        size = 0;
    }

    @Override
    public void add(int x, Metrics metrics) {
        ensureCapacity(metrics);
        data[size] = x;
        metrics.move(); // writing the new element in is one move
        size++;
    }

    @Override
    public void add(int index, int x, Metrics metrics) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        ensureCapacity(metrics);

        // push everything from index onward one slot to the right to make room
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            metrics.move();
        }
        data[index] = x;
        metrics.move();
        size++;
    }

    @Override
    public int remove(int index, Metrics metrics) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        metrics.step();
        int removed = data[index];

        // pull everything after index one slot to the left to close the gap
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            metrics.move();
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index, Metrics metrics) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        metrics.step();
        return data[index];
    }

    @Override
    public boolean contains(int x, Metrics metrics) {
        for (int i = 0; i < size; i++) {
            metrics.step();
            metrics.compare();
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    private void ensureCapacity(Metrics metrics) {
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = data[i];
                metrics.move(); // copying into the bigger array during growth
            }
            data = bigger;
        }
    }
}
