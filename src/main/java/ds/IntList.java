package ds;

import metrics.Metrics;

// DynamicArray and MyLinkedList both implement this, so the benchmark can run
// the exact same code against either one without caring which it's holding
public interface IntList {

    void add(int x, Metrics metrics);

    void add(int index, int x, Metrics metrics);

    int remove(int index, Metrics metrics);

    int get(int index, Metrics metrics);

    boolean contains(int x, Metrics metrics);

    int size();
}
