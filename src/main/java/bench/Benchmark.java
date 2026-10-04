package bench;

import ds.DynamicArray;
import ds.IntList;
import ds.MinHeap;
import ds.MyLinkedList;
import metrics.Metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Benchmark {

    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int RUNS = 5;

    public static void main(String[] args) throws IOException {
        new java.io.File("results").mkdirs();
        FileWriter csv = new FileWriter("results/results.csv");
        csv.write("workload,variant,structure,n,time_ms,steps,moves,comparisons\n");

        for (int n : SIZES) {
            runW1(n, csv);
            runW2(n, csv);
            runW3(n, "head", csv);
            runW3(n, "middle", csv);
            runW4(n, csv);
        }

        csv.close();
        System.out.println("done, results are in results/results.csv");
    }

    // W1 - random access: fill the structure with n values, then do 10 000 get(index) calls
    private static void runW1(int n, FileWriter csv) throws IOException {
        bench("W1", "-", "DynamicArray", n, csv, () -> {
            DynamicArray a = fillArray(n);
            Metrics m = new Metrics();
            Random rnd = new Random(7);
            for (int i = 0; i < 10_000; i++) {
                a.get(rnd.nextInt(n), m);
            }
            return m;
        });

        bench("W1", "-", "MyLinkedList", n, csv, () -> {
            MyLinkedList l = fillList(n);
            Metrics m = new Metrics();
            Random rnd = new Random(7);
            for (int i = 0; i < 10_000; i++) {
                l.get(rnd.nextInt(n), m);
            }
            return m;
        });
    }

    // W2 - search: 1000 contains() calls, half values that exist in the data, half that don't
    private static void runW2(int n, FileWriter csv) throws IOException {
        bench("W2", "-", "DynamicArray", n, csv, () -> {
            int[] data = makeData(n);
            DynamicArray a = new DynamicArray();
            Metrics fillMetrics = new Metrics();
            for (int v : data) a.add(v, fillMetrics);

            Metrics m = new Metrics();
            for (int x : searchQueries(data)) {
                a.contains(x, m);
            }
            return m;
        });

        bench("W2", "-", "MyLinkedList", n, csv, () -> {
            int[] data = makeData(n);
            MyLinkedList l = new MyLinkedList();
            Metrics fillMetrics = new Metrics();
            for (int v : data) l.add(v, fillMetrics);

            Metrics m = new Metrics();
            for (int x : searchQueries(data)) {
                l.contains(x, m);
            }
            return m;
        });
    }

    // W3 - insert & remove: 1000 of each, either always at index 0 or always near the middle
    private static void runW3(int n, String variant, FileWriter csv) throws IOException {
        bench("W3", variant, "DynamicArray", n, csv, () -> {
            DynamicArray a = fillArray(n);
            Metrics m = new Metrics();
            insertRemove(a, variant, m);
            return m;
        });

        bench("W3", variant, "MyLinkedList", n, csv, () -> {
            MyLinkedList l = fillList(n);
            Metrics m = new Metrics();
            insertRemove(l, variant, m);
            return m;
        });
    }

    private static void insertRemove(IntList list, String variant, Metrics m) {
        for (int i = 0; i < 1000; i++) {
            int index = variant.equals("head") ? 0 : list.size() / 2;
            list.add(index, i, m);
        }
        for (int i = 0; i < 1000; i++) {
            int index = variant.equals("head") ? 0 : list.size() / 2;
            list.remove(index, m);
        }
    }

    // W4 - priority processing: insert n values into the heap, extract them all, check order
    private static void runW4(int n, FileWriter csv) throws IOException {
        bench("W4", "-", "MinHeap", n, csv, () -> {
            int[] data = makeData(n);
            MinHeap heap = new MinHeap();
            Metrics m = new Metrics();
            for (int v : data) heap.insert(v, m);

            int prev = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                int x = heap.extractMin(m);
                if (x < prev) {
                    throw new IllegalStateException("heap returned an out-of-order value");
                }
                prev = x;
            }
            return m;
        });
    }

    // --- helpers ---

    private interface Task {
        Metrics run();
    }

    private static void bench(String workload, String variant, String structure, int n,
                               FileWriter csv, Task task) throws IOException {
        task.run(); // warm-up run, thrown away so JIT has a chance to kick in

        long[] times = new long[RUNS];
        Metrics lastMetrics = null;

        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            Metrics m = task.run();
            long end = System.nanoTime();
            times[run] = end - start;
            lastMetrics = m; // the operation counts barely change between runs, last one is fine
        }

        Arrays.sort(times);
        double medianMs = times[RUNS / 2] / 1_000_000.0;

        csv.write(String.format(Locale.US, "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload, variant, structure, n, medianMs,
                lastMetrics.steps, lastMetrics.moves, lastMetrics.comparisons));

        System.out.printf("%-4s %-7s %-13s n=%-7d %.3f ms%n", workload, variant, structure, n, medianMs);
    }

    // same seed every time -> DynamicArray and MyLinkedList get identical data to work with
    private static int[] makeData(int n) {
        Random rnd = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rnd.nextInt(1_000_000);
        }
        return data;
    }

    private static DynamicArray fillArray(int n) {
        int[] data = makeData(n);
        DynamicArray a = new DynamicArray();
        Metrics fillMetrics = new Metrics(); // filling itself isn't what we're measuring here
        for (int v : data) a.add(v, fillMetrics);
        return a;
    }

    private static MyLinkedList fillList(int n) {
        int[] data = makeData(n);
        MyLinkedList l = new MyLinkedList();
        Metrics fillMetrics = new Metrics();
        for (int v : data) l.add(v, fillMetrics);
        return l;
    }

    // half the queries are values we know are in the data, half are negative
    // numbers that can never be in there (data is always non-negative)
    private static int[] searchQueries(int[] data) {
        Random rnd = new Random(13);
        int[] queries = new int[1000];
        for (int i = 0; i < 1000; i++) {
            if (i % 2 == 0 && data.length > 0) {
                queries[i] = data[rnd.nextInt(data.length)];
            } else {
                queries[i] = -1 - rnd.nextInt(1_000_000);
            }
        }
        return queries;
    }
}
