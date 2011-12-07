package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import quasiorder.MeetJoinDeterminedLattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeIsModularTest extends LatticeFixture
{
    public LatticeIsModularTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeModularity()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colours, cur.subGraphs);
        MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
        assertEquals(cur.title, cur.isModular, mjdLat.IsModular());
        assertEquals(cur.title + "-x", cur.NonModularXElem, mjdLat.NonModularXElem);
        assertEquals(cur.title + "-a", cur.NonModularAElem, mjdLat.NonModularAElem);
        assertEquals(cur.title + "-b", cur.NonModularBElem, mjdLat.NonModularBElem);
        assertEquals(cur.title + "-xVa", cur.NonModularAXJoinElem, mjdLat.NonModularAXJoinElem);
        assertEquals(cur.title + "-a^b", cur.NonModularABMeetElem, mjdLat.NonModularABMeetElem);
    }
}
