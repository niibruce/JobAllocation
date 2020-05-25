package workloadComparators;

import java.util.Comparator;
import workload.Workload;

public class WorkloadComparator implements Comparator<Workload> {
  @Override
  public int compare(Workload o1, Workload o2) {
    if (o1.getDisk() < o2.getDisk()
        && o1.getInet() < o2.getInet()
        && o1.getXnet() < o2.getXnet()
        && o1.getCpu() < o2.getCpu()
        && o1.getIops() < o2.getIops()
        && o1.getMem() < o2.getMem()) {
      return 1;
    } else if (o1.getDisk() > o2.getDisk()
        && o1.getInet() > o2.getInet()
        && o1.getXnet() > o2.getXnet()
        && o1.getCpu() > o2.getCpu()
        && o1.getIops() > o2.getIops()
        && o1.getMem() > o2.getMem()) {
      return -1;
    } else {
      return 0;
    }
  }
}
