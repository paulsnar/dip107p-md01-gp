public class Test {
  static void print(int[] array) {
    for (int elem : array) {
      System.out.printf("%d ", elem);
    }
    System.out.println();
  }

  static boolean isSorted(int[] array) {
    if (array.length == 0) {
      return true;
    }
    int prev = array[0];
    for (int i = 1; i < array.length; i += 1) {
      if (prev > array[i]) {
        return false;
      }
    }
    return true;
  }

  private static MergeSort mergeSort = new MergeSort();
  private static ParallelMergeSort parallelMergeSort = new ParallelMergeSort(mergeSort);
  static void benchmark(int size) {
    System.out.printf("=== size: %d ===\n", size);
    int[] array = new int[size];
    var rand = new java.util.Random();
    for (int i = 0; i < array.length; i += 1) {
      array[i] = rand.nextInt();
    }

    var cocktailShakerSort = new CocktailShakerSort();
    System.out.print("cocktail-shaker = ");
    double T_cocktailShakerSort = BenchmarkTools.stopwatch(() -> {
      return array.clone();
    }, (_array) -> {
      return cocktailShakerSort.sort(_array);
    }, (_array) -> {
      assert isSorted(_array);
    });
    System.out.printf("%f\n", T_cocktailShakerSort);

    System.out.print("merge = ");
    double T_mergeSort = BenchmarkTools.stopwatch(() -> {
      return array.clone();
    }, (_array) -> {
      return mergeSort.sort(_array);
    }, (_array) -> {
      assert isSorted(_array);
    });
    System.out.printf("%f\n", T_mergeSort);

    System.out.print("merge-parallel = ");
    int[] parallelSortWorking = array.clone();
    double T_parallelSort = BenchmarkTools.stopwatch(() -> {
      return array.clone();
    }, (_array) -> {
      return parallelMergeSort.sort(_array);
    }, (_array) -> {
      assert isSorted(_array);
    });
    System.out.printf("%f\n", T_parallelSort);
  }

  public static void main(String[] args) {
    benchmark(12);
    benchmark(1000);
    benchmark(100000);
    benchmark(1000000);

    parallelMergeSort.quit();
  }
}
