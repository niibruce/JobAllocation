import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import rack.Rack;
import server.Server;
import workload.Workload;
import workload.Workloads;
import workloadComparators.CPUComparator;
import workloadComparators.IOPSComparator;
import workloadComparators.MemoryComparator;

/**
 * The solver class is the entry point of all the allocation algorithms. It is instantiated with a
 * new
 */
public class Solver {
  private final Problem problem;
  DecimalFormat df = new DecimalFormat("####0.000");
  ArrayList<String> unableToAllocate = new ArrayList<>();
  private String algorithm = "B";
  private ArrayList<Double> cpuUtilisation;
  private ArrayList<Double> diskUtilisation;
  private ArrayList<Double> iopsUtilisation;
  private ArrayList<Double> memUtilisation;
  private int totalAllocatedServers;
  private double serverAllocationRate;
  private double workloadAllocationRate;
  private double rack0_UtilisationRate;
  private double rack1_UtilisationRate;
  // timer
  private long startTime;
  private long elapsedTime;
  private boolean debug;
  private int recursion_count; // autility variable for debugging

  /**
   * Constructor
   *
   * @param p
   * @param algorithm
   */
  public Solver(Problem p, String algorithm) {
    this.algorithm = algorithm;
    this.problem = p;
  }

  public void solve(boolean debug) {
    this.debug = debug;
    switch (this.algorithm) {
      case ("F"):
        startTime = System.nanoTime();
        firstFitAllocation();
        elapsedTime = System.nanoTime() - startTime;
        break;

      case ("B"):
        startTime = System.nanoTime();
        // Collections.sort(this.problem.getWorkloads(), new WorkloadComparator());
        // Collections.reverse(this.problem.getWorkloads());
        bestFirstAllocation(this.problem.getWorkloads());
        elapsedTime = System.nanoTime() - startTime;
        break;

        // memory optimised allocation
      case ("MO"):
        Collections.sort(this.problem.getWorkloads(), new MemoryComparator());
        // Collections.reverse(this.problem.getWorkloads());
        startTime = System.nanoTime();
        memoryOptimisedAllocation(this.problem.getWorkloads());
        elapsedTime = System.nanoTime() - startTime;
        break;

        // CPU optimised allocation
      case ("CO"):
        Collections.sort(this.problem.getWorkloads(), new CPUComparator());
        startTime = System.nanoTime();
        cpuOptimisedAllocation(this.problem.getWorkloads());
        elapsedTime = System.nanoTime() - startTime;
        break;

        // CPU optimised allocation
      case ("IO"):
        Collections.sort(this.problem.getWorkloads(), new IOPSComparator());
        startTime = System.nanoTime();
        iopsOptimisedAllocation(this.problem.getWorkloads());
        elapsedTime = System.nanoTime() - startTime;
        break;
    } // end switch

    // after solving, print stats
    printAllocations();
    printAllocationStatistics();
  }

