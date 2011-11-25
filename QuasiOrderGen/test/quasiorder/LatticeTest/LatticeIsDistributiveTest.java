package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeIsDistributiveTest extends LatticeFixture
{

    public LatticeIsDistributiveTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeDistributivity()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colors);
        assertEquals(cur.title, cur.isDistributive, lat.IsDistributive());
        assertEquals(cur.title + "-x", cur.NonDistXElem, lat.NonDistXElem);
        assertEquals(cur.title + "-y", cur.NonDistYElem, lat.NonDistYElem);
        assertEquals(cur.title + "-z", cur.NonDistZElem, lat.NonDistZElem);
        assertEquals(cur.title + "-y^z", cur.NonDistYZMeetElem, lat.NonDistYZMeetElem);
        assertEquals(cur.title + "-xVy", cur.NonDistXYJoinElem, lat.NonDistXYJoinElem);
        assertEquals(cur.title + "-xVz", cur.NonDistXZJoinElem, lat.NonDistXZJoinElem);

    }
}
