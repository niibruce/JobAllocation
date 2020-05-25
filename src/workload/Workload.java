package workload;

import java.util.Comparator;
import server.Server;

public class Workload implements Comparator<Workload> {

  private String id;
  private String symbol;
  private int cpu; // the % of a CPU core the workload requires
  private int mem; // memory in kilobytes
  private int disk; // disk in kb
  private int iops; // the I/O operations per second the workload generates
  private int xnet; // the traffic the workload generates to nodes outside the rack
  private int inet; // the traffic the workload generates to nodes inside the rack
  private int no; // the number of instances of this workload to be instantiated
  private boolean isAllocated;
  private boolean unableToAllocate;

  private Server server; // this is the server that has been allocated to this workload

  public Workload() {}

  public void allocate(Server s) {
    this.server = s;
    setAllocated(true);
    setUnableToAllocate(false);
  }

  /** Prints details about the current workload to standard output */
  public void getInfo() {
    System.out.println("Symbol: " + this.symbol + "CPU: " + this.cpu);
  }

  public int getNo() {
    return no;
  }

  public void setNo(int no) {
    this.no = no;
  }

  public Workload copy() {
    Workload newWorkload = new Workload();
    newWorkload.setCpu(this.cpu);
    newWorkload.setDisk(this.disk);
    newWorkload.setId(this.id);
    newWorkload.setInet(this.inet);
    newWorkload.setIops(this.iops);
    newWorkload.setMem(this.mem);
    newWorkload.setSymbol(this.symbol);
    newWorkload.setXnet(this.xnet);

    return newWorkload;
  }

  public int getInet() {
    return inet;
  }

  public void setInet(int inet) {
    this.inet = inet;
  }

  public int getXnet() {
    return xnet;
  }

  public void setXnet(int xnet) {
    this.xnet = xnet;
  }

  public int getIops() {
    return iops;
  }

  public void setIops(int iops) {
    this.iops = iops;
  }

  public int getDisk() {
    return disk;
  }

  public void setDisk(int disk) {
    this.disk = disk;
  }

  public int getMem() {
    return mem;
  }

  public void setMem(int mem) {
    this.mem = mem;
  }

  public int getCpu() {
    return cpu;
  }

  public void setCpu(int cpu) {
    this.cpu = cpu;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isAllocated() {
    return isAllocated;
  }

  public void setAllocated(boolean allocated) {
    isAllocated = allocated;
  }

  @Override
  public int compare(Workload o1, Workload o2) {
    return o1.getMem() - o2.getMem();
  }

  public boolean isUnableToAllocate() {
    return unableToAllocate;
  }

  public void setUnableToAllocate(boolean unableToAllocate) {
    this.unableToAllocate = unableToAllocate;
  }
}
