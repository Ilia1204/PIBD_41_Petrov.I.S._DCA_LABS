package ru.ulstu.sortinglib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParallelMergeSortTest {

    private static final Comparator<Integer> ASC = Comparator.naturalOrder();

    @Test
    void sortsExampleFromAssignment() {
        List<Integer> data = List.of(3, 2, 6, 9, 2, 3, 5, 8, 2, 1, 11);

        SortResult<Integer> result = ParallelMergeSort.sort(data, 5, ASC);

        assertEquals(List.of(1, 2, 2, 2, 3, 3, 5, 6, 8, 9, 11), result.sorted());
        assertEquals(5, result.threadsUsed());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 4, 5, 8, 16 })
    void sortsRandomDataWithDifferentThreadCounts(int threads) {
        List<Integer> data = randomData(500);
        List<Integer> expected = new ArrayList<>(data);
        expected.sort(ASC);

        SortResult<Integer> result = ParallelMergeSort.sort(data, threads, ASC);

        assertEquals(expected, result.sorted());
        assertTrue(result.threadsUsed() <= threads);
    }

    @Test
    void clampsThreadsToDataSize() {
        List<Integer> data = List.of(3, 1, 2);

        SortResult<Integer> result = ParallelMergeSort.sort(data, 100, ASC);

        assertEquals(List.of(1, 2, 3), result.sorted());
        assertEquals(3, result.threadsUsed());
    }

    @Test
    void handlesEmptyList() {
        SortResult<Integer> result = ParallelMergeSort.sort(List.of(), 4, ASC);

        assertEquals(List.of(), result.sorted());
        assertEquals(0, result.threadsUsed());
    }

    @Test
    void handlesSingleElement() {
        SortResult<Integer> result = ParallelMergeSort.sort(List.of(42), 4, ASC);

        assertEquals(List.of(42), result.sorted());
    }

    @Test
    void rejectsNonPositiveThreadCount() {
        assertThrows(IllegalArgumentException.class, () -> ParallelMergeSort.sort(List.of(1, 2), 0, ASC));
    }

    private static List<Integer> randomData(int size) {
        Random random = new Random(42);
        List<Integer> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(random.nextInt(10_000));
        }
        return data;
    }
}
