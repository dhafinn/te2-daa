import java.util.*;

class BranchBound {

  private int W;
  private List<int[]> items;
  private int n;
  private int[][] M;
  public int[] xHat;
  public int zHat;

  public BranchBound(int W, List<int[]> items) {
    this.W = W;
    this.items = items;
    this.n = items.size();
    this.M = null;
    this.xHat = null;
    this.zHat = 0;
  }

  private void eliminateDominatedItems() {
    List<Integer> N = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      N.add(i);
    }

    for (int j = 0; j < N.size() - 1; j++) {
      for (int k = j + 1; k < N.size(); k++) {
        int[] itemJ = items.get(N.get(j));
        int[] itemK = items.get(N.get(k));
        int wj = itemJ[1];
        int vj = itemJ[0];
        int wk = itemK[1];
        int vk = itemK[0];

        if ((wk / wj) * vj >= vk) {
          N.remove(k);
        } else if ((wj / wk) * vk >= vj) {
          N.remove(j);
          k = N.size();
        }
      }
    }

    List<int[]> updatedItems = new ArrayList<>();
    for (int i : N) {
      updatedItems.add(items.get(i));
    }
    items = updatedItems;
    n = items.size();
  }

  public int calculateUpperBound(int wAps, int VN, int i) {
    List<int[]> items = this.items;
    int n = this.n;

    int U;

    if (i + 2 < n) {
        int[] itemI = items.get(i);
        int[] itemIPlusOne = items.get(i + 1);
        int[] itemIPlusTwo = items.get(i + 2);

        int v1 = itemI[0];
        int w1 = itemI[1];
        int v2 = itemIPlusOne[0];
        int w2 = itemIPlusOne[1];
        int v3 = itemIPlusTwo[0];
        int w3 = itemIPlusTwo[1];

        int zPrime = VN + (wAps / w2) * v2;
        int wDoubleAps = wAps - (wAps / w2) * w2;
        int uAps = zPrime + (wDoubleAps * v3 / w3);

        int wDoubleApsAdjusted = wDoubleAps + (int) Math.ceil((1.0 / w1) * (w2 - wDoubleAps)) * w1;
        int uDoubleAps = zPrime + (int) Math.floor((wDoubleApsAdjusted * v2 / w2) - Math.ceil((1.0 / w1) * (w2 - wDoubleAps)) * v1);

        U = Math.max(uAps, uDoubleAps);
    } else {
        U = VN;
    }

    return U;
}


  public int[][] step1Initialize() {
    eliminateDominatedItems();
    Collections.sort(
      items,
      (x, y) -> Double.compare((double) y[0] / y[1], (double) x[0] / x[1])
    );
    M = new int[n][W + 1];
    xHat = new int[n];
    zHat = 0;

    int[] x = new int[n];
    int i = 0;
    x[0] = W / items.get(0)[1];
    int Vn = items.get(0)[0] * x[0];
    int wAps = W - items.get(0)[1] * x[0];
    int U = calculateUpperBound(wAps, Vn, i);
    zHat = Vn;
    xHat = x.clone();

    int[] m = new int[n];
    for (int y = 0; y < n; y++) {
      int min_w = Integer.MAX_VALUE;
      for (int j = 0; j < items.size(); j++) {
        int[] item = items.get(j);
        if (j > y && item[1] < min_w) {
          min_w = item[1];
        }
      }
      m[y] = min_w;
    }
    return new int[][] { x, { i }, { Vn }, { wAps }, { U }, m };
  }

  public int[] step2Develop(
    int[] x,
    int i,
    int Vn,
    int wAps,
    int U,
    List<Integer> m
  ) {
    while (true) {
      if (wAps < m.get(i)) {
        if (zHat < Vn) {
          zHat = Vn;
          xHat = Arrays.copyOf(x, x.length);
          if (zHat == U) {
            return new int[] { i, Vn, wAps, 3 }; // 3 indicates Finish
          }
        }
        return new int[] { i, Vn, wAps, 0 }; // 0 indicates Backtrack
      } else {
        int min_j = -1;
        for (int j = i + 1; j < n; j++) {
          if (items.get(j)[1] <= wAps) {
            min_j = j;
            break;
          }
        }
        if (
          min_j == -1 ||
          (Vn + calculateUpperBound(wAps, Vn, min_j) <= zHat)
        ) {
          return new int[] { i, Vn, wAps, 0 }; // 0 indicates Backtrack
        }
        if (M[i][wAps] >= Vn) {
          return new int[] { i, Vn, wAps, 0 }; // 0 indicates Backtrack
        }
        int[] item = items.get(min_j);
        x[min_j] = wAps / item[1];
        Vn += item[0] * x[min_j];
        wAps -= item[1] * x[min_j];
        M[i][wAps] = Vn;
        i = min_j;
      }
    }
  }

  public int[] step3Backtrack(
    int[] x,
    int i,
    int Vn,
    int wAps,
    List<Integer> m
  ) {
    while (true) {
      int max_j = -1;
      for (int j = i; j >= 0; j--) {
        if (x[j] > 0) {
          max_j = j;
          break;
        }
      }
      if (max_j == -1) {
        return new int[] { i, Vn, wAps, 3 }; // 3 indicates Finish
      }
      i = max_j;
      x[i]--;
      Vn -= items.get(i)[0];
      wAps += items.get(i)[1];
      if (wAps < m.get(i)) {
        continue;
      }
      if (
        Vn +
        Math.floor(
          wAps * (double) items.get(i + 1)[0] / items.get(i + 1)[1]
        ) <=
        zHat
      ) {
        Vn -= items.get(i)[0] * x[i];
        wAps += items.get(i)[1] * x[i];
        x[i] = 0;
        continue;
      }
      if (wAps - items.get(i)[1]>= m.get(i)) {
        return new int[] { i, Vn, wAps, 1 }; // 1 indicates Develop
      }
    }
  }

  public int[] step4ReplaceItem(
    int[] x,
    int i,
    int Vn,
    int wAps,
    List<Integer> m
  ) {
    int j = i;
    int h = j + 1;
    while (true) {
      if (
        zHat >=
        Vn +
        Math.floor((double) wAps * items.get(h)[0] / items.get(h)[1])
      ) {
        return new int[] { i, Vn, wAps, 0 }; // 0 indicates Backtrack
      }
      if (items.get(h)[1] >= items.get(j)[1]) {
        if (
          items.get(h)[1] == items.get(j)[1] ||
          items.get(h)[1] > wAps ||
          zHat >= Vn + items.get(h)[0]
        ) {
          h++;
          continue;
        }
        zHat = Vn + items.get(h)[0];
        xHat = x.clone();
        x[h] = 1;
        if (zHat == calculateUpperBound(wAps, Vn, h)) {
          return new int[] { i, Vn, wAps, 3 }; // 3 indicates Finish
        }
        j = h;
        h++;
      } else {
        if (wAps - items.get(h)[1] < m.get(h - 1)) {
          h++;
          continue;
        }
        i = h;
        x[i] = wAps / items.get(i)[1];
        Vn += items.get(i)[0] * x[i];
        wAps -= items.get(i)[1] * x[i];
        return new int[] { i, Vn, wAps, 1 }; // 1 indicates Develop
      }
    }
  }

  public void branchAndBound() {
    int[][] result = step1Initialize();
    int[] x = result[0];
    // {i, Vn, wAps, U}
    int i = result[1][0];
    int Vn = result[2][0];
    int wAps = result[3][0];
    int U = result[4][0];
    int[] marr = result[5];

    List<Integer> m = new ArrayList<>();
    for (int value : marr) {
        m.add(value);
    }

    String next_step = "Develop";
    while (true) {
      int[] temp;
      if (next_step.equals("Develop")) {
        temp = step2Develop(x, i, Vn, wAps, U, m);
        if (temp[3] == 0) {
            next_step = "Backtrack";
        }
        else if (temp[3] == 1) {
            next_step = "Develop";
        }
        else if (temp[3] == 3) {
            next_step = "Finish";
        }
    } else if (next_step.equals("Backtrack")) {
        temp = step3Backtrack(x, i, Vn, wAps, m);
        if (temp[3] == 0) {
            next_step = "Backtrack";
        }
        else if (temp[3] == 1) {
            next_step = "Develop";
        }
        else if (temp[3] == 3) {
            next_step = "Finish";
        }
    } else if (next_step.equals("Replace")) {
        temp = step4ReplaceItem(x, i, Vn, wAps, m);
        if (temp[3] == 0) {
            next_step = "Backtrack";
        }
        else if (temp[3] == 1) {
            next_step = "Develop";
        }
        else if (temp[3] == 3) {
            next_step = "Finish";
        }
      } else {
        break;
      }
      i = temp[0];
      Vn = temp[1];
      wAps = temp[2];
    }
  }

  public static void main(String[] args) {
    int W = 100;
    int val[] = { 10, 30, 20 };
    int wt[] = { 5, 10, 15 };

    List<int[]> items = new ArrayList<>();
    for (int i = 0; i < val.length; i++) {
      items.add(new int[] { val[i], wt[i] });
    }

    BranchBound ukp = new BranchBound(W, items);
    ukp.branchAndBound();

    System.out.println("W                       :" + W);
    System.out.print("Items <value, weight>   : [");
    for (int i = 0; i < items.size(); i++) {
      int[] item = items.get(i);
      System.out.print(Arrays.toString(item));
      if (i != items.size() - 1) {
        System.out.print(", ");
      }
    }
    System.out.println("]");
    System.out.println(
      "Best value              : " + Arrays.toString(ukp.xHat)
    );
    System.out.println("Best item configuration : " + ukp.zHat);
  }
}
