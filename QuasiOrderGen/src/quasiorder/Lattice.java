package quasiorder;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class Lattice
{
    interface IBinaryOp
    {
        int DetermineResult(BitSet lattice, int latOrder, int i, int j);
    }

    private static int[][] DetermineOperationTable(BitSet lattice, int latOrder, IBinaryOp op)
    {
        int[][] opTable =  new int[latOrder][latOrder];
        for(int i=0;i<latOrder;i++)
        {
            opTable[i][i] = i;
            for(int j=i+1;j<latOrder;j++)
            {
                int meet = op.DetermineResult(lattice, latOrder, i, j);
                opTable[i][j] = meet;
                opTable[j][i] = meet;
            }
        }
        return opTable;
    }

    static int[][] DetermineMeets(BitSet lattice, int latOrder)
    {
        return DetermineOperationTable(lattice, latOrder, new IBinaryOp()
        {
            public int DetermineResult(BitSet lattice, int latOrder, int i, int j)
            {
                return DetermineMeet(lattice, latOrder, i, j);
            }
        });
    };

    static int[][] DetermineJoins(BitSet lattice, int latOrder)
    {
        return DetermineOperationTable(lattice, latOrder, new IBinaryOp()
        {
            public int DetermineResult(BitSet lattice, int latOrder, int i, int j)
            {
                return DetermineJoin(lattice, latOrder, i, j);
            }
        });
    }

    // needs: i < j ;
    private static int DetermineJoin(BitSet lattice, int latOrder, int i, int j)
    {
        for (int k=i;k>=0;k--)
            if (lattice.get(ToSerialIndex(i,k,latOrder)) && lattice.get(ToSerialIndex(j,k,latOrder)))
                return k;

        throw new RuntimeException("Lattice does not have unique meet.");

    }
    // needs: i < j ;
    private static int DetermineMeet(BitSet lattice, int latOrder, int i, int j)
    {
        for (int k=j;k<latOrder;k++)
            if (lattice.get(ToSerialIndex(k,i,latOrder)) && lattice.get(ToSerialIndex(k,j,latOrder)))
                return k;

        throw new RuntimeException("Lattice does not have unique meet.");

    }

}
