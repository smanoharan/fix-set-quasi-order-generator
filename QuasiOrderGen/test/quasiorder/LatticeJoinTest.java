package quasiorder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class LatticeJoinTest extends LatticeFixture
{
    public LatticeJoinTest(String title) { super(title); }

    @Test
    public void assertLatticeJoinIsCorrect()
    {
        assertTableEquals(title, expectedJoin, Lattice.DetermineJoins(lattice, latOrder), latOrder);
    }

    @Parameterized.Parameters
    public static Collection TestCases() { return Arrays.asList(LatticeFixture.TestLattices); }
}
