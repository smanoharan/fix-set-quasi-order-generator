package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeNodeAttributeTest extends LatticeFixture
{
    public LatticeNodeAttributeTest(LatticeTestCase lat) { super(lat); }

    @Test
    public void assertLatticeNodeAttributes()
    {
        Lattice lat = new Lattice(cur.lattice, cur.latOrder, cur.names, cur.colors, cur.subGraphs, cur.groupedNames, cur.groupRepNames);
        for(int i=0;i<cur.latOrder;i++)
            assertEquals(cur.title + "-" + i, cur.nodeAttr[i], lat.nodeAttrs[i]);
    }
}
