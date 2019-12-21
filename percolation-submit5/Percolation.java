/******************************************************************************
 *  Name:           MAITREE SAMANTA
 *  Date:           01/08/2019
 *  Compilation:    javac-algs4 Percolation.java
 *  Execution:      java-algs4 Percolation input5.txt
 *  Dependencies:
 *
 *
 ******************************************************************************/

// import edu.princeton.cs.algs4.In;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final byte OPEN_TOP = 1;
    private static final byte OPEN_BOTTOM = 2;
    private static final byte OPEN = 3;
    private static final byte BLOCKED = 4;
    private static final byte INVALID = 0;

    private final int n;
    private byte[][] site;
    private final WeightedQuickUnionUF uf;
    private int countOpenSites;
    private boolean percolationStatus;

    public Percolation(int n) {
        if (n <= 0) throw new java.lang.IllegalArgumentException();
        this.n = n;

        site = new byte[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                site[i][j] = BLOCKED;
            }
        }

        uf = new WeightedQuickUnionUF(n * n);

        countOpenSites = 0;
        percolationStatus = false;
    }

    private void validate(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n)
            throw new java.lang.IllegalArgumentException();
    }

    private int mapXyTo1D(int row, int col) {
        int id = n * (row - 1) + (col - 1);
        // System.out.println("(" + row + ", " + col + ") --> " + id);
        return id;
    }

    private int[] map1DToXy(int id) {
        int[] arr = new int[2];     // row --> arr[0], col --> arr[1]
        arr[0] = (id / n) + 1;
        arr[1] = (id % n) + 1;
        // System.out.println(id + " --> (" + arr[0] + ", " + arr[1] + ")");
        return arr;
    }

    public void open(int row, int col) {
        validate(row, col);

        // Check if the site is not open i.e. it is blocked then proceed
        if (!isOpen(row, col)) {
            // Increase the number of open sites by 1
            countOpenSites++;

            // Calculate the current site's ID
            int currSiteID = mapXyTo1D(row, col);

            // Calculate the current site's status
            byte currSiteStatus = OPEN;
            if (row == 1)
                currSiteStatus = OPEN_TOP;
            else if (row == n)
                currSiteStatus = OPEN_BOTTOM;
            // Update the current site's status in the matrix
            site[row - 1][col - 1] = currSiteStatus;

            // Calculate details of the neighbors
            // a. Site's position in terms of (x, y)
            int leftCol = col - 1;
            int rightCol = col + 1;
            int downRow = row + 1;
            int upRow = row - 1;

            // b. Neighbor's initial ID
            int leftSiteID = -1;
            int rightSiteID = -1;
            int downSiteID = -1;
            int upSiteID = -1;

            // c. Neighbor's initial validity
            int leftSiteValid = -1;
            int rightSiteValid = -1;
            int downSiteValid = -1;
            int upSiteValid = -1;

            // d. Neighbor's initial status
            byte leftSiteStatus = INVALID;
            byte rightSiteStatus = INVALID;
            byte downSiteStatus = INVALID;
            byte upSiteStatus = INVALID;

            // Check if it's a valid neighbor from the position in the matrix
            if (leftCol >= 1 && leftCol <= n) {
                leftSiteID = mapXyTo1D(row, leftCol);
                leftSiteValid = 1;
                leftSiteStatus = site[row - 1][leftCol - 1];
            }
            if (rightCol >= 1 && rightCol <= n) {
                rightSiteID = mapXyTo1D(row, rightCol);
                rightSiteValid = 1;
                rightSiteStatus = site[row - 1][rightCol - 1];
            }
            if (downRow >= 1 && downRow <= n) {
                downSiteID = mapXyTo1D(downRow, col);
                downSiteValid = 1;
                downSiteStatus = site[downRow - 1][col - 1];
            }
            if (upRow >= 1 && upRow <= n) {
                upSiteID = mapXyTo1D(upRow, col);
                upSiteValid = 1;
                upSiteStatus = site[upRow - 1][col - 1];
            }

            //
            currSiteStatus = connectWithNeighbor(currSiteID, currSiteStatus, leftSiteID,
                                                 leftSiteValid, leftSiteStatus);
            currSiteStatus = connectWithNeighbor(currSiteID, currSiteStatus, rightSiteID,
                                                 rightSiteValid, rightSiteStatus);
            currSiteStatus = connectWithNeighbor(currSiteID, currSiteStatus, upSiteID,
                                                 upSiteValid, upSiteStatus);
            connectWithNeighbor(currSiteID, currSiteStatus, downSiteID, downSiteValid,
                                downSiteStatus);

            // Corner case: n = 1
            if (n == 1)
                percolationStatus = true;
        }
    }

    private byte connectWithNeighbor(int currSiteID, byte currSiteStatus, int neighborSiteID,
                                     int neighborSiteValid,
                                     byte neighborSiteStatus) {
        int rootSiteID, rootSiteIDNew;
        int[] rootSite, rootSiteNew;
        int rootSiteRow, rootSiteRowNew;
        int rootSiteCol, rootSiteColNew;
        byte rootSiteStatus;

        // If it is a valid neighbor and is not blocked, then proceed
        if ((neighborSiteValid == 1) && (neighborSiteStatus != BLOCKED)) {
            // Get root ID of the neighbor. All connected components will have the same root ID
            rootSiteID = uf.find(neighborSiteID);

            // Locate the "root" in the matrix
            rootSite = map1DToXy(rootSiteID);
            rootSiteRow = rootSite[0];
            rootSiteCol = rootSite[1];

            // Get the status of the root site
            rootSiteStatus = site[rootSiteRow - 1][rootSiteCol - 1];

            // Connect the current ID to its neighbor
            uf.union(currSiteID, neighborSiteID);

            // After "union" operation, because of the nature of WQUUF, "rootSiteID" may change
            // So, "find" is needed to get the new "rootSiteID"
            rootSiteIDNew = uf.find(currSiteID);

            // Locate the "new root" in the matrix
            rootSiteNew = map1DToXy(rootSiteIDNew);
            rootSiteRowNew = rootSiteNew[0];
            rootSiteColNew = rootSiteNew[1];

            //
            if ((currSiteStatus == OPEN_TOP) && (rootSiteStatus == OPEN_BOTTOM)) {
                percolationStatus = true;
                site[rootSiteRowNew - 1][rootSiteColNew - 1] = currSiteStatus;
            }
            else if ((currSiteStatus == OPEN_TOP) && (rootSiteStatus == OPEN)) {
                site[rootSiteRowNew - 1][rootSiteColNew - 1] = currSiteStatus;
            }
            else if ((currSiteStatus == OPEN_BOTTOM) && (rootSiteStatus == OPEN_TOP)) {
                percolationStatus = true;
                site[rootSiteRowNew - 1][rootSiteColNew - 1] = rootSiteStatus;
            }
            else if ((currSiteStatus == OPEN_BOTTOM) && (rootSiteStatus == OPEN)) {
                site[rootSiteRowNew - 1][rootSiteColNew - 1] = currSiteStatus;
            }
            else if ((currSiteStatus == OPEN) && (rootSiteStatus == OPEN_TOP)) {
                site[rootSiteRowNew - 1][rootSiteColNew - 1] = rootSiteStatus;
            }
            else if ((currSiteStatus == OPEN) && (rootSiteStatus == OPEN_BOTTOM)) {
                site[rootSiteRowNew - 1][rootSiteColNew - 1] = rootSiteStatus;
            }

            return site[rootSiteRowNew - 1][rootSiteColNew - 1];
        }
        return currSiteStatus;
    }

    public boolean isOpen(int row, int col) {
        validate(row, col);
        int siteStaus = site[row - 1][col - 1];
        if (siteStaus == BLOCKED)
            return false;
        return true;
    }

    public boolean isFull(int row, int col) {
        validate(row, col);
        boolean fullSiteStatus;
        int currSiteID = mapXyTo1D(row, col);
        int rootSiteID = uf.find(currSiteID);
        int[] rootSite = map1DToXy(rootSiteID);
        int rootSiteRow = rootSite[0];
        int rootSiteCol = rootSite[1];
        int rootSiteStatus = site[rootSiteRow - 1][rootSiteCol - 1];

        if (rootSiteStatus == OPEN_TOP)
            fullSiteStatus = true;
        else
            fullSiteStatus = false;
        return fullSiteStatus;
    }

    public int numberOfOpenSites() {
        return countOpenSites;
    }

    public boolean percolates() {
        return percolationStatus;
    }

    public static void main(String[] args) {
        // // input1.txt
        // int n = 1;
        // Percolation obj = new Percolation(n);
        // System.out.println("[BEFORE] isOpen " + obj.isOpen(1, 1));
        // System.out.println("[BEFORE] isFull " + obj.isFull(1, 1));
        // System.out.println("[BEFORE] Number of open sites: " + obj.numberOfOpenSites());
        // System.out.println("[BEFORE] Percolates: " + obj.percolates());
        // obj.open(1, 1);
        // System.out.println("[AFTER] isOpen " + obj.isOpen(1, 1));
        // System.out.println("[AFTER] isFull " + obj.isFull(1, 1));
        // System.out.println("[AFTER] Number of open sites: " + obj.numberOfOpenSites());
        // System.out.println("[AFTER] Percolates: " + obj.percolates());

        // // Test with input5.txt
        // In in = new In(args[0]);
        // int n = in.readInt();
        // Percolation perc = new Percolation(n);
        // while (!in.isEmpty()) {
        //     int i = in.readInt();
        //     int j = in.readInt();
        //     perc.open(i, j);
        //     System.out.println(
        //             "(" + i + ", " + j + ") isOpen: " + perc.isOpen(i, j) + "\tisFull: " + perc
        //                     .isFull(i, j));
        // }
        // System.out.println("*************************");
        // for (int i = 1; i <= n; i++) {
        //     for (int j = 1; j <= n; j++) {
        //         System.out.println(
        //                 "(" + i + ", " + j + ") isOpen: " + perc.isOpen(i, j) + "\tisFull: " + perc
        //                         .isFull(i, j));
        //     }
        // }
        // System.out.println("Number of open sites: " + perc.numberOfOpenSites());
        // System.out.println("Percolates: " + perc.percolates());
    }
}
