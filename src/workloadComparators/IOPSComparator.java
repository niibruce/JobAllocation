package workloadComparators;

import java.util.Comparator;
import workload.Workload;

public class IOPSComparator implements Comparator<Workload> {
  @Override
  public int compare(Workload o1, Workload o2) {
    if (o1.getIops() < o2.getIops()) {
      return 1;
    } else if (o1.getIops() > o2.getIops()) {
      return -1;
    } else {
      return 0;
    }
  }
}
