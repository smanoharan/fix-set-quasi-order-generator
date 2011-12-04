package quasiorder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuasiOrderGenFixture
{
    // 5 elements : { A, B, C, D, E }
    protected static final int NumElem = 5;
    protected static final int IA = 0;
    protected static final int IB = 1;
    protected static final int IC = 2;
    protected static final int ID = 3;
    protected static final int IE = 4;

    protected BitSet[] elementMasks;

    public static BitSet StringToBitSet(String BitString)
    {
        BitSet result = new BitSet(BitString.length());

        for(int i=0;i<BitString.length();i++)
            if (BitString.charAt(i)=='1')
                result.set(i);

        return result;
    }

    static <T> void assertListEqual(Collection<T> actual, T... expected)
    {
        List<T> expectedList = new ArrayList<T>(expected.length);
        for(T b : expected) expectedList.add(b);

        assertEquals(expectedList.size(), actual.size());
        assertTrue(expectedList.containsAll(actual));
        assertTrue(actual.containsAll(expectedList));
    }

    static void assertRelationEqual(Group input, String familyMask, String expectedRel)
    {
        String messsage = "familyMask: " + familyMask + " expectedRel: " + expectedRel;
        BitSet actual = FixOrderSet.BuildRelation(input, StringToBitSet(familyMask));
        assertEquals(messsage, StringToBitSet(expectedRel), actual);
    }

    static FixOrder ToFixOrder(BitSet b)
    {
        return new FixOrder(b, true, true);
    }

    public static <T> void AssertArrayEquals(String title, T[] expected, T[] actual, int numElem)
    {
        for(int i=0;i<numElem;i++)
            assertEquals(title + "-" + i, expected[i], actual[i]);
    }

    static void AssertPermutationListsAreEqual(String title, ArrayList<Permutation> expected, ArrayList<Permutation> actual)
    {
        assertEquals(title + "-numPermutations", expected.size(), actual.size());
        for(int i=0;i<expected.size();i++)
            AssertPermutationsAreEqual(title + "-" + i, expected.get(i), actual.get(i));
    }

    static void AssertPermutationsAreEqual(String title, Permutation expected, Permutation actual)
    {
        assertEquals(title + "-numTwoSwaps", expected.swaps.size(), actual.swaps.size());
        for(int i=0;i<expected.swaps.size();i++)
            AssertSwapsAreEqual(title + "-" + i, expected.swaps.get(i), actual.swaps.get(i));
    }

    static void AssertSwapsAreEqual(String title, TwoSwap a, TwoSwap b)
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

    static int[] toPair(int a, int b) { return new int[]{a,b}; }

    static Permutation ToPermutation(int... twoSwaps)
    {
        ArrayList<TwoSwap> permutation = new ArrayList<TwoSwap>();

        for(int i=1;i<twoSwaps.length;i+=2)
            permutation.add(new TwoSwap(twoSwaps[i-1],twoSwaps[i]));

        return new Permutation(permutation);
    }
}
