package quasiorder.LatticeTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import quasiorder.Lattice;
import quasiorder.LatticeTest.TestCases.LatticeTestCase;
import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class LatticeFilterByTest extends LatticeFixture
{
    public LatticeFilterByTest(LatticeTestCase curLat) { super(curLat); }

    @Test
    public void assertFilterByAll()
    {
        String testTitle = cur.title + "-all-";
        Lattice lat = filterBy(false, false, cur);
        assertEquals(testTitle + "latOrder", cur.latOrder, lat.latOrder);
        assertEquals(testTitle + "relation", cur.lattice, lat.latBit);
        AssertArrayEquals(testTitle + "names", cur.names, lat.names, cur.latOrder);
    }

    @Test
    public void assertFilterByFaithfulOnly()
    {
        String testTitle = cur.title + "-faithfulOnly-";
        Lattice lat = filterBy(true, false, cur);
        assertEquals(testTitle + "latOrder", cur.FilteredFaithfulLatOrder, lat.latOrder);
        assertEquals(testTitle + "relation", cur.FilteredFaithfulRelation, lat.latBit);
        AssertArrayEquals(testTitle + "names", cur.FilteredFaithfulNames, lat.names, cur.FilteredFaithfulLatOrder);
    }

    @Test
    public void assertFilterByNormalOnly()
    {
        String testTitle = cur.title + "-normalOnly-";
        Lattice lat = filterBy(false, true, cur);
        assertEquals(testTitle + "latOrder", cur.FilteredNormalLatOrder, lat.latOrder);
        assertEquals(testTitle + "relation", cur.FilteredNormalRelation, lat.latBit);
        AssertArrayEquals(testTitle + "names", cur.FilteredNormalNames, lat.names, cur.FilteredNormalLatOrder);
    }

    @Test
    public void assertFilterByFaithfulNormalOnly()
    {
        String testTitle = cur.title + "-faithfulNormalOnly-";
        Lattice lat = filterBy(true, true, cur);
        assertEquals(testTitle + "latOrder", cur.FilteredFaithfulNormalLatOrder, lat.latOrder);
        assertEquals(testTitle + "relation", cur.FilteredFaithfulNormalRelation, lat.latBit);
        AssertArrayEquals(testTitle + "names", cur.FilteredFaithfulNormalNames, lat.names, cur.FilteredFaithfulNormalLatOrder);
    }

    private static Lattice filterBy(boolean faithfulOnly, boolean normalOnly, LatticeTestCase cur)
    {
        return Lattice.FilterBy(cur.FilteringRelations, cur.lattice, cur.latOrder,
                faithfulOnly, normalOnly, cur.names, cur.colors);
    }
}
