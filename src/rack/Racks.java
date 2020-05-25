package rack;

import java.util.ArrayList;
import java.util.Collections;
import server.CPUComparator;
import server.Server;
import workload.Workload;

public class Racks extends ArrayList<Rack> {

  public Racks() {
    super();
  }

  public Server getSmallestServer(Workload w) {
    Server smallestServer = null;

    for (Rack r : this) {
      for (Server s : r.getServers()) {

        if (smallestServer == null) {
          if (s.hasCapacityFor(w)) {
            smallestServer = s;
          }
        } else {
          if (s.hasCapacityFor(w) && s.hasLessCapacityThan(smallestServer)) {
            smallestServer = s;
          }
        }
      }
    }
    return smallestServer;
  }

  public Server getSmallestMemServer(Workload w) {
    Server smallestMemServer = null;
    ArrayList<Server> temp = new ArrayList<>();
    temp.addAll(this.get(0).getServers());
    temp.addAll(this.get(1).getServers());
    Collections.sort(temp, new CPUComparator());

    for (Server s : temp) {
      if (smallestMemServer == null) {
        if (s.hasCapacityFor(w)) {
          smallestMemServer = s;
        }

      } else {

        if (s.hasCapacityFor(w) && (s.getMem() < smallestMemServer.getMem())) {
          smallestMemServer = s;
        }
      }
    }
    return smallestMemServer;
  }

  public Server getSmallestCPUServer(Workload w) {
    Server smallestCPUServer = null;
    ArrayList<Server> temp = new ArrayList<>();
    temp.addAll(this.get(0).getServers());
    temp.addAll(this.get(1).getServers());
    Collections.sort(temp, new CPUComparator());

    for (Server s : temp) {
      if (smallestCPUServer == null) {
        if (s.hasCapacityFor(w)) {
          smallestCPUServer = s;
        }

      } else {

        if (s.hasCapacityFor(w) && (s.getMem() < smallestCPUServer.getMem())) {
          smallestCPUServer = s;
        }
      }
    }
    return smallestCPUServer;
  }

  public Server getSmallestIOPSServer(Workload w) {
    Server smallestIOPSServer = null;
    ArrayList<Server> temp = new ArrayList<>();
    temp.addAll(this.get(0).getServers());
    temp.addAll(this.get(1).getServers());
    Collections.sort(temp, new CPUComparator());

    for (Server s : temp) {
      if (smallestIOPSServer == null) {
        if (s.hasCapacityFor(w)) {
          smallestIOPSServer = s;
        }

      } else {

        if (s.hasCapacityFor(w) && (s.getMem() < smallestIOPSServer.getMem())) {
          smallestIOPSServer = s;
        }
      }
    }
    return smallestIOPSServer;
  }
}
