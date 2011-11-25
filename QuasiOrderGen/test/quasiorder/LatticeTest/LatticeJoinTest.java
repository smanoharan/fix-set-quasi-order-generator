package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;

@RunWith(value = Parameterized.class)
public class LatticeJoinTest extends LatticeFixture
{
    public LatticeJoinTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeJoinIsCorrect()
    {
        assertTableEquals(cur.title, cur.expectedJoin, Lattice.DetermineJoins(cur.lattice, cur.latOrder), cur.latOrder);
    }
}
