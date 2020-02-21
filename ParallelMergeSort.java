import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

class ParallelMergeSort implements Sorter, AutoCloseable {
  private static final int BUCKETS = Runtime.getRuntime().availableProcessors();

  private Sorter sorter;
  private WorkerThread[] workers;

  ParallelMergeSort(Sorter sorter) {
    this.sorter = sorter;

    workers = new WorkerThread[BUCKETS];
    for (int i = 0; i < BUCKETS; i += 1) {
      WorkerThread thread = new WorkerThread();
      workers[i] = thread;
      thread.start();
    }
  }

  public void close() {
    for (WorkerThread thread : workers) {
      thread.input.add(ArrayWindow.EMPTY);
    }
  }

  private class WorkerThread extends Thread {
    BlockingQueue<ArrayWindow> input = new LinkedBlockingQueue<ArrayWindow>();
    CountDownLatch doneSignal = null;

    public void run() {
      for (;;) {
        ArrayWindow window = null;
        try {
          window = input.take();
        } catch (InterruptedException exc) {
          continue;
        }
        if (window == ArrayWindow.EMPTY) {
          break;
        }

        sorter.sort(window);
        if (doneSignal != null) {
          doneSignal.countDown();
          doneSignal = null;
        }
      }
    }
  }

  public int[] sort(int[] array) {
    return sort(new ArrayWindow(array)).getArray();
  }

  public ArrayWindow sort(ArrayWindow window) {
    ArrayWindow[] partitions = window.partition(BUCKETS);

    CountDownLatch latch = new CountDownLatch(BUCKETS);
    for (int i = 0; i < BUCKETS; i += 1) {
      WorkerThread thread = workers[i];
      thread.doneSignal = latch;
      thread.input.add(partitions[i]);
    }

    do {
      try {
        latch.await();
      } catch (InterruptedException exc) {
        continue;
      }
    } while (false);
    ArrayWindow result = ArrayWindow.allocate(window.size());
    mergePartitionsInto(partitions, result);
    return result;
  }

  private static void mergePartitionsInto(
      ArrayWindow[] partitions, ArrayWindow target) {
    int count = partitions.length;

    ArrayWindow.Iterator[] iterators = new ArrayWindow.Iterator[count];
    for (int i = 0; i < partitions.length; i += 1) {
      iterators[i] = partitions[i].iterate();
    }

    int ptr = 0;
    for (;;) {
      int min = Integer.MAX_VALUE, minIndex = -1;
      for (int i = 0; i < count; i += 1) {
        ArrayWindow.Iterator iterator = iterators[i];
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

      target.set(ptr, min);
      iterators[minIndex].next();
      ptr += 1;
    }
  }
}
