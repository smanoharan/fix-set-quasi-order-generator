package quasiorder;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class PermutationTableTest
{
    private static int[] p(int a, int b) { return new int[]{a,b}; };
    private static int[][] p(int[] ... a) { return a; };

    private static Permutation ToPermutation(int ... twoSwaps)
    {
        ArrayList<TwoSwap> permutation = new ArrayList<TwoSwap>();

        for(int i=1;i<twoSwaps.length;i+=2)
            permutation.add(new TwoSwap(twoSwaps[i-1],twoSwaps[i]));

        return new Permutation(permutation);
    }

    @Test
    public void TestPermutationWithOnly2Cycles()
    {
        int[][][] pTable = new int[][][]
        {
            p(p(1, 1), p(0, 0), p(2, 4), p(3, 5), p(4, 2), p(5, 3), p(6, 7), p(7, 6)),
            p(p(0, 0), p(1, 1), p(2, 5), p(3, 4), p(4, 3), p(5, 2), p(6, 7), p(7, 6)),
            p(p(0, 0), p(1, 1), p(2, 5), p(3, 4), p(4, 3), p(5, 2), p(6, 6), p(7, 7))
        };

        ArrayList<Permutation> expectedPermutations = new ArrayList<Permutation>();
        expectedPermutations.add(ToPermutation(6,7, 3,5, 2,4));
        expectedPermutations.add(ToPermutation(6,7, 3,4, 2,5));
        expectedPermutations.add(ToPermutation(3,4, 2,5));

        ArrayList<Permutation> actualPermutations = Permutation.FromPermutationTable(pTable);
        AssertPermutationListsAreEqual("2 Cycles", expectedPermutations, actualPermutations);
    }

    @Test
    public void TestPermutationWith3Cycles()
    {
        int[][][] pTable = new int[][][]
        {
            p(p(1, 1), p(0, 0), p(2, 4), p(3, 2), p(4, 3), p(5, 5), p(6, 7), p(7, 6)),
            p(p(0, 0), p(1, 1), p(2, 5), p(3, 2), p(4, 6), p(5, 3), p(6, 7), p(7, 4)),
            p(p(0, 1), p(1, 0), p(2, 3), p(3, 4), p(4, 2), p(5, 6), p(6, 7), p(7, 5))
        };

        ArrayList<Permutation> expectedPermutations = new ArrayList<Permutation>();
        expectedPermutations.add(ToPermutation(6,7, 3,4, 2,4));
        expectedPermutations.add(ToPermutation(6,7, 4,6, 3,5, 2,5));
        expectedPermutations.add(ToPermutation(6,7, 5,6, 3,4, 2,3, 0,1));

        ArrayList<Permutation> actualPermutations = Permutation.FromPermutationTable(pTable);
        AssertPermutationListsAreEqual("3 Cycles", expectedPermutations, actualPermutations);
    }

    @Test
    public void TestPermutationWith4Cycles()
    {
        int[][][] pTable = new int[][][]
        {
            p(p(1, 1), p(0, 0), p(2, 5), p(3, 4), p(4, 2), p(5, 3), p(6, 7), p(7, 6)),
            p(p(0, 0), p(1, 1), p(2, 4), p(3, 5), p(4, 3), p(5, 2), p(6, 6), p(7, 7)),
            p(p(0, 1), p(1, 2), p(2, 3), p(3, 0), p(4, 5), p(5, 6), p(6, 7), p(7, 4))
        };

        ArrayList<Permutation> expectedPermutations = new ArrayList<Permutation>();
        expectedPermutations.add(ToPermutation(6,7, 3,4, 3,5, 2,5));
        expectedPermutations.add(ToPermutation(3,5, 3,4, 2,4));
        expectedPermutations.add(ToPermutation(6,7, 5,6, 4,5, 2,3, 1,2, 0,1));

        ArrayList<Permutation> actualPermutations = Permutation.FromPermutationTable(pTable);
        AssertPermutationListsAreEqual("4 Cycles", expectedPermutations, actualPermutations);
    }

    private static void AssertPermutationListsAreEqual(String title, ArrayList<Permutation> expected, ArrayList<Permutation> actual)
    {
        assertEquals(title + "-numPermutations", expected.size(), actual.size());
        for(int i=0;i<expected.size();i++)
            AssertPermutationsAreEqual(title + "-" + i, expected.get(i), actual.get(i));
    }

    private static void AssertPermutationsAreEqual(String title, Permutation expected, Permutation actual)
    {
        assertEquals(title + "-numTwoSwaps", expected.swaps.size(), actual.swaps.size());
        for(int i=0;i<expected.swaps.size();i++)
            AssertSwapsAreEqual(title + "-" + i, expected.swaps.get(i), actual.swaps.get(i));
    }

    private static void AssertSwapsAreEqual(String title, TwoSwap a, TwoSwap b)
    {
        int a1 = a.i;
        int a2 = a.j;
        if (a1 > a2) { int a3 = a1; a1 = a2; a2 = a3; }

        int b1 = b.i;
        int b2 = b.j;
        if (b1 > b2) { int b3 = b1; b1 = b2; b2 = b3; }

        assertEquals(title + "-min", a1, b1);
        assertEquals(title + "-max", a2, b2);
    }
}
