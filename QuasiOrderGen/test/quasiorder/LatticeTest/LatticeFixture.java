package quasiorder.LatticeTest;

import org.junit.runners.Parameterized;
import quasiorder.LatticeTest.TestCases.*;
import quasiorder.QuasiOrderGenFixture;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class LatticeFixture extends QuasiOrderGenFixture
{
    LatticeTestCase cur;

    protected static final LatticeTestCase[][] TestCaseLattices = new LatticeTestCase[][]
    {
            new LatticeTestCase[]{ new Dihedral6TestCase() },
            new LatticeTestCase[]{ new Figure8TestCase() },
            new LatticeTestCase[]{ new Dihedral4FaithfulOnlyTestCase() },
            new LatticeTestCase[]{ new N5TestCase() },
            new LatticeTestCase[]{ new N5SuperSetTestCase() },
            new LatticeTestCase[]{ new GridTestCase() },
            new LatticeTestCase[]{ new M3TestCase() },
            new LatticeTestCase[]{ new M3SuperSetTestCase() }
    };

    @Parameterized.Parameters
    public static Collection TestCases() { return Arrays.asList(TestCaseLattices); }

    public LatticeFixture(LatticeTestCase curLat)
    {
        cur = curLat;
        cur.SetupAll();
    }

    protected static void assertTableEquals(String title, int[][] expected, int[][] actual, int latOrder)
    {
        for(int i=0;i<latOrder;i++)
            for(int j=0;j<latOrder;j++)
                assertEquals(String.format("%s[%d,%d]", title, i, j), expected[i][j], actual[i][j]);
    }
}