  /**
   * Runs the first fit allocations strategy Scans through the list of available servers and
   * allocates the workload based on what's available
   */
  public void firstFitAllocation() {
    // int workloadIndex = 0;
    int rackIndex = 0;
    int serverIndex = 0;
    Rack currentRack;
    Server currentServer;
    Workload currentWorkload;
    boolean isAllocatable;
    int allocationAttempts = 0;
    int numServers =
        this.problem.getRacks().get(0).getServers().size()
            + this.problem.getRacks().get(1).getServers().size();

    // the total network traffic of all the workloads in rack[0](.ie. sum of xnet for all
    // workloads on rack[0]) must be <= the net bandwidth of rack[1]
    // and vice versa

    for (int workloadIndex = 0;
        workloadIndex < this.problem.getWorkloads().size();
        workloadIndex++) {

      // for each rack and each server, get a current server
      currentRack = this.problem.getRacks().get(rackIndex);
      currentServer = currentRack.getServers().get(serverIndex);
      currentWorkload = this.problem.getWorkloads().get(workloadIndex);

      isAllocatable =
          currentServer.hasCapacityFor(currentWorkload)
              && this.problem.getRacks().get(0).getTotalWorkloadXnet() + currentWorkload.getXnet()
                  <= this.problem.getRacks().get(1).getNet()
              && this.problem.getRacks().get(1).getTotalWorkloadXnet() + currentWorkload.getXnet()
                  <= this.problem.getRacks().get(0).getNet();

      while (isAllocatable == false) {
        // Cycle through to the next server until we find a server that has capacity for
        // this workload

        allocationAttempts += 1; // measure the number of times we attempt to allocate each workload

        // if we have tried to allocate the workload X times, where X is the number of servers
        // available and we still can't find a spot for it then we can't alocate that workload
        if (allocationAttempts > numServers) {
          break;
        }

        serverIndex += 1; // go to the next server index

        if (serverIndex >= currentRack.getServers().size()) {
          serverIndex = 0;
          rackIndex = rackIndex + 1;

          if (rackIndex >= 2) {
            rackIndex = 0;
          }
        }

        currentRack = null;
        currentRack = this.problem.getRacks().get(rackIndex);

        currentServer = null;
        currentServer = currentRack.getServers().get(serverIndex);

        isAllocatable =
            currentServer.hasCapacityFor(currentWorkload)
                && this.problem.getRacks().get(0).getTotalWorkloadXnet() + currentWorkload.getXnet()
                    <= this.problem.getRacks().get(1).getNet()
                && this.problem.getRacks().get(1).getTotalWorkloadXnet() + currentWorkload.getXnet()
                    <= this.problem.getRacks().get(0).getNet();
      }

      if (allocationAttempts < numServers) {
        currentServer.allocate(currentWorkload);
        allocationAttempts = 0;
        currentWorkload = null;
      } else {
        System.out.println(
            "Unable to allocate workload "
                + currentWorkload.getSymbol()
                + " "
                + currentWorkload.getId());
        currentWorkload.setUnableToAllocate(true);
        unableToAllocate.add(currentWorkload.getId());
      }
    }
  }

  /**
   * Go through each workload and place it in the tightest available spot. Place workloads so that
   * the least space is left *
   */
  public void bestFirstAllocation(Workloads w) {
    recursion_count += 1;
    Workload currentWorkload = w.getFirstUnallocated();
    if (currentWorkload == null) {
      return;
    }

    // get the tightest avaialble spot on the servers
    Server bestServer = this.problem.getRacks().getSmallestServer(currentWorkload);
    if (bestServer == null) {
      System.out.println(
          "Unable to allocate workload "
              + currentWorkload.getSymbol()
              + " "
              + currentWorkload.getId());
      currentWorkload.setUnableToAllocate(true);
      unableToAllocate.add(currentWorkload.getId());
    } else {
      bestServer.allocate(currentWorkload);
    }
    bestFirstAllocation(this.problem.getWorkloads());
  }

  /** Allocate the most memory intensive applications on servers with the most memory */
  public void iopsOptimisedAllocation(Workloads w) {
    Workload currentWorkload = w.getFirstUnallocated();
    if (currentWorkload == null) {
      return;
    }
    // get the tightest avaialble spot on the servers
    // Server bestServer = this.problem.getRacks().getSmallestMemServer(currentWorkload);
    Server bestServer = this.problem.getRacks().getSmallestIOPSServer(currentWorkload);
    if (bestServer == null) {
      System.out.println(
          "Unable to allocate workload "
              + currentWorkload.getSymbol()
              + " "
              + currentWorkload.getId());
      currentWorkload.setUnableToAllocate(true);
      unableToAllocate.add(currentWorkload.getId());
    } else {
      bestServer.allocate(currentWorkload);
    }

    iopsOptimisedAllocation(this.problem.getWorkloads());
  }

