import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnboundedKnapsackDP {

    static class Result {
        int value;
        int[] itemCount;

        public Result(int value, int[] itemCount) {
            this.value = value;
            this.itemCount = itemCount;
        }
    }

    public static Result unboundedKnapsackDP(int W, int[] val, int[] wt) {
        int n = val.length;
        int[] dp = new int[W + 1];
        int[][] itemCount = new int[W + 1][n];

        for (int i = 1; i <= W; i++) {
            for (int j = 0; j < n; j++) {
                if (wt[j] <= i && dp[i] < dp[i - wt[j]] + val[j]) {
                    dp[i] = dp[i - wt[j]] + val[j];
                    itemCount[i] = Arrays.copyOf(itemCount[i - wt[j]], n);
                    itemCount[i][j]++;
                }
            }
        }

        return new Result(dp[W], itemCount[W]);
    }

    public static void main(String[] args) {
        int W = 198;
        int[] val = {15, 25, 9, 10 ,18, 35, 12, 28, 18};
        int[] wt = {17, 8, 10, 5, 10, 15, 8, 17, 23};

        Result result = unboundedKnapsackDP(W, val, wt);
        System.out.println("W (knapsack capacity)   : " + W);
        System.out.println("Items <value, weight>   : " + Arrays.toString(val) + ", " + Arrays.toString(wt));
        System.out.println("Best value              : " + result.value);
        System.out.println("Best item configuration : " + Arrays.toString(result.itemCount));
    }
}
