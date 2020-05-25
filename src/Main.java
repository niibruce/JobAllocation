import java.util.ArrayList;

public class Main {

  public static void main(String[] args) {

    singleRun(args, "F", true);
     //singleRun(args, args[1], true);
    // compoundRun(args, false);

  }

  public static void compoundRun(String[] args, boolean debug) {
    ArrayList<String> options = new ArrayList<>();
    options.add("F");
    options.add("B");
    options.add("CO");
    options.add("MO");
    options.add("IO");

    for (String option : options) {
      // Problem problem1_info = readProblem("problem2.json");
      ProblemReader pr = new ProblemReader(args[0]);
      // Problem problem1 = problem1_info.createInfrastructure();
      Problem problem1 = pr.createInfrastructure();

      Solver s = new Solver(problem1, option);
      s.solve(debug);
      System.out.println("End of allocation");
    }
  }

  public static void singleRun(String[] args, String algorithm, boolean debug) {
    // write your code here
    // Problem problem1_info = readProblem("problem2.json");
    ProblemReader pr = new ProblemReader(args[0]);
    // Problem problem1 = problem1_info.createInfrastructure();
    Problem problem1 = pr.createInfrastructure();

    Solver s = new Solver(problem1, algorithm);
    s.solve(debug);
    System.out.println("End of allocation");
  }
}
