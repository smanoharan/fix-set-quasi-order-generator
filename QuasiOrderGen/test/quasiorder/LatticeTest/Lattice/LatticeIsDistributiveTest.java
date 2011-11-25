package quasiorder.LatticeTest.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeIsDistributiveTest extends LatticeFixture
{

    public LatticeIsDistributiveTest(String latticeTitle) { super(latticeTitle); }

    @Test
    public void assertLatticeDistributivity()
    {
        Lattice lat = new Lattice(lattice, latOrder, names, null);
        assertEquals(title, isDistributive, lat.IsDistributive());
        assertEquals(title + "-x", NonDistXElem, lat.NonDistXElem);
        assertEquals(title + "-y", NonDistYElem, lat.NonDistYElem);
        assertEquals(title + "-z", NonDistZElem, lat.NonDistZElem);
        assertEquals(title + "-y^z", NonDistYZMeetElem, lat.NonDistYZMeetElem);
        assertEquals(title + "-xVy", NonDistXYJoinElem, lat.NonDistXYJoinElem);
        assertEquals(title + "-xVz", NonDistXZJoinElem, lat.NonDistXZJoinElem);

    }
}
