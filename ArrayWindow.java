class ArrayWindow {
  public static final ArrayWindow EMPTY = new ArrayWindow(null, 0, 0);

  private int[] array;
  private int start, end;
  ArrayWindow(int[] array) {
    this(array, 0, array.length);
  }
  ArrayWindow(int[] array, int start, int end) {
    this.array = array;
    this.start = start;
    this.end = end;
  }

  static ArrayWindow allocate(int size) {
    return new ArrayWindow(new int[size]);
  }

  private int indexToAbsolute(int index) {
    int size = end - start;
    if (index < 0) {
      index += size;
    }

    if (index < 0 || index >= size) {
      throw new ArrayIndexOutOfBoundsException(
        String.format("Index %d out of bounds for window of size %d",
          index, size));
    }

    index += start;
    return index;
  }

  ArrayWindow slice(int start, int end) {
    start = indexToAbsolute(start);
    if (end == 0) {
      end = this.end;
    } else if (end == (this.end - this.start)) {
      end = this.end;
    } else {
      end = indexToAbsolute(end);
    }
    return new ArrayWindow(array, start, end);
  }

  int get(int index) {
    return array[indexToAbsolute(index)];
  }

  void set(int index, int value) {
    array[indexToAbsolute(index)] = value;
  }

  void swap(int indexA, int indexB) {
    int tmp;
    indexA = indexToAbsolute(indexA);
    indexB = indexToAbsolute(indexB);
    tmp = array[indexA];
    array[indexA] = array[indexB];
    array[indexB] = tmp;
  }

  int size() {
    return end - start;
  }

  int[] getArray() {
    if (start == 0 && end == array.length) {
      return array;
    } else {
      int[] window = new int[size()];
      Iterator iterator = iterate();
      int i = 0;
      while (iterator.hasNext()) {
        window[i] = iterator.next();
        i += 1;
      }
      return window;
    }
  }

  class Iterator {
    private int index = start;
    boolean hasNext() {
      return index < end;
    }
    int peek() {
      return array[index];
    }
    int next() {
      try {
        return array[index];
      } finally {
        index += 1;
      }
    }
  }
  Iterator iterate() {
    return new Iterator();
  }

  @Override
  public ArrayWindow clone() {
    int[] copy = new int[size()];
    for (int i = start, ic = 0; i < end; i += 1, ic += 1) {
      copy[ic] = array[i];
    }
    return new ArrayWindow(copy);
  }

  void copyTo(ArrayWindow other) {
    int iThis = start, iOther = other.start;
    while (iThis < end && iOther < other.end) {
      other.array[iOther] = array[iThis];
      iThis += 1;
      iOther += 1;
    }
  }

  ArrayWindow[] partition(int count) {
    int size = this.size();
    int[] partSizes = new int[count];
    int partSize = size / count;
    for (int i = 0; i < count; i += 1) {
      partSizes[i] = partSize;
    }
    if (partSize * count < size) {
      int delta = size - (partSize * count);
      partSizes[count - 1] += delta;
    }

    int start = this.start;
    ArrayWindow[] windows = new ArrayWindow[count];
    for (int i = 0; i < count; i += 1) {
      int part = partSizes[i];
      windows[i] = new ArrayWindow(array, start, start + part);
      start += part;
    }

    return windows;
  }

  static void mergePartitionsInto(
      ArrayWindow[] partitions, ArrayWindow target) {
    int count = partitions.length;

    Iterator[] iterators = new Iterator[count];
    for (int i = 0; i < partitions.length; i += 1) {
      iterators[i] = partitions[i].iterate();
    }

    int ptr = target.start;
    for (;;) {
      int min = Integer.MAX_VALUE, minIndex = -1;
      for (int i = 0; i < count; i += 1) {
        Iterator iterator = iterators[i];
        if (iterator.hasNext()) {
          int val = iterator.peek();
          if (val < min || minIndex == -1) {
            min = val;
            minIndex = i;
          }
        }
      }
      if (minIndex == -1) {
        break;
      }

      target.array[ptr] = min;
      iterators[minIndex].next();
      ptr += 1;
    }
  }
}
