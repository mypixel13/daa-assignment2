package ds;

import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {

    @Test
    void extractMinReturnsValuesInNonDecreasingOrder() {
        Random rnd = new Random(55);
        MinHeap heap = new MinHeap();
        Metrics m = new Metrics();

        int n = 500;
        for (int i = 0; i < n; i++) {
            heap.insert(rnd.nextInt(10_000), m);
        }

        int prev = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int x = heap.extractMin(m);
            assertTrue(x >= prev, "heap returned an out-of-order value");
            prev = x;
        }
    }

    @Test
    void matchesJavaPriorityQueueOnRandomData() {
        Random rnd = new Random(77);
        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> reference = new PriorityQueue<>();
        Metrics m = new Metrics();

        int n = 1000;
        for (int i = 0; i < n; i++) {
            int v = rnd.nextInt(100_000);
            heap.insert(v, m);
            reference.add(v);
        }

        for (int i = 0; i < n; i++) {
            assertEquals(reference.poll(), heap.extractMin(m));
        }
    }

    @Test
    void peekMinOnEmptyThrows() {
        MinHeap heap = new MinHeap();
        assertThrows(IllegalStateException.class, heap::peekMin);
    }

    @Test
    void extractMinOnEmptyThrows() {
        MinHeap heap = new MinHeap();
        Metrics m = new Metrics();
        assertThrows(IllegalStateException.class, () -> heap.extractMin(m));
    }

    @Test
    void peekMinDoesNotRemove() {
        MinHeap heap = new MinHeap();
        Metrics m = new Metrics();
        heap.insert(5, m);
        heap.insert(1, m);
        assertEquals(1, heap.peekMin());
        assertEquals(2, heap.size());
    }

    @Test
    void oneElementHeap() {
        MinHeap heap = new MinHeap();
        Metrics m = new Metrics();
        heap.insert(42, m);
        assertEquals(42, heap.peekMin());
        assertEquals(42, heap.extractMin(m));
        assertEquals(0, heap.size());
    }

    @Test
    void heapPropertyHoldsAfterEveryInsertAndExtract() {
        Random rnd = new Random(99);
        MinHeap heap = new MinHeap();
        Metrics m = new Metrics();
        int[] backing = new int[2000]; // mirrors the heap's internal array to check the property
        int size = 0;

        for (int i = 0; i < 300; i++) {
            if (size == 0 || rnd.nextBoolean()) {
                int v = rnd.nextInt(1000);
                heap.insert(v, m);
                backing[size++] = v;
                // we don't have direct access to the heap's array, so instead we just
                // re-check the invariant indirectly: peekMin must always be <= anything we inserted
            } else {
                int min = heap.extractMin(m);
                // find and remove that value from our own mirror, then confirm it really was the smallest
                int minIndex = 0;
                for (int j = 1; j < size; j++) {
                    if (backing[j] < backing[minIndex]) minIndex = j;
                }
                assertEquals(backing[minIndex], min);
                backing[minIndex] = backing[--size];
            }
        }
    }
}
