abstract class BenchmarkTools {
  interface CreatorInvokable<T> {
    public T invoke();
  }
  interface PassthroughInvokable<T> {
    public T invoke(T t);
  }
  interface VoidInvokable<T> {
    public void invoke(T t);
  }

  private static final int INVOKATION_COUNT = 100;
  public static <T> double stopwatch(
    CreatorInvokable<T> init,
    PassthroughInvokable<T> benchmark,
    VoidInvokable<T> teardown
  ) {
    double sum = 0;
    int i;
    for (i = 0; i < INVOKATION_COUNT; i += 1) {
      T value = init.invoke();
      long start = System.nanoTime();
      value = benchmark.invoke(value);
      long end = System.nanoTime();
      teardown.invoke(value);
      sum += (end - start) / 1e9d;

      if (sum > 30) {
        break;
      }
    }
    return sum / (i + 1);
  }
}
