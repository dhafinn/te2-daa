import java.io.*;
import java.util.*;


class Main {
    public static void main(String args[]) {
        String filename = "input100v2.txt";
        int[][] input = readInputFromFile(filename);
        int W = input[0][0];
        int[] wt = input[1];
        int[] val = input[2];
        int n = val.length;

        List<int[]> items = new ArrayList<>();
        for (int i = 0; i < val.length; i++) {
            items.add(new int[]{val[i], wt[i]});
        }

        
        System.out.println("----------Branch and Bound----------");

        // Measure the starting state
        long startTime = System.currentTimeMillis();
        long beforeMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        BranchBound knapsackSolver = new BranchBound(W, items);
        knapsackSolver.branchAndBound();

         // Measure the ending state
        long endTime = System.currentTimeMillis();
        long afterMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        // Calculate the running time
        long runningTime = endTime - startTime;
        long actualMemUsed=(afterMem-beforeMem);

        // Print running time
        System.out.println("Running Time Branch and Bound: " + runningTime + " milliseconds");
        System.out.println("Memory Usage Branch and Bound: "+ actualMemUsed + " bytes");
        System.out.println("Hasil: " +  knapsackSolver.zHat);

        
        System.out.println("----------DP 2 Dimensi----------");

        // Measure the starting state
        startTime = System.currentTimeMillis();
        beforeMem =Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        int res = (UnboundedKnapsackDP.unboundedKnapsackDP(W, val, wt).value);

          // Measure the ending state
        endTime = System.currentTimeMillis();
        afterMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        // Calculate the running time and memory
        runningTime = endTime - startTime;
        actualMemUsed=(afterMem-beforeMem);

        // Print running time
        System.out.println("Running Time DP: " + runningTime + " milliseconds");
        System.out.println("Memory Usage DP: "+ actualMemUsed + " bytes");
        System.out.println("Hasil: " +  res);



        System.out.println("----------DP 1 Dimensi----------");

        // Measure the starting state
        startTime = System.currentTimeMillis();
        beforeMem =Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        int res1 = (Knapsack.unboundedKnapsack(W, n, val, wt));

          // Measure the ending state
        endTime = System.currentTimeMillis();
        afterMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        // Calculate the running time and memory
        runningTime = endTime - startTime;
        actualMemUsed=(afterMem-beforeMem);

        // Print running time
        System.out.println("Running Time DP: " + runningTime + " milliseconds");
        System.out.println("Memory Usage DP: "+ actualMemUsed + " bytes");
        System.out.println("Hasil: " +  res1);
    }


    static int[][] readInputFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            ArrayList<Integer> valList = new ArrayList<>();
            ArrayList<Integer> wtList = new ArrayList<>();
            int W = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Knapsack Capacity:")) {
                    W = Integer.parseInt(line.split(": ")[1]);
                } else if (line.startsWith("Weights:")) {
                    String[] weights = line.split(": ")[1].split(", ");
                    for (String weight : weights) {
                        wtList.add(Integer.parseInt(weight));
                    }
                } else if (line.startsWith("Values:")) {
                    String[] values = line.split(": ")[1].split(", ");
                    for (String value : values) {
                        valList.add(Integer.parseInt(value));
                    }
                }
            }
            reader.close();

            // Convert ArrayLists to arrays
            int[] val = valList.stream().mapToInt(Integer::intValue).toArray();
            int[] wt = wtList.stream().mapToInt(Integer::intValue).toArray();

            return new int[][] { { W }, wt, val };

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
