package rack;

import server.Server;
import server.Servers;
import workload.Workload;

public class Rack {
  private String id;
  private int net; // the network bandwidth available to nodes outside the rack
  private Servers servers;
  private int totalXnet;

  public Rack() {}

  public Rack copy() {
    Rack new_rack = new Rack();
    new_rack.setId(this.id);
    new_rack.setNet(this.net);
    new_rack.setServers(this.servers);

    return new_rack;
  }

  /**
   * Get the sum of all the XNETS of all the workloads assigned to this server
   *
   * @return
   */
  public int getTotalWorkloadXnet() {
    computeTotalWorkloadXnet();
    return totalXnet;
  }

  /** For each workload in each server on this rack, compute the total XNET. */
  public void computeTotalWorkloadXnet() {
    totalXnet = 0;
    for (Server s : this.servers) {
      for (Workload w : s.getWorkloads()) {
        // sum the xnet
        totalXnet = totalXnet + w.getXnet();
      }
    }
  }

  private void updateTotalXnet() {
    totalXnet = 0;
    totalXnet = servers.getTotalXnet();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getNet() {
    return net;
  }

  public void setNet(int net) {
    this.net = net;
  }

  public Servers getServers() {
    return servers;
  }

  public void setServers(Servers servers) {
    this.servers = servers;
  }

  /**
   * Get the number of servers with at least 1 workload assigned
   *
   * @return
   */
  public double getServerUtilisationRate() {
    int totalServersWithAtLeastOneWorkload = 0;

    for (Server s : this.getServers()) {
      if (s.isAllocated()) {
        totalServersWithAtLeastOneWorkload += 1;
      }
    }

    return totalServersWithAtLeastOneWorkload / (double) this.getServers().size();
  }
}
