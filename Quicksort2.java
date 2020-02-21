import java.util.Random;

class Quicksort2 implements Sorter {
  public int[] sort(int[] array) {
    return sort(new ArrayWindow(array)).getArray();
  }

  private Random random = new Random();
  public ArrayWindow sort(ArrayWindow window) {
    int size = window.size();
    if (size < 2) {
      return window;
    }

    int pivotIndex = random.nextInt(size),
        pivot = window.get(pivotIndex);
    int left = 0, right = window.size() - 1;
    while (left <= right) {
      while (window.get(left) < pivot) left += 1;
      while (window.get(right) > pivot) right -= 1;
      if (left > right) {
        break;
      }
      window.swap(left, right);
      left += 1;
      right -= 1;
    }

    if (right > 0) {
      sort(window.slice(0, right));
    }
    if (left < size - 1) {
      sort(window.slice(left, 0));
    }
    return window;
  }
}