  /** Allocate the most memory intensive applications on servers with the most memory */
  public void memoryOptimisedAllocation(Workloads w) {
    Workload currentWorkload = w.getFirstUnallocated();
    if (currentWorkload == null) {
      return;
    }
    // get the tightest avaialble spot on the servers
    Server bestServer = this.problem.getRacks().getSmallestMemServer(currentWorkload);
    if (bestServer == null) {
      System.out.println(
          "Unable to allocate workload "
              + currentWorkload.getSymbol()
              + " "
              + currentWorkload.getId());
      currentWorkload.setUnableToAllocate(true);
      unableToAllocate.add(currentWorkload.getId());
    } else {
      bestServer.allocate(currentWorkload);
    }

    memoryOptimisedAllocation(this.problem.getWorkloads());
  }

  public void cpuOptimisedAllocation(Workloads w) {
    recursion_count += 1;
    Workload currentWorkload = w.getFirstUnallocated();
    if (currentWorkload == null) {
      return;
    }
    // get the tightest avaialble spot on the servers
    Server bestServer = this.problem.getRacks().getSmallestCPUServer(currentWorkload);
    if (bestServer == null) {
      System.out.println(
          "Unable to allocate workload "
              + currentWorkload.getSymbol()
              + " "
              + currentWorkload.getId());
      currentWorkload.setUnableToAllocate(true);
    } else {
      bestServer.allocate(currentWorkload);
    }

    cpuOptimisedAllocation(this.problem.getWorkloads());
  }

  /** Prints the current state of the allocation of workloads to servers to standard output */
  public void printAllocations() {
    for (Rack r : this.problem.getRacks()) {
      System.out.println("###################");
      System.out.println("rack.Rack " + r.getId());
      System.out.println("###################");

      for (Server s : r.getServers()) {

        if (s.isAllocated() == false) {
          System.out.println(
              "\uD83D\uDFE2 server.Server"
                  + s.getType()
                  + ". No. "
                  + s.getNo()
                  + "\t: No workloads allocated");
        } else {
          System.out.println("\uD83D\uDD34 server.Server " + s.getType() + ". No. " + s.getNo());

          for (Workload w : s.getWorkloads()) {
            System.out.println("\ud83d\udcbd " + w.getId() + "" + w.getNo());
          }
          System.out.println("\n");
        }
      }
      // System.out.println("\n###################");
    }
  }

