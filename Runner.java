import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Runner {
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
  private boolean force = false;
  private void benchmarkArrays(Sorter[] sorters, int size) {
    int[] data = new int[size];
    for (int i = 0; i < size; i += 1) {
      data[i] = this.random.nextInt();
    }

    int[] random = data.clone();

    int[] sorted = data.clone();
    Arrays.parallelSort(sorted);

    int[] almostSorted = data.clone();
    int sizeHalf = size / 2;
    for (int i = 0; i < size;) {
      int swapWith = sizeHalf + this.random.nextInt(sizeHalf);
      int tmp = almostSorted[i];
      almostSorted[i] = almostSorted[swapWith];
      almostSorted[swapWith] = tmp;
      i += swapWith;
    }

    int[] reverse = sorted.clone();
    for (int i = 0; i < size / 2; i += 1) {
      int tmp = reverse[i];
      reverse[i] = reverse[size - i - 1];
      reverse[size - i - 1] = tmp;
    }

    for (Sorter sorter : sorters) {
      String name = sorterNames.get(sorter);
      if (sorter instanceof QuadraticSort && size > 100_000 && ! force) {
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

  private void printCsvHeader() {
    System.out.println("method,size,result_sorted,result_random," +
      "result_almost_sorted,result_reverse");
  }

  private HashMap<Sorter, String> sorterNames;
  private static final int[] ARRAY_SIZES =
    {20, 100, 10_000, 100_000, 1_000_000};

  private int runAll() {
    double[] results = new double[4];
    SquareRootSort sqrt = new SquareRootSort();
    InsertionSort insertion = new InsertionSort();
    MergeSort merge = new MergeSort();
    Quicksort2 quick2 = new Quicksort2();
    ParallelMergeSort parallelSqrt = new ParallelMergeSort(sqrt);
    ParallelMergeSort parallelMerge = new ParallelMergeSort(merge);
    Samplesort sampleSqrt = new Samplesort(sqrt);
    Samplesort sampleMerge = new Samplesort(merge);

    Sorter[] sorters = {
      sqrt, insertion,
      merge, quick2,
      parallelSqrt, parallelMerge,
      sampleSqrt, sampleMerge
    };

    sorterNames = new HashMap<>(3);
    sorterNames.put(sqrt, "sqrt");
    sorterNames.put(insertion, "insertion");
    sorterNames.put(merge, "merge");
    sorterNames.put(quick2, "quicksort2");
    sorterNames.put(parallelSqrt, "parallel_sqrt");
    sorterNames.put(parallelMerge, "parallel_merge");
    sorterNames.put(sampleSqrt, "sample_sqrt");
    sorterNames.put(sampleMerge, "sample_merge");

    printCsvHeader();
    for (int size : ARRAY_SIZES) {
      benchmarkArrays(sorters, size);
    }

    for (Sorter sorter : sorters) {
      if (sorter instanceof AutoCloseable) {
        try {
          ((AutoCloseable) sorter).close();
        } catch (Exception exc) {
          throw new RuntimeException("Closing sorter failed", exc);
        }
      }
    }

    return 0;
  }

  private Sorter instantiate(String sorterName) {
    Class<? extends Sorter> sorterClass;
    try {
      sorterClass = Class.forName(sorterName).asSubclass(Sorter.class);
    } catch (Exception exc) {
      throw new RuntimeException(
        String.format("%s is not a known sorter class.", sorterName), exc);
    }

    Sorter sorter;
    try {
      sorter = sorterClass.getDeclaredConstructor().newInstance();
    } catch (Exception exc) {
      throw new RuntimeException(
        String.format(
          "Could not instantiate %s (needs extra arguments?)", sorterName),
        exc);
    }

    return sorter;
  }
  private int runSome(String[] sorterNames) {
    Sorter[] sorters = new Sorter[sorterNames.length];
    this.sorterNames = new HashMap<>(sorterNames.length);
    for (int i = 0; i < sorterNames.length; i += 1) {
      String sorterName = sorterNames[i];
      Sorter sorter = instantiate(sorterName);
      sorters[i] = sorter;
      this.sorterNames.put(sorter, sorterName);
    }

    force = true;
    printCsvHeader();
    for (int size : ARRAY_SIZES) {
      benchmarkArrays(sorters, size);
    }

    for (Sorter sorter : sorters) {
      if (sorter instanceof AutoCloseable) {
        try {
          ((AutoCloseable) sorter).close();
        } catch (Exception exc) {
          throw new RuntimeException("Closing sorter failed", exc);
        }
      }
    }

    return 0;
  }

  public static void main(String[] args) {
    Runner runner = new Runner();
    int retcode = args.length == 0 ? runner.runAll() : runner.runSome(args);
    System.exit(retcode);
  }
}
