package quasiorder.LatticeTest.Lattice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeModDistCheckMessageTest extends LatticeFixture
{
    public LatticeModDistCheckMessageTest(String latticeTitle) { super(latticeTitle); }

    @Test
    public void assertLatticeModularity()
    {
        Lattice lat = new Lattice(lattice, latOrder, names, null);
        assertEquals(title, expectedModDistMessage, lat.ModDistCheckMessage());
    }
}
