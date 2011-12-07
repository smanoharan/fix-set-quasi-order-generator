package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import quasiorder.MeetJoinDeterminedLattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeIsDistributiveTest extends LatticeFixture
{

    public LatticeIsDistributiveTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeDistributivity()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colours, cur.subGraphs);
        MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
        assertEquals(cur.title, cur.isDistributive, mjdLat.IsDistributive());
        assertEquals(cur.title + "-x", cur.NonDistXElem, mjdLat.NonDistXElem);
        assertEquals(cur.title + "-y", cur.NonDistYElem, mjdLat.NonDistYElem);
        assertEquals(cur.title + "-z", cur.NonDistZElem, mjdLat.NonDistZElem);
        assertEquals(cur.title + "-y^z", cur.NonDistYZMeetElem, mjdLat.NonDistYZMeetElem);
        assertEquals(cur.title + "-xVy", cur.NonDistXYJoinElem, mjdLat.NonDistXYJoinElem);
        assertEquals(cur.title + "-xVz", cur.NonDistXZJoinElem, mjdLat.NonDistXZJoinElem);

    }
}
