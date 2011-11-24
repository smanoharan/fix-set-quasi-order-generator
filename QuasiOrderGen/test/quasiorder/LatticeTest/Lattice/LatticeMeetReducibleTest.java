package quasiorder.LatticeTest.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeMeetReducibleTest extends LatticeFixture
{
    public LatticeMeetReducibleTest(String latticeTitle) { super(latticeTitle); }

    @Test
    public void assertLatticeJoinReducibility()
    {
        Lattice lat = new Lattice(lattice, latOrder);
        assertEquals(title, meetReducible, lat.MeetReducibles());
    }
}
