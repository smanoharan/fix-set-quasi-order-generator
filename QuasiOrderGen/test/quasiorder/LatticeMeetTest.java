package quasiorder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(value = Parameterized.class)
public class LatticeMeetTest extends LatticeFixture
{
    public LatticeMeetTest(String title) { super(title); }

    @Test
    public void assertLatticeMeetIsCorrect()
    {
        assertTableEquals(title, expectedMeet, Lattice.DetermineMeets(lattice, latOrder), latOrder);
    }

    @Parameterized.Parameters
    public static Collection TestCases() { return LatticeFixture.TestLattices; }
}
