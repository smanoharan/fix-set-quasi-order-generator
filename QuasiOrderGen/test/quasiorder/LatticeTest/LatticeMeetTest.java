package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;

@RunWith(value = Parameterized.class)
public class LatticeMeetTest extends LatticeFixture
{
    public LatticeMeetTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeMeetIsCorrect()
    {
        assertTableEquals(cur.title, cur.expectedMeet, Lattice.DetermineMeets(cur.lattice, cur.latOrder), cur.latOrder);
    }
}
