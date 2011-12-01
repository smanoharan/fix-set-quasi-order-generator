package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeMeetReducibleTest extends LatticeFixture
{
    public LatticeMeetReducibleTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeJoinReducibility()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colors, cur.subGraphs, cur.groupedNames);
        assertEquals(cur.title, cur.meetReducible, lat.MeetReducibles());
    }
}
