package server;

import java.util.Comparator;

public class CPUComparator implements Comparator<Server> {

  @Override
  public int compare(Server o1, Server o2) {
    if (o1.getCpu() < o2.getCpu()) {
      return 1;
    } else if (o1.getCpu() > o2.getCpu()) {
      return -1;
    } else {
      return 0;
    }
  }
}
