package server;

import workload.Workload;
import workload.Workloads;

public class Server {
  // The workloads assigned to this server
  private final Workloads workloads = new Workloads();
  private String type; // a type identifier
  private int cpu; // the number of CPU cores in the server times 100
  private int mem; // the amount of main memory in the server in GB
  private int disk; // the amount of disk space available in a server
  private int iops; // the I/O operations per second the workload generates
  private int net; // the network bandwidth to the router at the top of the rack, in kb
  private int watt; // the power consumption of the server
  private int no; // the number of servers of this type
  private int spare_capacity_cpu;
  private int spare_capacity_mem; // memory in kilobytes
  private int spare_capacity_disk; // disk in kb
  private int spare_capacity_iops; // the I/O operations per second the workload generates
  private int spare_capacity_xnet; // the traffic the workload generates to nodes outside the rack
  private int spare_capacity_inet; // the traffic the workload generates to nodes inside the rack
  private int spare_capacity_net;

  // analytics
  private double cpu_utilisation;
  private double mem_utilisation;
  private double disk_utilisation;
  private double iops_utlisation;
  private int totalNetworkTraffic;
  private int totalInetTraffic;

  public Server() {}

  /**
   * Creates a new instance of the current server object with the same data. The number is updated
   * in the calling function
   *
   * @return
   */
  public Server copy() {
    Server new_server = new Server();

    new_server.setCpu(this.cpu);
    new_server.setDisk(this.disk);
    new_server.setIops(this.iops);
    new_server.setMem(this.mem);
    new_server.setNet(this.net);
    new_server.setType(this.type);
    new_server.setWatt(this.watt);

    return new_server;
  }

  /**
   * Allocate a workload to this server
   *
   * @param w
   */
  public void allocate(Workload w) {

    this.workloads.addWorkload(w);
    w.allocate(this);
    computeSpareCapacity(); // after each allocation, compute the remaining spare capacity
  }

  public void computeSpareCapacity() {
    this.workloads.computeTotalStats();
    spare_capacity_cpu = this.cpu - this.workloads.getTotal_cpu();
    spare_capacity_disk = this.disk - this.workloads.getTotal_disk();
    spare_capacity_net =
        this.net - (this.workloads.getTotal_xnet() + this.workloads.getTotal_inet());
    spare_capacity_iops = this.iops - this.workloads.getTotal_iops();
    spare_capacity_mem = this.mem - this.workloads.getTotal_mem();
  }

  public boolean hasCapacityFor(Workload w) {

    // A server has capacity iff:
    // the new workload can fit in the spare capacity of the server
    // network space capacity is given as the difference between the net of the server AND
    // the sum of the total (XNet & inet) of all the workloads of the server
    computeSpareCapacity();
    boolean hasCapacity = false;

    hasCapacity =
        w.getCpu() <= this.spare_capacity_cpu
            && w.getIops() <= this.spare_capacity_iops
            && w.getDisk() <= this.spare_capacity_disk
            && (w.getXnet() + w.getInet()) <= this.spare_capacity_net
            && w.getMem() <= this.spare_capacity_mem;

    return hasCapacity;
  }

  public int getTotalXnet() {
    int totalXnet = 0;
    for (Workload w : workloads) {
      totalXnet = totalXnet + w.getXnet();
    }
    return totalXnet;
  }

  public int getTotalWorkloadTraffic() {
    totalNetworkTraffic = 0;

    for (Workload w : this.workloads) {
      totalNetworkTraffic = totalNetworkTraffic + w.getXnet() + w.getInet();
    }
    return totalNetworkTraffic;
  }

  public int getTotalInetTraffic() {
    totalInetTraffic = 0;

    for (Workload w : this.workloads) {
      totalInetTraffic = totalInetTraffic + w.getInet();
    }
    return totalInetTraffic;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getCpu() {
    return cpu;
  }

  public void setCpu(int cpu) {
    this.cpu = cpu;
  }

  public int getMem() {
    return mem;
  }

  public void setMem(int mem) {
    this.mem = mem;
  }

  public int getDisk() {
    return disk;
  }

  public void setDisk(int disk) {
    this.disk = disk;
  }

  public int getIops() {
    return iops;
  }

  public void setIops(int iops) {
    this.iops = iops;
  }

  public int getNet() {
    return net;
  }

  public void setNet(int net) {
    this.net = net;
  }

  public int getWatt() {
    return watt;
  }

  public void setWatt(int watt) {
    this.watt = watt;
  }

  public int getNo() {
    return no;
  }

  public void setNo(int no) {
    this.no = no;
  }

  public Workloads getWorkloads() {
    return this.workloads;
  }

  /**
   * Returns true is a workload has been allocated to this server
   *
   * @return
   */
  public boolean isAllocated() {
    computeSpareCapacity();
    return this.workloads.size() > 0;
  }

  public void compute_utilisations() {
    computeSpareCapacity();
    this.cpu_utilisation = (cpu - spare_capacity_cpu) / (double) cpu;
    this.mem_utilisation = (mem - spare_capacity_mem) / (double) mem;
    this.disk_utilisation = (disk - spare_capacity_disk) / (double) disk;
    this.iops_utlisation = (iops - spare_capacity_iops) / (double) iops;
  }

  public double getCpu_utilisation() {
    return cpu_utilisation;
  }

  public double getMem_utilisation() {
    return mem_utilisation;
  }

  public double getDisk_utilisation() {
    return disk_utilisation;
  }

  public double getIops_utlisation() {
    return iops_utlisation;
  }

  /**
   * This server has les capacity than the server S if all the spare capacities of this server are
   * less than the spare capacities of the Server S.
   *
   * @param s A server you want to compare with the current server
   * @return
   */
  public boolean hasLessCapacityThan(Server s) {
    // update hte spare capacity statistics of both servers
    this.computeSpareCapacity();
    s.computeSpareCapacity();

    return (this.getCpu() < s.getCpu()
        && this.getMem() < s.getMem()
        && this.getDisk() < s.getDisk()
        && this.getIops() < s.getIops()
        && this.getNet() < s.getNet());

    /**
     * return this.spare_capacity_iops < s.spare_capacity_iops && this.spare_capacity_disk <
     * s.spare_capacity_disk && this.spare_capacity_mem < s.spare_capacity_mem &&
     * this.spare_capacity_net < s.spare_capacity_net && this.spare_capacity_cpu <
     * s.spare_capacity_cpu;
     */
  }

  public int getSpare_capacity_cpu() {
    return this.spare_capacity_cpu;
  }

  public int getSpare_capacity_mem() {
    return getSpare_capacity_mem();
  }
}
