import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private int width;
    private int height;
    private final int[][] pixelMatrix;
    private final double[][] energyMatrix;

    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();

        width = picture.width();
        height = picture.height();
        pixelMatrix = new int[width][height];
        energyMatrix = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelMatrix[x][y] = picture.getRGB(x, y);
                energyMatrix[x][y] = calculateEnergy(picture, x, y);
            }
        }
    }

    public Picture picture() {
        Picture p = new Picture(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                p.setRGB(x, y, pixelMatrix[x][y]);
            }
        }

        return p;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double energy(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height -1)
            throw new IllegalArgumentException();

        return energyMatrix[x][y];
    }

    public int[] findHorizontalSeam() {
        int[] seam = new int[width];
        int[][] edgeTo = new int[width][height];
        double[][] distTo = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distTo[x][y] = Double.POSITIVE_INFINITY;
            }
        }

        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0) distTo[0][y] = 1000;

                int nextCol = x + 1;

                // we have at most 3 edges from each cell
                for (int i = -1; i <= 1; i++) {
                    int nextRow = y + i;

                    if (nextRow < 0 || nextRow >= height) continue;

                    double e = energyMatrix[nextCol][nextRow];
                    double dist = distTo[x][y] + e;

                    if (distTo[nextCol][nextRow] > dist) {
                        distTo[nextCol][nextRow] = dist;
                        edgeTo[nextCol][nextRow] = y;
                    }
                }
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int minRow = 0;

        for (int row = 0; row < height; row++) {
            if (minDist > distTo[width - 1][row]) {
                minDist = distTo[width - 1][row];
                minRow = row;
            }
        }

        int row = minRow;
        for (int col = width - 1; col >= 0; col--) {
            seam[col] = row;
            row = edgeTo[col][row];
        }

        return seam;
    }

    public int[] findVerticalSeam() {
        int[] seam = new int[height];
        int[][] edgeTo = new int[width][height];
        double[][] distTo = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distTo[x][y] = Double.POSITIVE_INFINITY;
            }
        }

        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0) distTo[x][0] = 1000;

                int nextRow = y + 1;

                // we have at most 3 edges from each cell
                for (int i = -1; i <= 1; i++) {
                    int nextCol = x + i;

                    if (nextCol < 0 || nextCol >= width) continue;

                    double e = energyMatrix[nextCol][nextRow];
                    double dist = distTo[x][y] + e;

                    if (distTo[nextCol][nextRow] > dist) {
                        distTo[nextCol][nextRow] = dist;
                        edgeTo[nextCol][nextRow] = x;
                    }
                }
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int minCol = 0;

        for (int col = 0; col < width; col++) {
            if (minDist > distTo[col][height - 1]) {
                minDist = distTo[col][height - 1];
                minCol = col;
            }
        }

        int col = minCol;
        for (int row = height - 1; row >= 0; row--) {
            seam[row] = col;
            col = edgeTo[col][row];
        }

        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width || height <= 1)
            throw new IllegalArgumentException();

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height) {
                throw new IllegalArgumentException();
            }

            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = seam[x]; y < height - 1; y++) {
                pixelMatrix[x][y] = pixelMatrix[x][y + 1];
                energyMatrix[x][y] = energyMatrix[x][y + 1];
            }
        }

        height -= 1;
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height || width <= 1)
            throw new IllegalArgumentException();

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width) {
                throw new IllegalArgumentException();
            }

            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = seam[y]; x < width - 1; x++) {
                pixelMatrix[x][y] = pixelMatrix[x + 1][y];
                energyMatrix[x][y] = energyMatrix[x + 1][y];
            }
        }

        width -= 1;
    }

    private double calculateEnergy(Picture picture, int x, int y) {
        if (x == 0 || x == this.width() - 1 || y == 0 || y == this.height() - 1) {
            return 1000;
        }

        Color left = picture.get(x - 1, y);
        Color top = picture.get(x, y - 1);
        Color right = picture.get(x + 1, y);
        Color bottom = picture.get(x, y + 1);

        return Math.sqrt(
            Math.pow(right.getRed() - left.getRed(), 2) +
            Math.pow(right.getGreen() - left.getGreen(), 2) +
            Math.pow(right.getBlue() - left.getBlue(), 2) +
            Math.pow(bottom.getRed() - top.getRed(), 2) +
            Math.pow(bottom.getGreen() - top.getGreen(), 2) +
            Math.pow(bottom.getBlue() - top.getBlue(), 2)
        );
    }

    public static void main(String[] args) {
        SeamCarver sc = new SeamCarver(new Picture("chameleon.png"));
        // 600x300
        System.out.println(sc.width() + "x" + sc.height());

        double[][] energy = new double[sc.width()][sc.height()];
        for (int x = 0; x < sc.width(); x++) {
            for (int y = 0; y < sc.height(); y++) {
                energy[x][y] = sc.energy(x, y);
            }
        }

        System.out.println(Arrays.deepToString(energy));
        System.out.println(Arrays.toString(sc.findHorizontalSeam()));
        System.out.println(Arrays.toString(sc.findVerticalSeam()));

        sc.picture().show();

        // rm 300 vertical seams
        for (int i = 0; i < 300; i++) {
            sc.removeVerticalSeam(sc.findVerticalSeam());
        }

        // rm 150 vertical seams
        for (int i = 0; i < 150; i++) {
            sc.removeHorizontalSeam(sc.findHorizontalSeam());
        }

        sc.picture().show();
    }
}
