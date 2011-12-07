package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeFilterByTest extends LatticeFixture
{
    public LatticeFilterByTest(LatticeTestCase curLat) { super(curLat); }

    @Test
    public void assertFilterByAll()
    {
        AssertLatticeIs("-all-", false, false,
                cur.latOrder,
                cur.lattice,
                cur.names,
                cur.colours,
                cur.subGraphs);
    }

    @Test
    public void assertFilterByFaithfulOnly()
    {
        AssertLatticeIs("-faithfulOnly-", true, false,
                cur.FilteredFaithfulLatOrder,
                cur.FilteredFaithfulRelation,
                cur.FilteredFaithfulNames,
                cur.FilteredFaithfulColours,
                cur.FilteredFaithfulSubGraphs);
    }

    @Test
    public void assertFilterByNormalOnly()
    {
        AssertLatticeIs("-normalOnly-", false, true,
                cur.FilteredNormalLatOrder,
                cur.FilteredNormalRelation,
                cur.FilteredNormalNames,
                cur.FilteredNormalColours,
                cur.FilteredNormalSubGraphs);
    }

    @Test
    public void assertFilterByFaithfulNormalOnly()
    {
        AssertLatticeIs("-faithfulNormalOnly-", true, true,
                cur.FilteredFaithfulNormalLatOrder,
                cur.FilteredFaithfulNormalRelation,
                cur.FilteredFaithfulNormalNames,
                cur.FilteredFaithfulNormalColours,
                cur.FilteredFaithfulNormalSubGraphs);
    }

    private void AssertLatticeIs(String testCaseTitle, boolean faithfulOnly, boolean normalOnly, int latOrder,
                               BitSet latBit, String[] names, String[] colours, LinkedList<ArrayList<Integer>> subgraphs)
    {
        String testTitle = cur.title + testCaseTitle;
        Lattice lat = filterBy(faithfulOnly, normalOnly, cur);
        assertEquals(testTitle + "latOrder", latOrder, lat.latOrder);
        assertEquals(testTitle + "relation", latBit, lat.latBit);
        AssertArrayEquals(testTitle + "names", names, lat.names, latOrder);
        AssertArrayEquals(testTitle + "colours", colours, lat.colours, latOrder);
        AssertListOfListEquals(testTitle + "subGraphs", subgraphs, lat.subGraphs);
    }

    private static Lattice filterBy(boolean faithfulOnly, boolean normalOnly, LatticeTestCase cur)
    {
        BitSet include = Lattice.includeBy(cur.FilteringRelations, faithfulOnly, normalOnly);
        return Lattice.FilterBy(cur.latOrder, cur.names, cur.colours, cur.lattice, cur.subGraphs, include);
    }

    private static void AssertListOfListEquals(String title, LinkedList<ArrayList<Integer>> expected, LinkedList<ArrayList<Integer>> actual)
    {
        assertEquals(title + "-numParts", expected.size(), actual.size());
        for(int i=0;i<expected.size();i++)
        {
            // get(i) on linked-list is slow, but this is not a problem as the lists are usually small.
            ArrayList<Integer> exp = expected.get(i);
            ArrayList<Integer> act = actual.get(i);
            assertEquals(title + "-partLength-"+i, exp.size(), act.size());
            for(int j=0;j<exp.size();j++)
                assertEquals(title+"-part-" + i + "-" + j, act.get(j), exp.get(j));
        }

    }
}
