class OptimizedBubbleSort implements Sorter, QuadraticSort {
  private void swap(int[] input, int a, int b) {
    int x = input[a];
    input[a] = input[b];
    input[b] = x;
  }

  public int[] sort(int[] array) {
    int top = array.length;
    for (;;) {
      boolean didSwap = false;

      for (int i = 1; i < top; i += 1) {
        if (array[i - 1] > array[i]) {
          swap(array, i - 1, i);
          didSwap = true;
        }
      }

      if ( ! didSwap) {
        break;
      }

      top -= 1;
    }

    return array;
  }

  public ArrayWindow sort(ArrayWindow window) {
    int[] array = window.getArray();
    return new ArrayWindow(sort(array));
  }
}
