package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import quasiorder.MeetJoinDeterminedLattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeJoinReducibleTest extends LatticeFixture
{
    public LatticeJoinReducibleTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeJoinReducibility()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colours, cur.subGraphs);
        MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
        assertEquals(cur.title, cur.joinReducible, mjdLat.JoinReducibles());
    }
}
