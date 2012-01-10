package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import quasiorder.MeetJoinDeterminedLattice;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class IsALatticeTest extends LatticeFixture
{
    public IsALatticeTest(LatticeTestCase curLat) { super(curLat); }

    @Test
    public void TestWholePosetIsALattice()
    {
        AssertLatticeIs("Whole-poset", cur.isCollapsedALattice, cur.CollapsedRelation,
                cur.CollapsedLatOrder, cur.wholeNotLatI, cur.wholeNotLatJ,
                cur.wholeNotLatK, cur.wholeNotLatM, cur.wholeIsLatMessage, cur.names);
    }

    @Test
    public void TestPosetFilteredByFaithfulNormalIsALattice()
    {
        AssertLatticeIs("Faithful-normal", cur.isFaithfulNormalCollapsedALattice,
                cur.CollapsedFaithfulNormalRelation, cur.CollapsedFaithfulNormalLatOrder,
                cur.faithfulNormalNotLatI, cur.faithfulNormalNotLatJ, cur.faithfulNormalNotLatK,
                cur.faithfulNormalNotLatM, cur.faithfulNormalIsLatMessage, cur.FilteredFaithfulNames);
    }

    private void AssertLatticeIs(String testCaseTitle, boolean expectedIsALattice, BitSet poset,
                                 int latOrder, int expI, int expJ, int expK, int expM,
                                 String expMsg, String[] names)
    {
        String testTitle = cur.title + testCaseTitle;
        assertEquals(testTitle + "-overall", expectedIsALattice, MeetJoinDeterminedLattice.IsALattice(poset, latOrder));
        List<Integer> expected = Arrays.asList(expI, expJ, expK, expM);
        List<Integer> actual = Arrays.asList(MeetJoinDeterminedLattice.NotLatI, MeetJoinDeterminedLattice.NotLatJ,
                MeetJoinDeterminedLattice.NotLatK, MeetJoinDeterminedLattice.NotLatM);
        assertTrue(testTitle + "exp <= act", actual.containsAll(expected));
        assertTrue(testTitle + "act <= exp", expected.containsAll(actual));
        assertEquals(testTitle + "-isLat-message", expMsg, MeetJoinDeterminedLattice.LatCheckMessage(poset, latOrder, names).message);
    }
}
