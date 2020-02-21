class MergeSort implements Sorter {
  private void sort(ArrayWindow window, ArrayWindow scratch) {
    int size = window.size();
    if (size < 2) {
      return;
    }
    int seam = size / 2;
    ArrayWindow
      left = window.slice(0, seam), leftTarget = scratch.slice(0, seam),
      right = window.slice(seam, 0), rightTarget = scratch.slice(seam, 0);
    sort(left, leftTarget);
    sort(right, rightTarget);

    merge(left, right, scratch);
    scratch.copyTo(window);
  }

  private void merge(ArrayWindow leftWin, ArrayWindow rightWin,
      ArrayWindow target) {

    assert target.size() == leftWin.size() + rightWin.size();
    ArrayWindow.Iterator
      left = leftWin.iterate(),
      right = rightWin.iterate();

    for (int iTarget = 0; ; iTarget += 1) {
      if ( ! left.hasNext() && right.hasNext()) {
        target.set(iTarget, right.next());
      } else if ( ! right.hasNext() && left.hasNext()) {
        target.set(iTarget, left.next());
      } else if (left.hasNext() && right.hasNext()) {
        int
          valLeft = left.peek(),
          valRight = right.peek();
        if (valLeft <= valRight) {
          target.set(iTarget, valLeft);
          left.next();
        } else {
          target.set(iTarget, valRight);
          right.next();
        }
      } else {
        break;
      }
    }
  }

  public int[] sort(int[] array) {
    return sort(new ArrayWindow(array)).getArray();
  }

  public ArrayWindow sort(ArrayWindow array) {
    ArrayWindow scratch = ArrayWindow.allocate(array.size());
    sort(array, scratch);
    return array;
  }
}
