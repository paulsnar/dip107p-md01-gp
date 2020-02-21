import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

class Samplesort implements Sorter, AutoCloseable {
  private int BUCKETS = Runtime.getRuntime().availableProcessors();

  private Sorter sorter;
  private WorkerThread[] workers;
  Samplesort(Sorter sorter) {
    this.sorter = sorter;

    workers = new WorkerThread[BUCKETS];
    for (int i = 0; i < BUCKETS; i += 1) {
      WorkerThread worker = new WorkerThread();
      worker.start();
      workers[i] = worker;
    }
  }

  public void close() {
    for (WorkerThread worker : workers) {
      worker.input.add(ArrayWindow.EMPTY);
    }
  }

  private class WorkerThread extends Thread {
    BlockingQueue<ArrayWindow> input = new LinkedBlockingQueue<>();
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

  private Random random = new Random();
  public ArrayWindow sort(ArrayWindow window) {
    int size = window.size();
    int[] tmp = new int[size];

    int[] pivots = new int[BUCKETS - 1];
    for (int i = 0; i < BUCKETS - 1; i += 1) {
      pivots[i] = random.nextInt(size);
    }

    int[] bucketSizes = new int[BUCKETS];
    int lastBucket = BUCKETS - 1;
    elems: for (int i = 0; i < size; i += 1) {
      int value = window.get(i);
      for (int ii = 0; ii < pivots.length; ii += 1) {
        if (ii == 0) {
          if (value < pivots[0]) {
            bucketSizes[0] += 1;
            tmp[i] = 0;
            continue elems;
          }
        } else if (pivots[ii - 1] <= value && value < pivots[ii]) {
          bucketSizes[ii] += 1;
          tmp[i] = ii;
          continue elems;
        }
      }
      bucketSizes[lastBucket] += 1;
      tmp[i] = lastBucket;
    }

    ArrayWindow space = ArrayWindow.allocate(size);
    ArrayWindow[] buckets = new ArrayWindow[BUCKETS];
    int top = 0;
    for (int i = 0; i < buckets.length; i += 1) {
      buckets[i] = space.slice(top, top + bucketSizes[i]);
      top += bucketSizes[i];
    }

    int bucketIndices[] = new int[BUCKETS];
    for (int i = 0; i < size; i += 1) {
      int bucketNum = tmp[i];
      ArrayWindow bucket = buckets[bucketNum];
      bucket.set(bucketIndices[bucketNum], window.get(i));
      bucketIndices[bucketNum] += 1;
    }

    CountDownLatch latch = new CountDownLatch(BUCKETS);
    for (int i = 0; i < BUCKETS; i += 1) {
      WorkerThread worker = workers[i];
      worker.doneSignal = latch;
      worker.input.add(buckets[i]);
    }

    do {
      try {
        latch.await();
      } catch (InterruptedException exc) {
        continue;
      }
    } while (false);
    return space;
  }
}
