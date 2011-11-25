package quasiorder.LatticeTest.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeIsModularTest extends LatticeFixture
{
    public LatticeIsModularTest(String latticeTitle) { super(latticeTitle); }

    @Test
    public void assertLatticeModularity()
    {
        Lattice lat = new Lattice(lattice, latOrder, names, null);
        assertEquals(title, isModular, lat.IsModular());
        assertEquals(title + "-x", NonModularXElem, lat.NonModularXElem);
        assertEquals(title + "-a", NonModularAElem, lat.NonModularAElem);
        assertEquals(title + "-b", NonModularBElem, lat.NonModularBElem);
        assertEquals(title + "-xVa", NonModularAXJoinElem, lat.NonModularAXJoinElem);
        assertEquals(title + "-a^b", NonModularABMeetElem, lat.NonModularABMeetElem);
    }
}
