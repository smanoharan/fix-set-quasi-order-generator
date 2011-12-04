package quasiorder;

import org.junit.Test;

import java.util.ArrayList;

public class PermutationTableTest extends QuasiOrderGenFixture
{
    private static int[][] p(int[] ... a) { return a; };

    @Test
    public void TestPermutationWithOnly2Cycles()
    {
        int[][][] pTable = new int[][][]
        {
            p(toPair(1, 1), toPair(0, 0), toPair(2, 4), toPair(3, 5), toPair(4, 2), toPair(5, 3), toPair(6, 7), toPair(7, 6)),
            p(toPair(0, 0), toPair(1, 1), toPair(2, 5), toPair(3, 4), toPair(4, 3), toPair(5, 2), toPair(6, 7), toPair(7, 6)),
            p(toPair(0, 0), toPair(1, 1), toPair(2, 5), toPair(3, 4), toPair(4, 3), toPair(5, 2), toPair(6, 6), toPair(7, 7))
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
            p(toPair(1, 1), toPair(0, 0), toPair(2, 4), toPair(3, 2), toPair(4, 3), toPair(5, 5), toPair(6, 7), toPair(7, 6)),
            p(toPair(0, 0), toPair(1, 1), toPair(2, 5), toPair(3, 2), toPair(4, 6), toPair(5, 3), toPair(6, 7), toPair(7, 4)),
            p(toPair(0, 1), toPair(1, 0), toPair(2, 3), toPair(3, 4), toPair(4, 2), toPair(5, 6), toPair(6, 7), toPair(7, 5))
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
            p(toPair(1, 1), toPair(0, 0), toPair(2, 5), toPair(3, 4), toPair(4, 2), toPair(5, 3), toPair(6, 7), toPair(7, 6)),
            p(toPair(0, 0), toPair(1, 1), toPair(2, 4), toPair(3, 5), toPair(4, 3), toPair(5, 2), toPair(6, 6), toPair(7, 7)),
            p(toPair(0, 1), toPair(1, 2), toPair(2, 3), toPair(3, 0), toPair(4, 5), toPair(5, 6), toPair(6, 7), toPair(7, 4))
        };

        ArrayList<Permutation> expectedPermutations = new ArrayList<Permutation>();
        expectedPermutations.add(ToPermutation(6,7, 3,4, 3,5, 2,5));
        expectedPermutations.add(ToPermutation(3,5, 3,4, 2,4));
        expectedPermutations.add(ToPermutation(6,7, 5,6, 4,5, 2,3, 1,2, 0,1));

        ArrayList<Permutation> actualPermutations = Permutation.FromPermutationTable(pTable);
        AssertPermutationListsAreEqual("4 Cycles", expectedPermutations, actualPermutations);
    }

}
