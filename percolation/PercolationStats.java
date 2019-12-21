/******************************************************************************
 *  Name:           SOURAV SAMANTA
 *  Date:           01/09/2018
 *  Compilation:    javac-algs4 PercolationStats.java
 *  Execution:      java-algs4 PercolationStats n T
 *  Dependencies:   Percolation.java
 *
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONST_1 = 1.96;

    private final int trials;
    private final double percolationThresholdMean;
    private final double percolationThresholdStdDev;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new java.lang.IllegalArgumentException();

        this.trials = trials;
        double[] percolationThresholdForAllTrials = new double[trials];
        double percolationThresholdPerTrial;

        for (int i = 0; i < trials; i++) {
            Percolation percObj = new Percolation(n);
            int id, row, col;
            while (!percObj.percolates()) {
                id = StdRandom.uniform(0, n * n);
                row = (id / n) + 1;
                col = (id % n) + 1;
                percObj.open(row, col);
            }
            percolationThresholdPerTrial = (double) percObj.numberOfOpenSites() / (n * n);
            percolationThresholdForAllTrials[i] = percolationThresholdPerTrial;
        }
        percolationThresholdMean = StdStats.mean(percolationThresholdForAllTrials);
        percolationThresholdStdDev = StdStats.stddev(percolationThresholdForAllTrials);
    }

    public double mean() {
        return percolationThresholdMean;
    }

    public double stddev() {
        return percolationThresholdStdDev;
    }

    public double confidenceLo() {
        return (percolationThresholdMean - (CONST_1 * percolationThresholdStdDev) / Math
                .sqrt(trials));
    }

    public double confidenceHi() {
        return (percolationThresholdMean + (CONST_1 * percolationThresholdStdDev) / Math
                .sqrt(trials));
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        // System.out.println("n: " + n + " trials: " + trials);
        PercolationStats obj = new PercolationStats(n, trials);
        System.out.println("mean\t\t\t= " + obj.mean());
        System.out.println("stddev\t\t\t= " + obj.stddev());
        System.out.println(
                "95% confidence interval = [" + obj.confidenceLo() + ", " + obj.confidenceHi()
                        + "]");
    }
}
