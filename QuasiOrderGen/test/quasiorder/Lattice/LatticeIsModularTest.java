package quasiorder.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.LatticeUtil;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeIsModularTest extends LatticeFixture
{
    public LatticeIsModularTest(String latticeTitle) { super(latticeTitle); }

    @Test
    public void assertLatticeModularity()
    {
        LatticeUtil lat = new LatticeUtil(lattice, latOrder);
        assertEquals(title, isModular, lat.IsModular());
        assertEquals(title + "-x", NonModularXElem, lat.NonModularXElem);
        assertEquals(title + "-a", NonModularAElem, lat.NonModularAElem);
        assertEquals(title + "-b", NonModularBElem, lat.NonModularBElem);
        assertEquals(title + "-xVa", NonModularAXJoinElem, lat.NonModularAXJoinElem);
        assertEquals(title + "-a^b", NonModularABMeetElem, lat.NonModularABMeetElem);
    }
}
