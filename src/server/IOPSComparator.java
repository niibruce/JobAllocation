package server;

import java.util.Comparator;

public class IOPSComparator implements Comparator<Server> {
  @Override
  public int compare(Server o1, Server o2) {
    if (o1.getIops() < o2.getIops()) {
      return 1;
    } else if (o1.getIops() > o2.getIops()) {
      return -1;
    } else {
      return 0;
    }
  }
}
