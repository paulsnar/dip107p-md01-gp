public class Runner {
  private int run(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: java Runner SortClassName");
      return 1;
    }

    Class<? extends Sorter> sorterClass;
    try {
      sorterClass = Class.forName(args[0]).asSubclass(Sorter.class);
    } catch (Exception exc) {
      System.out.printf("Error: %s is not a known Sorter class.\n", args[0]);
      return 1;
    }

    Sorter sorter;
    try {
      sorter = sorterClass.getDeclaredConstructor().newInstance();
    } catch (Exception exc) {
      System.out.printf("Error: Could not instantiate %s\n", args[0]);
      exc.printStackTrace();
      return 1;
    }

    return 0;
  }

  public static void main(String[] args) {
    System.exit(new Runner().run(args));
  }
}
