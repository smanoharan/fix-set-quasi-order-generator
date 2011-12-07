package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import quasiorder.MeetJoinDeterminedLattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeNodeAttributeTest extends LatticeFixture
{
    public LatticeNodeAttributeTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertMeetJoinDeterminedLatticeNodeAttributes()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colours, cur.subGraphs);
        MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
        for(int i=0;i<cur.latOrder;i++)
            assertEquals(cur.title + "-" + i, cur.nodeAttr[i], mjdLat.nodeAttr[i]);
    }

    @Test
    public void assertLatticeNodeAttributes()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colours, cur.subGraphs);
        for(int i=0;i<cur.latOrder;i++)
            assertEquals(cur.title + "-" + i, cur.colours[i], lat.colours[i]);
    }
}
