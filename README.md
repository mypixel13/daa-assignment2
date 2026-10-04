# DAA Assignment 2 — DynamicArray / MyLinkedList / MinHeap

## Структура

```
src/main/java/ds/
  IntList.java         — общий интерфейс для DynamicArray и MyLinkedList (один бенчмарк на оба)
  DynamicArray.java      — массив с ростом x2
  MyLinkedList.java      — односвязный список (head + tail указатели)
  MinHeap.java            — min-куча на массиве
src/main/java/metrics/
  Metrics.java            — счётчики steps/moves/comparisons
src/main/java/bench/
  Benchmark.java           — гоняет W1-W4 на n=100/1000/10000/100000, 5 прогонов, медиана
src/test/java/ds/
  DynamicArrayTest.java
  MyLinkedListTest.java
  MinHeapTest.java
results/
  results.csv
  plots/
```


## Что дальше

- Графики по `results.csv` уже лежат в `results/plots/`
- `REPORT.md` — таблица сложностей, 2 доказательства loop invariant, разбор графиков, discussion
- Git: ветки `feature/array`, `feature/list`, `feature/heap`, `feature/metrics`, смёржить в `main`, тег `v1.0`
