package quasiorder.LatticeTest.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;

@RunWith(value = Parameterized.class)
public class LatticeJoinTest extends LatticeFixture
{
    public LatticeJoinTest(String title) { super(title); }

    @Test
    public void assertLatticeJoinIsCorrect()
    {
        assertTableEquals(title, expectedJoin, Lattice.DetermineJoins(lattice, latOrder), latOrder);
    }
}
