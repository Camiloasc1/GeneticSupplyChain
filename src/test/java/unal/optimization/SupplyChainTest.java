package unal.optimization;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SupplyChainTest {

    @Test
    public void testUnwrap() {
        int[][] res = new int[3][4];
        int[] permutation = {1, 4, 6, 2, 0, 4, 5};
        int[][] cost = {{1, 6, 5, 2}, {6, 2, 4, 5}, {3, 4, 2, 1}};
        int[] offer = {100, 100, 150};
        int[] demand = {50, 150, 100, 50};

        int[][] expected = {{50, 0, 50, 0}, {0, 100, 0, 0}, {0, 50, 50, 50}};

        SupplyChain.unwrapPermutation(res, permutation, cost, offer, demand);
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                assertEquals(expected[i][j], res[i][j]);
            }
        }

        assertArrayEquals(new int[3], offer);
        assertArrayEquals(new int[4], demand);
    }

    @Test
    public void testRightUnwrap1() {
        int[][] res = new int[2][4];
        int[] permutation = {0, 1, 2, 3};
        int[][] cost = {{10, 10, 1, 1}, {1, 1, 10, 10}};
        int[] offer = {150, 150};
        int[] demand = {50, 100, 50, 100};

        int[][] expected = {{0, 0, 50, 100}, {50, 100, 0, 0}};

        SupplyChain.unwrapSingleRightPermutation(res, permutation, cost, offer, demand);
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                assertEquals(expected[i][j], res[i][j]);
            }
        }

        assertArrayEquals(new int[2], offer);
        assertArrayEquals(new int[4], demand);
    }

    @Test
    public void testRightUnwrap2() {
        int[][] res = new int[3][3];
        int[] permutation = {0, 1, 2};
        int[][] cost = {{10, 10, 10}, {10, 10, 10}, {0, 10, 10}};
        int[] offer = {0, 0, 10};
        int[] demand = {10, 0, 0};

        int[][] expected = {{0, 0, 0}, {0, 0, 0}, {10, 0, 0}};

        SupplyChain.unwrapSingleRightPermutation(res, permutation, cost, offer, demand);
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                assertEquals(expected[i][j], res[i][j]);
            }
        }

        assertArrayEquals(new int[3], offer);
        assertArrayEquals(new int[3], demand);
    }

    @Test
    public void testRightUnwrap3() {
        int[][] res = new int[3][3];
        int[] permutation = {0, 1, 2};
        int[][] cost = {{10, 10, 10}, {10, 10, 10}, {0, 10, 10}};
        int[] offer = {0, 0, 10};
        int[] demand = {11, 0, 0};

        int[][] expected = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

        SupplyChain.unwrapSingleRightPermutation(res, permutation, cost, offer, demand);
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                assertEquals(expected[i][j], res[i][j]);
            }
        }

        assertArrayEquals(new int[]{0, 0, 10}, offer);
        assertArrayEquals(new int[]{11, 0, 0}, demand);
    }
}
