package ds;

import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {

    @Test
    void matchesJavaLinkedListOnRandomOperations() {
        Random rnd = new Random(456);
        MyLinkedList actual = new MyLinkedList();
        List<Integer> expected = new LinkedList<>();
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
        MyLinkedList l = new MyLinkedList();
        Metrics m = new Metrics();
        for (int v : new int[]{5, 3, 8, 1}) l.add(v, m);

        assertTrue(l.contains(3, m));
        assertFalse(l.contains(99, m));
    }

    @Test
    void emptyListHasSizeZero() {
        MyLinkedList l = new MyLinkedList();
        assertEquals(0, l.size());
        assertFalse(l.contains(1, new Metrics()));
    }

    @Test
    void oneElementList() {
        MyLinkedList l = new MyLinkedList();
        Metrics m = new Metrics();
        l.add(42, m);
        assertEquals(1, l.size());
        assertEquals(42, l.get(0, m));
    }

    @Test
    void duplicateValuesAreKept() {
        MyLinkedList l = new MyLinkedList();
        Metrics m = new Metrics();
        l.add(7, m);
        l.add(7, m);
        l.add(7, m);
        assertEquals(3, l.size());
        assertTrue(l.contains(7, m));
    }

    @Test
    void firstAndLastIndex() {
        MyLinkedList l = new MyLinkedList();
        Metrics m = new Metrics();
        for (int v : new int[]{10, 20, 30}) l.add(v, m);
        assertEquals(10, l.get(0, m));
        assertEquals(30, l.get(2, m));
    }

    @Test
    void invalidIndexThrows() {
        MyLinkedList l = new MyLinkedList();
        Metrics m = new Metrics();
        l.add(1, m);
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(5, m));
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(-1, m));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(5, m));
        assertThrows(IndexOutOfBoundsException.class, () -> l.add(5, 1, m));
    }

    @Test
    void removingHeadFixesHeadPointer() {
        MyLinkedList l = new MyLinkedList();
        Metrics m = new Metrics();
        for (int v : new int[]{1, 2, 3}) l.add(v, m);
        assertEquals(1, l.remove(0, m));
        assertEquals(2, l.get(0, m));
        assertEquals(2, l.size());
    }

    @Test
    void removingTailFixesTailPointerSoAddStillWorks() {
        MyLinkedList l = new MyLinkedList();
        Metrics m = new Metrics();
        for (int v : new int[]{1, 2, 3}) l.add(v, m);
        l.remove(2, m); // remove the tail
        l.add(99, m);   // this relies on the tail pointer being fixed correctly
        assertEquals(99, l.get(2, m));
    }
}
