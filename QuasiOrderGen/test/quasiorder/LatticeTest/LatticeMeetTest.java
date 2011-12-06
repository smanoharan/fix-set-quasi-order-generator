package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import quasiorder.MeetJoinDeterminedLattice;

@RunWith(value = Parameterized.class)
public class LatticeMeetTest extends LatticeFixture
{
    public LatticeMeetTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeMeetIsCorrect()
    {
        assertTableEquals(cur.title, cur.expectedMeet, MeetJoinDeterminedLattice.DetermineMeets(cur.lattice, cur.latOrder), cur.latOrder);
    }
}
