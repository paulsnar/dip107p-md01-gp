class CocktailShakerSort implements Sorter, QuadraticSort {
  private void swap(int[] array, int a, int b) {
    int tmp = array[a];
    array[a] = array[b];
    array[b] = tmp;
  }

  public int[] sort(int[] array) {
    int bottom = 0, top = array.length;
    if (top - bottom == 1) {
      return array;
    }
    while (bottom < top) {
      int max = array[bottom], maxI = bottom;
      for (int i = bottom + 1; i < top; i += 1) {
        if (max < array[i]) {
          max = array[i];
          maxI = i;
        }
      }
      swap(array, top - 1, maxI);
      top -= 1;

      int min = array[top - 1], minI = top - 1;
      for (int i = top - 2; i >= bottom; i -= 1) {
        if (min > array[i]) {
          min = array[i];
          minI = i;
        }
      }
      bottom += 1;
    }
    return array;
  }

  public ArrayWindow sort(ArrayWindow window) {
    return new ArrayWindow(sort(window.getArray()));
  }
}
