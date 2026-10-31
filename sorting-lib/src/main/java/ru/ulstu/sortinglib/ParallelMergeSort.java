package ru.ulstu.sortinglib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ParallelMergeSort {

    private static final Logger log = LoggerFactory.getLogger(ParallelMergeSort.class);

    private ParallelMergeSort() {
    }

    public static <T> SortResult<T> sort(List<T> data, int initialThreads, Comparator<T> comparator) {
        if (initialThreads < 1) {
            throw new IllegalArgumentException("число потоков должно быть не меньше 1");
        }
        if (data.isEmpty()) {
            return new SortResult<>(List.of(), 0, 0);
        }

        int threads = Math.min(initialThreads, data.size());
        long start = System.nanoTime();

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<List<T>> chunks = splitIntoChunks(data, threads);
            log.info("разбили {} элементов на {} кусков, сортируем в {} потоков", data.size(), chunks.size(), threads);
            chunks = runStage(pool, chunks, chunk -> sequentialMergeSort(chunk, comparator));

            while (chunks.size() > 1) {
                log.info("сливаем {} кусков в {} потоков", chunks.size(), (chunks.size() + 1) / 2);
                chunks = mergeStage(pool, chunks, comparator);
            }

            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
            return new SortResult<>(chunks.get(0), elapsedMillis, threads);
        } finally {
            pool.shutdown();
        }
    }

    private static <T> List<List<T>> splitIntoChunks(List<T> data, int parts) {
        List<List<T>> chunks = new ArrayList<>(parts);
        int size = data.size() / parts;
        int remainder = data.size() % parts;
        int from = 0;
        for (int i = 0; i < parts; i++) {
            int to = from + size + (i == parts - 1 ? remainder : 0);
            chunks.add(new ArrayList<>(data.subList(from, to)));
            from = to;
        }
        return chunks;
    }

    private static <T> List<List<T>> runStage(ExecutorService pool, List<List<T>> chunks,
            Function<List<T>, List<T>> work) {
        List<Future<List<T>>> futures = new ArrayList<>(chunks.size());
        for (List<T> chunk : chunks) {
            futures.add(pool.submit(() -> work.apply(chunk)));
        }
        return awaitAll(futures);
    }

    private static <T> List<List<T>> mergeStage(ExecutorService pool, List<List<T>> chunks, Comparator<T> comparator) {
        List<Callable<List<T>>> tasks = new ArrayList<>();
        for (int i = 0; i + 1 < chunks.size(); i += 2) {
            List<T> left = chunks.get(i);
            List<T> right = chunks.get(i + 1);
            tasks.add(() -> merge(left, right, comparator));
        }

        List<Future<List<T>>> futures = new ArrayList<>(tasks.size());
        for (Callable<List<T>> task : tasks) {
            futures.add(pool.submit(task));
        }
        List<List<T>> merged = awaitAll(futures);

        if (chunks.size() % 2 != 0) {
            merged.add(chunks.get(chunks.size() - 1));
        }
        return merged;
    }

    private static <T> List<List<T>> awaitAll(List<Future<List<T>>> futures) {
        List<List<T>> results = new ArrayList<>(futures.size());
        try {
            for (Future<List<T>> future : futures) {
                results.add(future.get());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new SortingException("сортировка прервана", ex);
        } catch (ExecutionException ex) {
            throw new SortingException("ошибка в потоке сортировки", ex.getCause());
        }
        return results;
    }

    private static <T> List<T> sequentialMergeSort(List<T> list, Comparator<T> comparator) {
        if (list.size() <= 1) {
            return list;
        }
        int middle = list.size() / 2;
        List<T> left = sequentialMergeSort(new ArrayList<>(list.subList(0, middle)), comparator);
        List<T> right = sequentialMergeSort(new ArrayList<>(list.subList(middle, list.size())), comparator);
        return merge(left, right, comparator);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<T> comparator) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        result.addAll(left.subList(i, left.size()));
        result.addAll(right.subList(j, right.size()));
        return result;
    }
}
