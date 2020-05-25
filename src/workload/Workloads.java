package workload;

import java.util.ArrayList;

public class Workloads extends ArrayList<Workload> {
  private int total_cpu;
  private int total_mem; // memory in kilobytes
  private int total_disk; // disk in kb
  private int total_iops; // the I/O operations per second the workload generates
  private int total_xnet; // the traffic the workload generates to nodes outside the rack
  private int total_inet; // the traffic the workload generates to nodes inside the rack

  public Workloads() {
    super();
    total_cpu = 0;
    total_cpu = 0;
    total_mem = 0;
    total_disk = 0;
    total_iops = 0;
    total_xnet = 0;
    total_inet = 0;
  }

  @Override
  /** Computes the sum of the stats of all the workloads in this currrent collection of workloads */
  public boolean add(Workload workload) {
    boolean res = super.add(workload);
    computeTotalStats();
    return res;
  }

  public boolean addWorkload(Workload workload) {
    boolean res = super.add(workload);
    computeTotalStats();
    return res;
  }

  /** Updates the total stats */
  public void computeTotalStats() {
    clearAllStats();
    for (Workload w : this) {
      total_cpu = total_cpu + w.getCpu();
      total_mem = total_mem + w.getMem();
      total_disk = total_disk + w.getDisk();
      total_iops = total_iops + w.getIops();
      total_xnet = total_xnet + w.getXnet();
      total_inet = total_inet + w.getInet();
    }
  }

  public void clearAllStats() {
    total_cpu = 0;
    total_mem = 0;
    total_disk = 0;
    total_inet = 0;
    total_xnet = 0;
    total_iops = 0;
  }

  public void removeWorkload(Workload workload) {
    super.remove(workload);
    computeTotalStats();
  }

  public int getTotal_cpu() {
    return this.total_cpu;
  }

  public int getTotal_mem() {
    return this.total_mem;
  }

  public int getTotal_disk() {
    return this.total_disk;
  }

  public int getTotal_iops() {
    return this.total_iops;
  }

  public int getTotal_xnet() {
    return this.total_xnet;
  }

  public int getTotal_inet() {
    return this.total_inet;
  }

  /**
   * Gets the first unallocatd workload
   *
   * @return
   */
  public Workload getFirstUnallocated() {
    for (Workload w : this) {
      if (w.isAllocated() == false && w.isUnableToAllocate() == false) {
        return w;
      }
    }
    return null;
  }

  public double getWorkloadAllocationRate() {
    int total = 0;
    for (Workload w : this) {
      if (w.isAllocated()) {
        total = total + 1;
      }
    }
    return total / (double) this.size();
  }
}
