package server;

import java.util.ArrayList;

public class Servers extends ArrayList<Server> {

  public Servers() {
    super();
  }

  public int getTotalXnet() {
    int totalXnet = 0;

    for (Server s : this) {
      totalXnet = totalXnet + s.getTotalXnet();
    }
    return totalXnet;
  }
}
