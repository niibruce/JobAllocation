package workloadComparators;

import java.util.Comparator;
import workload.Workload;

public class CPUComparator implements Comparator<Workload> {
  @Override
  public int compare(Workload o1, Workload o2) {
    if (o1.getCpu() < o2.getCpu()) {
      return 1;
    } else if (o1.getCpu() > o2.getCpu()) {
      return -1;
    } else {
      return 0;
    }
  }
}
