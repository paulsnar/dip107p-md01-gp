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

  public static void main(String[] args) {
    int[] array = new int[350_000];
    var rand = new java.util.Random();
    for (int i = 0; i < array.length; i += 1) {
      array[i] = rand.nextInt();
    }

    MergeSort mergeSort = new MergeSort();
    double T_mergeSort = BenchmarkTools.stopwatch(() -> {
      return array.clone();
    }, (_array) -> {
      return mergeSort.sort(_array);
    }, (_array) -> {
      assert isSorted(_array);
    });

    ParallelMergeSort parallelMergeSort = new ParallelMergeSort(mergeSort);
    int[] parallelSortWorking = array.clone();
    double T_parallelSort = BenchmarkTools.stopwatch(() -> {
      return array.clone();
    }, (_array) -> {
      return parallelMergeSort.sort(_array);
    }, (_array) -> {
      assert isSorted(_array);
    });

    System.out.printf("T(merge) = %f s\n", T_mergeSort);
    System.out.printf("T(parallel-merge) = %f s\n", T_parallelSort);

    parallelMergeSort.quit();
  }
}
