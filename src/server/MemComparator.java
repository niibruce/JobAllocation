package server;

import java.util.Comparator;

public class MemComparator implements Comparator<Server> {

  @Override
  public int compare(Server o1, Server o2) {
    if (o1.getMem() < o2.getMem()) {
      return 1;
    } else if (o1.getMem() > o2.getMem()) {
      return -1;
    } else {
      return 0;
    }
  }
}
