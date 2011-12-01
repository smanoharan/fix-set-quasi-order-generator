package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeIsModularTest extends LatticeFixture
{
    public LatticeIsModularTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeModularity()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colors, cur.subgraphs);
        assertEquals(cur.title, cur.isModular, lat.IsModular());
        assertEquals(cur.title + "-x", cur.NonModularXElem, lat.NonModularXElem);
        assertEquals(cur.title + "-a", cur.NonModularAElem, lat.NonModularAElem);
        assertEquals(cur.title + "-b", cur.NonModularBElem, lat.NonModularBElem);
        assertEquals(cur.title + "-xVa", cur.NonModularAXJoinElem, lat.NonModularAXJoinElem);
        assertEquals(cur.title + "-a^b", cur.NonModularABMeetElem, lat.NonModularABMeetElem);
    }
}
