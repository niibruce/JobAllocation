import rack.Rack;
import rack.Racks;
import server.Server;
import server.Servers;
import workload.Workload;
import workload.Workloads;

public class Problem {
  private Workloads workloads;
  private Racks racks;
  private String problem_json_filename;

  public Problem() {}

  public Problem createInfrastructure() {

    Problem new_problem = new Problem();
    Workloads new_workloads = new Workloads();
    Racks new_racks = new Racks();

    // create the workloads
    Workload current_workload;
    Rack current_rack;

    // Loop through each workload item and add the required number of workloads
    for (Workload w : this.workloads) {
      for (int i = 0; i < w.getNo(); i++) {
        current_workload = w.copy();
        current_workload.setNo(i);
        new_workloads.add(current_workload);
      }
    }

    // create the racks
    for (Rack r : this.racks) {
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

  public Workloads getWorkloads() {
    return this.workloads;
  }

  public void setWorkloads(Workloads workloads) {
    this.workloads = workloads;
  }

  public Racks getRacks() {
    return racks;
  }

  public void setRacks(Racks racks) {
    this.racks = racks;
  }

  public String getProblem_json_filename() {
    return problem_json_filename;
  }

  public void setProblem_json_filename(String problem_json_filename) {
    this.problem_json_filename = problem_json_filename;
  }
}
