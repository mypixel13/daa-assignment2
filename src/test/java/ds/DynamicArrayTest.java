package ds;

import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {

    @Test
    void matchesArrayListOnRandomOperations() {
        Random rnd = new Random(123);
        DynamicArray actual = new DynamicArray();
        List<Integer> expected = new ArrayList<>();
        Metrics m = new Metrics();

        for (int op = 0; op < 2000; op++) {
            int choice = rnd.nextInt(4);
            if (choice == 0 || expected.isEmpty()) {
                int value = rnd.nextInt(1000);
                actual.add(value, m);
                expected.add(value);
            } else if (choice == 1) {
                int index = rnd.nextInt(expected.size() + 1);
                int value = rnd.nextInt(1000);
                actual.add(index, value, m);
                expected.add(index, value);
            } else if (choice == 2) {
                int index = rnd.nextInt(expected.size());
                assertEquals((int) expected.remove(index), actual.remove(index, m));
            } else {
                int index = rnd.nextInt(expected.size());
                assertEquals((int) expected.get(index), actual.get(index, m));
            }
        }

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals((int) expected.get(i), actual.get(i, m));
        }
    }

    @Test
    void containsFindsExistingAndMissingValues() {
        DynamicArray a = new DynamicArray();
        Metrics m = new Metrics();
        for (int v : new int[]{5, 3, 8, 1}) a.add(v, m);

        assertTrue(a.contains(3, m));
        assertFalse(a.contains(99, m));
    }

    @Test
    void emptyArrayHasSizeZero() {
        DynamicArray a = new DynamicArray();
        assertEquals(0, a.size());
        assertFalse(a.contains(1, new Metrics()));
    }

    @Test
    void oneElementArray() {
        DynamicArray a = new DynamicArray();
        Metrics m = new Metrics();
        a.add(42, m);
        assertEquals(1, a.size());
        assertEquals(42, a.get(0, m));
    }

    @Test
    void duplicateValuesAreKept() {
        DynamicArray a = new DynamicArray();
        Metrics m = new Metrics();
        a.add(7, m);
        a.add(7, m);
        a.add(7, m);
        assertEquals(3, a.size());
        assertTrue(a.contains(7, m));
    }

    @Test
    void firstAndLastIndex() {
        DynamicArray a = new DynamicArray();
        Metrics m = new Metrics();
        for (int v : new int[]{10, 20, 30}) a.add(v, m);
        assertEquals(10, a.get(0, m));
        assertEquals(30, a.get(2, m));
    }

    @Test
    void invalidIndexThrows() {
        DynamicArray a = new DynamicArray();
        Metrics m = new Metrics();
        a.add(1, m);
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(5, m));
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(-1, m));
        assertThrows(IndexOutOfBoundsException.class, () -> a.remove(5, m));
        assertThrows(IndexOutOfBoundsException.class, () -> a.add(5, 1, m));
    }

    @Test
    void growsPastInitialCapacity() {
        DynamicArray a = new DynamicArray(2);
        Metrics m = new Metrics();
        for (int i = 0; i < 100; i++) {
            a.add(i, m);
        }
        assertEquals(100, a.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, a.get(i, m));
        }
    }
}
