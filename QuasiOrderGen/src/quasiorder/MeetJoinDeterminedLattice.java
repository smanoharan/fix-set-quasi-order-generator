package quasiorder;

import sun.security.rsa.RSASignature;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class MeetJoinDeterminedLattice extends Lattice
{
    private final int[][] joinTable;
    private final int[][] meetTable;

    public int NonModularXElem = -1;
    public int NonModularAElem = -1;
    public int NonModularBElem = -1;
    public int NonModularAXJoinElem = -1;
    public int NonModularABMeetElem = -1;

    public int NonDistXElem = -1;
    public int NonDistYElem = -1;
    public int NonDistZElem = -1;
    public int NonDistYZMeetElem = -1;
    public int NonDistXYJoinElem = -1;
    public int NonDistXZJoinElem = -1;

    public int M5XElem = -1;
    public int M5YElem = -1;
    public int M5ZElem = -1;
    public int M5XYZJoin = -1;
    public int M5XYZMeet = -1;


    public static int NotLatI = -1;
    public static int NotLatJ = -1;
    public static int NotLatK = -1;
    public static int NotLatM = -1;

    public int CMSx = -1;
    public int CMSy = -1;
    public int CMSz = -1;
    public int CMSxMy = -1;
    public int CMSxMz = -1;
    public int CMSyJZ = -1;
    public int CMSxMyJz = -1;

    private MeetJoinDeterminedLattice(
            BitSet lattice, int latOrder, String[] names, String[] colors,
            LinkedList<ArrayList<Integer>> subGraphs, int[][] joinTable, int[][] meetTable)
    {
        super(lattice, latOrder, names, colors, subGraphs);
        this.joinTable = joinTable;
        this.meetTable = meetTable;
        this.nodeAttr = DetermineNodeAttributes(colors, JoinReducibles(), MeetReducibles(), latOrder);
    }

    public static MeetJoinDeterminedLattice FromLattice(Lattice lat)
    {
        int[][] joinTable = DetermineJoins(lat.latBit, lat.latOrder);
        int[][] meetTable = DetermineMeets(lat.latBit, lat.latOrder);
        return new MeetJoinDeterminedLattice(lat.latBit, lat.latOrder, lat.names, lat.colours, lat.subGraphs, joinTable, meetTable);
    }

    // TODO idea
    //  For each pair of elements, not related:
    //      Determine meet:
    //          find all elements less than both elements.
    //          find the largest cardinality:
    //              if multiple, then fail; (same card, diff elem means not comparable)
    //              else (then single), check against all others
    //
    //  Refined:
    //      IsUniqueMeet(A,B):
    //          start at MIN-INDEX(A,B) { if A<=B or B<=A return true; }
    //          iterate <C> downwards till C <= A and C <= B
    //          iterate <D> downwards till end
    //              if D <= A and D <= B then if D is not <= C, return false;
    //          return true;

    /**
     * Determine the node attributes based on the join and meet irreducibility
     * @param colours The node colours
     * @param joinReducible The BitSet showing join reducibility
     * @param meetReducible The BitSet showing meet reducibility
     * @param latOrder The number of elements
     * @return A string array showing the node attribute string for each element.
     */
    public static String[] DetermineNodeAttributes(String[] colours, BitSet joinReducible, BitSet meetReducible, int latOrder)
    {
        String[] nodeAttrs = new String[latOrder];
        for(int i=0;i<latOrder;i++)
        {
            boolean isJIr = !joinReducible.get(i);
            boolean isMIr = !meetReducible.get(i);

            String style =  (isJIr ? (isMIr ? ",solid" : ",dashed") : (isMIr ? ",dotted" : ""));
            int rings = (isJIr || isMIr) ? 2 : 1;
            nodeAttrs[i] = String.format("fillcolor=\"%s\"; peripheries=%d; style=\"filled%s\"", colours[i], rings, style);
        }
        return nodeAttrs;
    }
     /**
     * Check if the lattice is modular. If not, store which elements 0,x,a,b,1 caused the problem.
     *
     * @return whether the lattice is modular.
     */
    public boolean IsModular()
    {
        // modular law: [ x<= b ] ==> [ x V ( a ^ b) == ( x V a ) ^ b ]
        for (int k=latBit.nextSetBit(0); k>=0; k=latBit.nextSetBit(k+1))
        {
            int x = k / latOrder;
            int b = k % latOrder;

            for (int a=0;a<latOrder;a++)
            {
                int xJa = joinTable[x][a];
                int aMb = meetTable[a][b];
                if (joinTable[x][aMb] != meetTable[xJa][b])
                {
                    SaveModularStatus(a, b, x, xJa, aMb);
                    return false;
                }
            }
        }

        SaveModularStatus(-1, -1, -1, -1, -1);
        return true;
    }

    private void SaveModularStatus(int a, int b, int x, int ax, int ab)
    {
        NonModularAElem = a;
        NonModularBElem = b;
        NonModularXElem = x;
        NonModularAXJoinElem = ax;
        NonModularABMeetElem = ab;
    }

    /**
     * Check if the lattice is distributive. If not, store the x,y,z,y^z,xVy,xVz which caused the problem.
     *
     * @return whether the lattice is distributive.
     */
    public boolean IsDistributive()
    {
        // distributive law: [ x V ( y ^ z) == ( x V y ) ^ (x V z) ]
        for (int x=0;x<latOrder;x++)
            for(int y=0;y<latOrder;y++)
                for(int z=0;z<latOrder;z++)
                {
                    int yMz = meetTable[y][z];
                    int xJy = joinTable[x][y];
                    int xJz = joinTable[x][z];
                    if (joinTable[x][yMz] != meetTable[xJy][xJz])
                    {
                        SaveDistStatus(x, y, z, yMz,  xJy, xJz);
                        return false;
                    }
                }

        SaveDistStatus(-1, -1, -1, -1, -1, -1);
        return true;
    }

    // TODO test
    public boolean hasM5AsASublattice()
    {
        for (int x=0;x<latOrder;x++)
            for(int y=0;y<latOrder;y++)
                for (int z=0;z<latOrder;z++)
                    if (x != y && y != z && z != x && hasSameOp(x, y, z, joinTable) && hasSameOp(x, y, z, meetTable))
                            return hasM5(x, y, z, joinTable[x][y], meetTable[x][y]);
        return false;
    }

    private boolean hasM5(int x, int y, int z, int J, int M)
    {
        M5XElem = x;
        M5YElem = y;
        M5ZElem = z;
        M5XYZJoin = J;
        M5XYZMeet = M;
        return true;
    }

        private boolean doesNotHaveM5()
    {
        return !hasM5(-1, -1, -1, -1, -1);
    }

    public String isCongMeetSemiDistMsg()
    {
        return "isCongruentMeetSemiDistributive: " + (!isCongMeetSemiDistributive() ?
            String.format("false { %s, %s, %s, (%s, %s, %s) }",
                    names[CMSx], names[CMSy], names[CMSz], names[CMSxMy], names[CMSxMz], names[CMSxMyJz])
                : "true");
    }

    public boolean isCongMeetSemiDistributive()
    {
        for (int x=0;x<latOrder;x++)
            for(int y=0;y<latOrder;y++)
                for (int z=0;z<latOrder;z++)
                {
                    int xMy = meetTable[x][y];
                    int xMz = meetTable[x][z];
                    int yJz = joinTable[y][z];
                    int xMyJz = meetTable[x][yJz];
                    if (xMy == xMz && xMyJz != xMy)
                        return isNotCongMeetSemiDistributive(x, y, z, xMy, xMz, yJz);
                }

        return isSaveCongMeetSemiDistributive();
    }

    private boolean isNotCongMeetSemiDistributive(int x, int y, int z, int xMy, int xMz, int xMyJz)
    {
        CMSx = x;
        CMSy = y;
        CMSz = z;
        CMSxMy = xMy;
        CMSxMz = xMz;
        CMSxMyJz = xMyJz;
        return false;
    }

    private boolean isSaveCongMeetSemiDistributive()
    {
        return !isNotCongMeetSemiDistributive(-1, -1, -1, -1, -1, -1);
    }


    private static boolean hasSameOp(int x, int y, int z, int[][] opTable)
    {
        int xJy = opTable[x][y];
        int yJz = opTable[y][z];
        int xJz = opTable[x][z];
        return (xJy == yJz && yJz == xJz);
    }

    private void SaveDistStatus(int x, int y, int z, int yz, int xy, int xz)
    {
        NonDistXElem = x;
        NonDistYElem = y;
        NonDistZElem = z;
        NonDistYZMeetElem = yz;
        NonDistXYJoinElem = xy;
        NonDistXZJoinElem = xz;
    }

    private static BitSet FindReducibles(int latOrder, int[][] opTable)
    {
        BitSet reducibles = new BitSet(latOrder);

        // iterate through the op-tables, looking for reducibles:
        for(int i=0;i<latOrder;i++)
            for(int j=i+1;j<latOrder;j++)
            {
                int k = opTable[i][j];
                if (k != i && k != j)
                    reducibles.set(k);
            }

        return reducibles;
    }

    public String ModDistCheckMessage(FlagMessagePair isMod, FlagMessagePair isDist)
    {
        return String.format("Modular: %1$s\tDistributive: %2$s%3$s%4$s", isMod.flag, isDist.flag, isMod.message, isDist.message);
    }


    public String ModDistCheckMessage()
    {
        return ModDistCheckMessage(ModularCheckMessage(), DistributiveCheckMessage());
    }

    /**
     * Compose the message to let the user know that the status of modularity of the lattice.
     * @return The message string.
     */
    public FlagMessagePair ModularCheckMessage()
    {
        boolean isModular = IsModular();
        return new FlagMessagePair(isModular, isModular ? "" : String.format("%1$-50s", String.format("\t\tNot-modular: {%s, %s, %s, %s, %s}",
                names[NonModularXElem], names[NonModularAElem], names[NonModularBElem],
                names[NonModularAXJoinElem], names[NonModularABMeetElem])));
    }

    /**
     * Compose the message to let the user know that the status of distributivity of the lattice.
     * @return The message string.
     */
    public FlagMessagePair DistributiveCheckMessage()
    {
        boolean isDistributive = IsDistributive();
        return new FlagMessagePair(isDistributive, isDistributive ? "" : String.format("%1$-50s", String.format("\t\tNot-distributive: {%s, %s, %s, %s, %s, %s}",
                names[NonDistXElem], names[NonDistYElem], names[NonDistZElem],
                names[NonDistXYJoinElem], names[NonDistXZJoinElem], names[NonDistYZMeetElem])));
    }

    /** @return A BitSet containing all of the join reducible elements */
    public BitSet JoinReducibles()
    {
       return FindReducibles(latOrder, joinTable);
    }

    /** @return A BitSet containing all of the meet reducible elements */
    public BitSet MeetReducibles()
    {
        return FindReducibles(latOrder, meetTable);
    }

    public String M5Message()
    {
        return "Contains M5: " + (hasM5AsASublattice() ?
            String.format("true { (%s), %s, %s, %s, (%s) }",
                    names[M5XYZJoin], names[M5XElem], names[M5YElem], names[M5ZElem], names[M5XYZMeet])
                : "false");
    }


    interface IBinaryOp
    {
        int DetermineResult(BitSet lattice, int latOrder, int i, int j);
    }

    /**
     * Determine the result table of applying a specified Binary operation
     * @param lattice The lattice to apply the binary operation on
     * @param latOrder The number of elements in this lattice
     * @param op The operation, which must be a complete binary operation, LxL->L
     * @return The resulting table for this operation.
     */
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

    /**
     * Determine the meet table
     *
     * @param lattice The lattice to act on
     * @param latOrder The number of elements in this lattice
     * @return The meet table
     */
    public static int[][] DetermineMeets(BitSet lattice, int latOrder)
    {
        return DetermineOperationTable(lattice, latOrder, new IBinaryOp()
        {
            public int DetermineResult(BitSet lattice, int latOrder, int i, int j)
            {
                return DetermineMeet(lattice, latOrder, i, j);
            }
        });
    };

    /**
     * Determine the join table
     *
     * @param lattice The lattice to act on
     * @param latOrder The number of elements in the lattice
     * @return The join table
     */
    public static int[][] DetermineJoins(BitSet lattice, int latOrder)
    {
        return DetermineOperationTable(lattice, latOrder, new IBinaryOp()
        {
            public int DetermineResult(BitSet lattice, int latOrder, int i, int j)
            {
                return DetermineJoin(lattice, latOrder, i, j);
            }
        });
    }

    private static boolean isRel(BitSet b, int latOrder, int i, int j)
    {
        return b.get(ToSerialIndex(i,j,latOrder));
    }

    /**
     * Determine the join of the elements i and j (i V j)
     *
     * Assumes i < j
     *
     * @param lat The lattice in which i and j reside
     * @param latOrder The number of elements in the lattice
     * @param i The index of the first element
     * @param j The index of the second element
     * @return index-of(elem[i] V elem[j])
     */
    private static int DetermineJoin(BitSet lat, int latOrder, int i, int j)
    {
        for (int k=i;k>=0;k--)
            if (isRel(lat, latOrder, i, k) && isRel(lat, latOrder, j, k))
                return k;

        throw new RuntimeException("Lattice does not have unique join for " + i + " " + j);
    }

    /**
     * Determine the meet of the elements i and j (i ^ j)
     *
     * Assumes i < j
     *
     * @param lat The lattice in which i and j reside
     * @param latOrder The number of elements in the lattice
     * @param i The index of the first element
     * @param j The index of the second element
     * @return index-of(elem[i] ^ elem[j])
     */
    private static int DetermineMeet(BitSet lat, int latOrder, int i, int j)
    {
        for (int k=j;k<latOrder;k++)
            if (isRel(lat, latOrder, k, i) && isRel(lat, latOrder, k, j))
                return k;

        throw new RuntimeException("Lattice does not have unique meet for " + i + " " + j);
    }

    private static boolean IsMeetUnique(BitSet poset, int latOrder, int i, int j)
    {
        int k = DetermineMeet(poset, latOrder, i, j);
        for(int m=k;m<latOrder;m++)
            if (isRel(poset, latOrder, m, i) && isRel(poset, latOrder, m, j) && !isRel(poset, latOrder, m, k))
                return SaveIsNotALattice(i, j, k, m);

        return true;
    }

    private static boolean IsJoinUnique(BitSet poset, int latOrder, int i, int j)
    {
        int k = DetermineJoin(poset, latOrder, i, j);
        for(int m=k;m>=0;m--)
            if (isRel(poset, latOrder, i, m) && isRel(poset, latOrder, j, m) && !isRel(poset, latOrder, k, m))
                return SaveIsNotALattice(i, j, k, m);

        return true;
    }

    interface IBinFilter
    {
        boolean IsOpUnique(BitSet poset, int latOrder, int i, int j);
    }

    private static boolean IsOpUnique(BitSet poset, int latOrder, IBinFilter binFilter)
    {
        for(int i=0;i<latOrder;i++)
            for(int j=i+1;j<latOrder;j++)
               if (!isRel(poset, latOrder, i, j) && !binFilter.IsOpUnique(poset, latOrder, i, j))
                   return false;

        return SaveIsALattice();
    }

    private static boolean SaveIsALattice() { return !SaveIsNotALattice(-1, -1, -1, -1); }

    private static boolean SaveIsNotALattice(int i, int j, int k, int m)
    {
        NotLatI = i;
        NotLatJ = j;
        NotLatK = k;
        NotLatM = m;
        return false;
    }

    // TODO test
    public static boolean IsMeetUnique(BitSet poset, int latOrder)
    {
        return IsOpUnique(poset, latOrder, new IBinFilter()
        {
            public boolean IsOpUnique(BitSet poset, int latOrder, int i, int j)
            {
                return IsMeetUnique(poset, latOrder, i, j);
            }
        });
    }

    // TODO test
    public static boolean IsJoinUnique(BitSet poset, int latOrder)
    {
        return IsOpUnique(poset, latOrder, new IBinFilter() {
            public boolean IsOpUnique(BitSet poset, int latOrder, int i, int j) {
                return IsJoinUnique(poset, latOrder, i, j);
            }
        });
    }

    // TODO test
    public static boolean IsALattice(BitSet poset, int latOrder)
    {
        return IsMeetUnique(poset, latOrder) && IsJoinUnique(poset, latOrder);
    }

    public static FlagMessagePair LatCheckMessage(BitSet poset, int latOrder, String[] names)
    {
        boolean isLattice = IsALattice(poset, latOrder);
        StringBuilder output = new StringBuilder("Lattice: " + isLattice);

        if (!isLattice)
            output.append(String.format("\t\t {%s, %s, %s, %s}", names[NotLatI], names[NotLatJ], names[NotLatK], names[NotLatM]));

        return new FlagMessagePair(isLattice, output.toString());
    }

    public static FlagMessagePair LatCheckMessage(Lattice lat)
    {
        return LatCheckMessage(lat.latBit, lat.latOrder, lat.names);
    }
}
