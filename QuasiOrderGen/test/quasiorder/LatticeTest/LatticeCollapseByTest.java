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
public class LatticeCollapseByTest extends LatticeFixture
{
    public LatticeCollapseByTest(LatticeTestCase curLat) { super(curLat); }

    @Test
    public void TestCollapseAll()
    {
        AssertCollapsedLatticeIs("-all-", false, false, cur.CollapsedLatOrder, cur.CollapsedRelation,
                cur.CollapsedGroupedNames, cur.CollapsedRepNames, cur.CollapsedSubGraphs);
    }

    @Test
    public void TestCollapseWhenFilterByFaithfulNormal()
    {
        AssertCollapsedLatticeIs("-faithful-normal-", true, true, cur.CollapsedFaithfulNormalLatOrder,
                cur.CollapsedFaithfulNormalRelation, cur.CollapsedFaithfulNormalGroupedNames,
                cur.CollapsedFaithfulNormalRepNames, cur.CollapsedFaithfulNormalSubGraphs);
    }

    private void AssertCollapsedLatticeIs(
            String testCaseTitle, boolean faithfulOnly, boolean normalOnly, int latOrder, BitSet latBit, String[] fullNames,
            String[] repNames, LinkedList<ArrayList<Integer>> subgraphs)
    {
        String testTitle = cur.title + testCaseTitle;
        BitSet include = Lattice.includeBy(cur.FilteringRelations, faithfulOnly, normalOnly);
        Lattice toCollapse = Lattice.FilterBy(cur.latOrder, cur.names, cur.colours, cur.lattice, cur.subGraphs, include);

        Lattice latFullName = Lattice.CollapseBy(toCollapse, Lattice.FullPartNameSelector);
        Lattice latRepName = Lattice.CollapseBy(toCollapse, Lattice.RepNameSelector);

        assertEquals(testTitle + "-full-latOrder", latOrder, latFullName.latOrder);
        assertEquals(testTitle + "-rep-latOrder", latOrder, latRepName.latOrder);

        assertEquals(testTitle + "-full-relation", latBit, latFullName.latBit);
        assertEquals(testTitle + "-rep-relation", latBit, latRepName.latBit);

        AssertArrayEquals(testTitle + "-full-names", fullNames, latFullName.names, latOrder);
        AssertArrayEquals(testTitle + "-rep-names", repNames, latRepName.names, latOrder);

        AssertListOfListEquals(testTitle + "-full-subGraphs", subgraphs, latFullName.subGraphs);
        AssertListOfListEquals(testTitle + "-rep-subGraphs", subgraphs, latRepName.subGraphs);
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
