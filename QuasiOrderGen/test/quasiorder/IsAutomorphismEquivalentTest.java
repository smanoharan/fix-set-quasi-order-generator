package quasiorder;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class IsAutomorphismEquivalentTest extends QuasiOrderGenFixture
{
    private static ArrayList<Permutation> permutations;
    private static ArrayList<Permutation> evenPermutations;
    private static int numElem = 3;
    private static final int G1SIZE = 4;
    private static final int G2SIZE = 3;

    BitSet[] G1;
    BitSet[] G2;
    ArrayList<FixOrder> fixOrders;

    @BeforeClass
    public static void SetupPermutations()
    {
        // allow all even permutations 
        permutations = new ArrayList<Permutation>();
        permutations.add(FromString("01"));
        permutations.add(FromString("02"));
        permutations.add(FromString("12"));
        permutations.add(FromString("012"));
        permutations.add(FromString("021"));

        evenPermutations = new ArrayList<Permutation>();
        evenPermutations.add(FromString("012"));
        evenPermutations.add(FromString("021"));
    }

    private static Permutation FromString(String s)
    {
        ArrayList<TwoSwap> swaps = new ArrayList<TwoSwap>();
        for (int i=s.length()-1;i>0;i--)
        {
            int d1 = Integer.parseInt(String.valueOf(s.charAt(i-1)));
            int d2 = Integer.parseInt(String.valueOf(s.charAt(i)));

            if (d2 < d1) { int t = d1; d1 = d2; d2 = t; }

            swaps.add(new TwoSwap(d1, d2));
        }
        return new Permutation(swaps);
    }

    @Before
    public void Setup()
    {
        // Group 1: (related by odd permutations only)
        // orig     swap(0,1)   swap(0,2)   swap(1,2)
        // 110      100         101         101
        // 010      110         010         110
        // 101      011         011         001
        G1 = new BitSet[G1SIZE];
        G1[0] = StringToBitSet("110"+"010"+"101");
        G1[1] = StringToBitSet("100"+"110"+"011");
        G1[2] = StringToBitSet("101"+"010"+"011");
        G1[3] = StringToBitSet("101"+"110"+"001");

        // Group 2: (related by even permutations)
        //  orig    swap(0,1,2) swap(0,2,1)
        //  100     100         101
        //  110     010         010
        //  001     011         001
        G2 = new BitSet[G2SIZE];
        G2[0] = StringToBitSet("100"+"110"+"001");
        G2[1] = StringToBitSet("100"+"010"+"011");
        G2[2] = StringToBitSet("101"+"010"+"001");

        // add all to fix orders
        fixOrders = new ArrayList<FixOrder>();
        for (int i=0;i<G1SIZE;i++) fixOrders.add(new FixOrder(G1[i], true, true));
        for (int i=0;i<G2SIZE;i++) fixOrders.add(new FixOrder(G2[i], true, true));
    }

    @Test
    public void TestGroupByPartitionsAllWhenPermutationsAreEmpty()
    {
        AssertPartitionsAre(new ArrayList<Permutation>(), "Empty", new int[] {0, 1, 2, 3, 4, 5, 6});
    }

    @Test
    public void TestGroupByPartitionsBySizeWhenPermutationsAreFull()
    {
        AssertPartitionsAre(permutations, "All", new int[]{}, new int[]{0, 1, 2, 3}, new int[] {4, 5, 6});
    }

    @Test
    public void TestGroupByPartitionsWhenPermutationsAreEvenOnly()
    {
        AssertPartitionsAre(evenPermutations, "Even", new int[]{0}, new int[]{1, 2, 3}, new int[]{4, 5, 6});
    }

    private void AssertPartitionsAre(ArrayList<Permutation> perms, String title, int[] ... expected)
    {
        LinkedList<ArrayList<Integer>> actual = Generate.AutomorphismHandler.PartitionBy(fixOrders, perms, numElem);
        assertEquals(title+"-numPartitions", actual.size(), expected.length);
        int ci = 0;
        for (ArrayList<Integer> act : actual)
        {
            int[] exp = expected[ci];
            assertEquals(title+"-lengthOfpart-"+ci, exp.length, act.size());

            for(int j=0;j<exp.length;j++)
                assertEquals(title+"-part-"+ci+"-"+j, exp[j], act.get(j).intValue());

            ci++;
        }
    }

    @Test
    public void TestThatAllOfG1IsEquivalent()
    {
        assertAllOfArrayIsEquivalent("Group1", permutations, G1);

        // G1 is unrelated to anything else by even
        for(int i=1;i<G1SIZE;i++) 
            assertFalse("Group1:[0][" + i + "]:even", Generate.AutomorphismHandler.isAutomorphismEquivalent(G1[0], G1[i], evenPermutations, numElem));

        // all others are related together by even:
        assertAllOfArrayIsEquivalent("Group1:<0", evenPermutations, G1[1], G1[2], G1[3]);
    }

    @Test
    public void TestThatAllOfG2IsEquivalent()
    {
        assertAllOfArrayIsEquivalent("Group2", permutations, G2);
        assertAllOfArrayIsEquivalent("Group2", evenPermutations, G2);
    }

    private static void assertAllOfArrayIsEquivalent(String name, ArrayList<Permutation> ps, BitSet ... arr)
    {
        int size = arr.length;
        for(int i=0;i<size;i++)
            for(int j=i+1;j<size;j++)
                assertTrue(String.format("%s[%d][%d]:all", name, i, j),
                        Generate.AutomorphismHandler.isAutomorphismEquivalent(arr[i], arr[j], ps, numElem));
    }

    @Test
    public void TestThatG1AndG2AreUnrelated()
    {
        for(int i=0;i<G1SIZE;i++)
        {
            for(int j=0;j<G2SIZE;j++)
            {
                assertFalse(String.format("G1[%d]:G2[%d]:all", i, j), Generate.AutomorphismHandler.isAutomorphismEquivalent(G1[i], G2[j], permutations, numElem));
                assertFalse(String.format("G1[%d]:G2[%d]:even", i, j), Generate.AutomorphismHandler.isAutomorphismEquivalent(G1[i], G2[j], evenPermutations, numElem));
            }
        }
    }
}
