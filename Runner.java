import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Runner {
  enum ArrayType { SORTED, RANDOM, ALMOST_SORTED, REVERSE }

  private static final int INVOCATION_COUNT = 100;
  private double benchmark(Sorter sorter, ArrayWindow window) {
    double timeSum = 0;

    int i = 0;
    boolean didPassAssertion = false;
    for (;;) {
      ArrayWindow workingWindow = window.clone();
      long start = System.nanoTime();
      sorter.sort(workingWindow);
      long end = System.nanoTime();
      if ( ! didPassAssertion) {
        assert isSorted(workingWindow);
        didPassAssertion = true;
      }
      timeSum += (end - start) / 1e9d;

      i += 1;
      if (i >= INVOCATION_COUNT || timeSum > 30d) {
        break;
      }
    }

    return timeSum / i;
  }

  private boolean isSorted(ArrayWindow x) {
    if (x.size() == 0) {
      return true;
    }

    ArrayWindow.Iterator iter = x.iterate();
    int prev = iter.next();
    while (iter.hasNext()) {
      int current = iter.next();
      if (current < prev) {
        return false;
      }
      prev = current;
    }

    return true;
  }

  private Random random = new Random();
  private void benchmarkArrays(Sorter[] sorters, int size) {
    int[] data = new int[size];
    for (int i = 0; i < size; i += 1) {
      data[i] = this.random.nextInt();
    }

    int[] random = data.clone();

    int[] sorted = data.clone();
    Arrays.parallelSort(sorted);

    int[] almostSorted = data.clone();
    for (int i = 0; i < size;) {
      int swapWith = this.random.nextInt(size);
      int tmp = almostSorted[i];
      almostSorted[i] = almostSorted[swapWith];
      almostSorted[swapWith] = tmp;
      i += this.random.nextInt(size - i + 1);
    }

    int[] reverse = sorted.clone();
    for (int i = 0; i < size / 2; i += 1) {
      int tmp = reverse[i];
      reverse[i] = reverse[size - i - 1];
      reverse[size - i - 1] = tmp;
    }

    for (Sorter sorter : sorters) {
      String name = sorterNames.get(sorter);
      if (name.equals("shaker") && size > 100_000) {
        // this becomes untenable :)
        continue;
      }

      // printCsvLine(...)
      System.out.printf("%s,%d,", name, size);
      System.out.printf("%.9f,",
        benchmark(sorter, new ArrayWindow(sorted.clone())));
      System.out.printf("%.9f,",
        benchmark(sorter, new ArrayWindow(random.clone())));
      System.out.printf("%.9f,",
        benchmark(sorter, new ArrayWindow(almostSorted.clone())));
      System.out.printf("%.9f\n",
        benchmark(sorter, new ArrayWindow(reverse.clone())));
    }
  }

  private HashMap<Sorter, String> sorterNames;

  private int run(String[] args) {
    double[] results = new double[4];
    CocktailShakerSort cocktailShaker = new CocktailShakerSort();
    MergeSort merge = new MergeSort();
    InsertionSort insertionSort = new InsertionSort();
    Quicksort quickSort = new Quicksort();
    ParallelMergeSort parallelShaker = new ParallelMergeSort(cocktailShaker);
    ParallelMergeSort parallelMerge = new ParallelMergeSort(merge);
    
    Sorter[] sorters = {cocktailShaker, merge, parallelShaker, parallelMerge,
      insertionSort, quickSort};

    sorterNames = new HashMap<>(3);
    sorterNames.put(cocktailShaker, "shaker");
    sorterNames.put(merge, "merge");
    sorterNames.put(parallelShaker, "parallel_shaker");
    sorterNames.put(parallelMerge, "parallel_merge");
    sorterNames.put(insertionSort, "insertion_sort");
    sorterNames.put(quickSort, "quicksort");

    // printCsvHeader();
    System.out.println("method,size,result_sorted,result_random," +
      "result_almost_sorted,result_reverse");
    int[] sizes = {20, 100, 10_000, 100_000, 1_000_000};
    for (int size : sizes) {
      benchmarkArrays(sorters, size);
    }
    return 0;
  }

  public static void main(String[] args) {
    System.exit(new Runner().run(args));
  }
}