  /**
   * Prints the status of the allocation statistics of workloads to servers to standard outout such
   * as cpu utilisation, mem utilisation, etc
   */
  public void printAllocationStatistics() {
    updateAllocationStatistics();
    int totalServers =
        this.problem.getRacks().get(0).getServers().size()
            + this.problem.getRacks().get(1).getServers().size();
    serverAllocationRate = totalAllocatedServers / (double) totalServers;

    System.out.println(
        "Algorithm: " + this.algorithm + " on " + this.problem.getProblem_json_filename());
    System.out.println("Server Allocation rate:\t\t\t\t" + df.format(serverAllocationRate));
    System.out.println("Workload Allocation rate:\t\t\t\t" + df.format(workloadAllocationRate));

    System.out.println(
        "Mean CPU Utilisation:\t\t\t"
            + df.format(cpuUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)));
    System.out.println(
        "Mean Mem Utilisation:\t\t\t"
            + df.format(memUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)));
    System.out.println(
        "Mean Disk Utilisation:\t\t"
            + df.format(diskUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)));
    System.out.println(
        "Mean iops Utilisation:\t\t"
            + df.format(iopsUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)));

    System.out.println("Rack 0 Max Xnet:\t\t\t\t\t" + this.problem.getRacks().get(0).getNet());
    System.out.println(
        "Rack 0 Total Xnet:\t\t\t\t" + this.problem.getRacks().get(0).getTotalWorkloadXnet());

    System.out.println("Rack 1 Max Xnet:\t\t\t\t\t" + this.problem.getRacks().get(1).getNet());
    System.out.println(
        "Rack 1 Total Xnet:\t\t\t\t" + this.problem.getRacks().get(1).getTotalWorkloadXnet());

    System.out.println("Rack 0 Server Util Rate:\t" + df.format(this.rack0_UtilisationRate));
    System.out.println("Rack 1 Server Util Rate:\t" + df.format(this.rack1_UtilisationRate));

    if (!debug) {
      writeResults();
    }
  }

  /**
   * Recompute all the utilisation information. This function is always called before
   * printAllocationInfo()
   */
  private void updateAllocationStatistics() {
    ArrayList<Double> cpuUtilisation = new ArrayList<>();
    ArrayList<Double> diskUtilisation = new ArrayList<>();
    ArrayList<Double> iopsUtilisation = new ArrayList<>();
    ArrayList<Double> memUtilisation = new ArrayList<>();
    int totalAllocatedServers = 0;

    for (Rack r : this.problem.getRacks()) {
      for (Server s : r.getServers()) {
        if (s.isAllocated()) {
          totalAllocatedServers += 1;
          s.compute_utilisations();
          cpuUtilisation.add(s.getCpu_utilisation());
          memUtilisation.add(s.getMem_utilisation());
          iopsUtilisation.add(s.getIops_utlisation());
          diskUtilisation.add(s.getDisk_utilisation());
        }
      }
    }
    this.cpuUtilisation = cpuUtilisation;
    this.diskUtilisation = diskUtilisation;
    this.memUtilisation = memUtilisation;
    this.iopsUtilisation = iopsUtilisation;
    this.totalAllocatedServers = totalAllocatedServers;

    // int numWorkloads = this.problem.getWorkloads().size();
    // workloadAllocationRate = this.problem.getWorkloads().stream().filter(workload ->
    // workload.isAllocated() == true).count() / numWorkloads;
    workloadAllocationRate = this.problem.getWorkloads().getWorkloadAllocationRate();

    this.rack0_UtilisationRate = this.problem.getRacks().get(0).getServerUtilisationRate();
    this.rack1_UtilisationRate = this.problem.getRacks().get(1).getServerUtilisationRate();
  }

  /** Stores the results of each run to csv file */
  public void writeResults() {
    PrintWriter pw = null;
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter("./allocation_results.csv", true));
      pw = new PrintWriter(bw);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    StringBuilder builder = new StringBuilder();
    String columnNamesList =
        "problem_number,"
            + "algorithm,"
            + "totalAllocatedServers, "
            + "serverAllocationRate,workloadAllocationRate,"
            + "mean_cpu_util,"
            + "mean_mem_util,"
            + "mean_disk_util,"
            + "mean_iops_util,"
            + "rack0_max_net,"
            + "rack1_totalWorkloadXnet,"
            + "rack1_max_net,"
            + "rack0_totalWorkloadXnet,"
            + "rack0_utilisation_rate,"
            + "rack1_utilisation_rate,"
            + "unableToAllocate,"
            + "elapsed_time_ns\n";

    // builder.append(columnNamesList);

    // problem description
    builder.append(this.problem.getProblem_json_filename() + ",");
    builder.append(this.algorithm + ","); // problem name
    builder.append(this.totalAllocatedServers + ","); // problem name
    builder.append(df.format(serverAllocationRate) + ","); // serverAllocaion rate
    builder.append(df.format(workloadAllocationRate) + ","); // serverAllocaion rate

    builder.append(
        df.format(cpuUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)) + ",");
    builder.append(
        df.format(memUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)) + ",");
    builder.append(
        df.format(diskUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)) + ",");
    builder.append(
        df.format(iopsUtilisation.stream().mapToDouble(val -> val).average().orElse(0.00)) + ",");
    builder.append(this.problem.getRacks().get(0).getNet() + ","); // rack 0 max net
    builder.append(this.problem.getRacks().get(1).getTotalWorkloadXnet() + ","); // rack 0 max net

    builder.append(this.problem.getRacks().get(1).getNet() + ","); // rack 1 max net
    builder.append(this.problem.getRacks().get(0).getTotalWorkloadXnet() + ","); // rack 0 max net

    builder.append(df.format(this.rack0_UtilisationRate) + ",");
    builder.append(df.format(this.rack1_UtilisationRate) + ",");
    builder.append(this.unableToAllocate.size() + ",");

    // problem results
    builder.append(this.elapsedTime + "\n"); // time

    pw.write(builder.toString());
    pw.close();
  }
}
