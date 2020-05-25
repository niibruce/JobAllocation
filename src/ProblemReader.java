import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import rack.Rack;
import rack.Racks;
import server.Server;
import server.Servers;
import workload.Workload;
import workload.Workloads;

/**
 * Utility class to read in the jon problem files and create the infrastructure of the problem. The
 * main class is the createInfrastructure class which returns a Problem class
 */
public class ProblemReader {
  private final Problem this_problem;
  private final String filename;

  /**
   * Creates a new instance of the constructor
   *
   * @param problemFile
   */
  public ProblemReader(String problemFile) {
    this.filename = problemFile;
    Gson gson = new Gson();
    JsonReader reader = null;
    try {
      reader = new JsonReader(new FileReader(problemFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Problem problem = gson.fromJson(reader, Problem.class);
    this.this_problem = problem;
  }

  /**
   * Creates the required number of infrastructure elements for each rack, server and workload.
   *
   * @return
   */
  public Problem createInfrastructure() {

    Problem new_problem = new Problem();
    new_problem.setProblem_json_filename(this.filename);

    Workloads new_workloads = new Workloads();
    Racks new_racks = new Racks();

    // create the workloads
    Workload current_workload;
    Rack current_rack;

    // Loop through each workload item and add the required number of workloads
    for (Workload w : this.this_problem.getWorkloads()) {
      for (int i = 0; i < w.getNo(); i++) {
        current_workload = w.copy();
        current_workload.setNo(i);
        new_workloads.add(current_workload);
      }
    }

    // create the racks
    for (Rack r : this.this_problem.getRacks()) {
      // create a new rack
      Rack new_rack = r.copy();

      // for each newly created rack, create a number of servers tht corresponds to how may servers
      // there shoudl be
      Servers s = new Servers();
      for (Server s_new : r.getServers()) {
        for (int k = 0; k < s_new.getNo(); k++) {
          Server new_server = s_new.copy();
          new_server.setNo(k);
          s.add(new_server);
        }
      }
      new_rack.setServers(s);
      new_racks.add(new_rack);
    }

    new_problem.setWorkloads(new_workloads);
    new_problem.setRacks(new_racks);
    return new_problem;
  }
}
