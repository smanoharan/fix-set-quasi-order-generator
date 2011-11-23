package quasiorder.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.LatticeUtil;

@RunWith(value = Parameterized.class)
public class LatticeJoinTest extends LatticeFixture
{
    public LatticeJoinTest(String title) { super(title); }

    @Test
    public void assertLatticeJoinIsCorrect()
    {
        assertTableEquals(title, expectedJoin, LatticeUtil.DetermineJoins(lattice, latOrder), latOrder);
    }
}
