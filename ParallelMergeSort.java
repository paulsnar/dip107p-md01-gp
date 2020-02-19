import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

class ParallelMergeSort implements Sorter {
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
    ArrayWindow.mergePartitionsInto(partitions, result);
    return result;
  }

  void quit() {
    for (WorkerThread thread : workers) {
      thread.input.add(ArrayWindow.EMPTY);
    }
  }
}
