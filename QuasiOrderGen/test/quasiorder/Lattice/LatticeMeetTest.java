package quasiorder.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.LatticeUtil;

@RunWith(value = Parameterized.class)
public class LatticeMeetTest extends LatticeFixture
{
    public LatticeMeetTest(String title) { super(title); }

    @Test
    public void assertLatticeMeetIsCorrect()
    {
        assertTableEquals(title, expectedMeet, LatticeUtil.DetermineMeets(lattice, latOrder), latOrder);
    }
}
