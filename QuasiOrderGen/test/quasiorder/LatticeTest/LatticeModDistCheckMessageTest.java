package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import quasiorder.MeetJoinDeterminedLattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeModDistCheckMessageTest extends LatticeFixture
{
    public LatticeModDistCheckMessageTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeModularity()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colours, cur.subGraphs);
        MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
        assertEquals(cur.title, cur.modDistMessage, mjdLat.ModDistCheckMessage());
    }
}
