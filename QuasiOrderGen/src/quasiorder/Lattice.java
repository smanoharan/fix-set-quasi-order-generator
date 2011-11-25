package quasiorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class Lattice
{
    public final BitSet latBit;
    public final int latOrder;
    private final int[][] joinTable;
    private final int[][] meetTable;
    public final String[] names;
    public final String[] nodeAttrs;

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


    public Lattice(BitSet lattice, int latOrder, String[] names, String[] nodeAttrs)
    {
        this.latBit = lattice;
        this.latOrder = latOrder;
        this.names = names;
        this.nodeAttrs = nodeAttrs;
        this.joinTable = DetermineJoins(lattice, latOrder);
        this.meetTable = DetermineMeets(lattice, latOrder);
    }

    /**
     * Check if the lattices of the given JSON files in the directory are modular and/or distributive.
     * @param args the directory in which the json resides
     */
    public static void main(String[] args)
    {
        try
        {
            if (args.length!=1)
            {
                System.err.println("Usage: quasiorder.Lattice directory.");
                System.err.println("\tAll files in the directory are assumed to be lattices in serialised format.");
                System.err.println("\tEach lattice is then checked for modularity and distributivity.");
                return;
            }

            File dir = new File(args[0]);
            if (!dir.isDirectory())
                throw new RuntimeException("Input: " + args[0] + " is not a directory.");

            File[] files = dir.listFiles();
            Arrays.sort(files, new Comparator<File>()
            {
                public int compare(File o1, File o2)
                {
                    String[] f1p = o1.getName().split("-");
                    String[] f2p = o2.getName().split("-");

                    int f1order = Integer.parseInt(f1p[1]);
                    int f1id = Integer.parseInt((f1p[2].split("\\."))[0]);
                    int f2order = Integer.parseInt(f2p[1]);
                    int f2id = Integer.parseInt((f2p[2].split("\\."))[0]);

                    int diff = f1order - f2order;
                    if (diff == 0) diff = f1id - f2id;
                    if (diff == 0) diff = f1p.length - f2p.length;
                    if (diff == 0) diff = f1p[2].length() - f2p[2].length();
                    return diff;

                }
            });

            for (File f : files)
            {
                if (!f.isFile()) continue;
                Lattice l = (Lattice)(new ObjectInputStream(new FileInputStream(f))).readObject();
                System.out.println(String.format("%1$-30s\t:\t%s", f.getName(), l.ModDistCheckMessage()));
            }
        }
        catch (Exception e)
        {
            System.err.println("An error occurred:\n\n" + e.getMessage());
            e.printStackTrace();
        }

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
                    NonModularAElem = a;
                    NonModularBElem = b;
                    NonModularXElem = x;
                    NonModularAXJoinElem = xJa;
                    NonModularABMeetElem = aMb;
                    return false;
                }
            }
        }

        NonModularAElem = -1;
        NonModularBElem = -1;
        NonModularXElem = -1;
        NonModularAXJoinElem = -1;
        NonModularABMeetElem = -1;
        return true;
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
                        NonDistXElem = x;
                        NonDistYElem = y;
                        NonDistZElem = z;
                        NonDistYZMeetElem = yMz;
                        NonDistXYJoinElem = xJy;
                        NonDistXZJoinElem = xJz;
                        return false;
                    }
                }

        NonDistXElem = -1;
        NonDistYElem = -1;
        NonDistZElem = -1;
        NonDistYZMeetElem = -1;
        NonDistXYJoinElem = -1;
        NonDistXZJoinElem = -1;
        return true;
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

    // TODO test
    public static Lattice FilterBy(ArrayList<FixOrder> relations, BitSet fullRelation, int numRels,
            boolean faithfulOnly, boolean normalOnly, String[] relationNames, String[] nodeAttrs)
    {
        ArrayList<Integer> inclElem = new ArrayList<Integer>();
        for (int i=0;i<numRels;i++)
        {
            FixOrder f = relations.get(i);
            if ((!faithfulOnly || f.isFaithful) && (!normalOnly || f.isNormal))
                inclElem.add(i);
        }

        int numInclRels = inclElem.size();
        String[] partNames = new String[numInclRels];
        String[] partNodeAttrs = new String[numInclRels];
        BitSet partRelation = new BitSet(numInclRels*numInclRels);

        // setup part-relations, colors and names.
        for (int i=0;i<numInclRels;i++)
        {
            int oldI = inclElem.get(i);
            partNames[i] = relationNames[oldI];
            partNodeAttrs[i] = nodeAttrs[oldI];
            for (int j=0;j<numInclRels;j++)
            {
                int oldJ = inclElem.get(j);
                if (fullRelation.get(ToSerialIndex(oldI, oldJ, numRels)))
                    partRelation.set(ToSerialIndex(i, j, numInclRels));
            }
        }

        return new Lattice(partRelation, numInclRels, partNames, partNodeAttrs);
    }

    /**
     * Compose the message to let the user know that the status of modularity and distributivity of the lattice.
     * @return The message string.
     */
    public String ModDistCheckMessage()
    {
        boolean isModular = IsModular();
        boolean isDistributive = IsDistributive();

        StringBuilder output = new StringBuilder(String.format("Modular: %1$s\tDistributive: %2$s", isModular, isDistributive));

        if (!isModular)
            output.append(String.format("%1$-50s", String.format("\t\tNot-modular: {%s, %s, %s, %s, %s}",
                names[NonModularXElem], names[NonModularAElem], names[NonModularBElem],
                names[NonModularAXJoinElem], names[NonModularABMeetElem])));

        if (!isDistributive)
            output.append(String.format("%1$-50s", String.format("\t\tNot-distributive: {%s, %s, %s, %s, %s, %s}",
                names[NonDistXElem], names[NonDistYElem], names[NonDistZElem],
                names[NonDistXYJoinElem], names[NonDistXZJoinElem], names[NonDistYZMeetElem])));

        return output.toString();
    }

    public BitSet JoinReducibles()
    {
       return FindReducibles(latOrder, joinTable);
    }

    public BitSet MeetReducibles()
    {
        return FindReducibles(latOrder, meetTable);
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

    /**
     * Determine the join of the elements i and j (i V j)
     *
     * Assumes i < j
     *
     * @param lattice The lattice in which i and j reside
     * @param latOrder The number of elements in the lattice
     * @param i The index of the first element
     * @param j The index of the second element
     * @return index-of(elem[i] V elem[j])
     */
    private static int DetermineJoin(BitSet lattice, int latOrder, int i, int j)
    {
        for (int k=i;k>=0;k--)
            if (lattice.get(ToSerialIndex(i,k,latOrder)) && lattice.get(ToSerialIndex(j,k,latOrder)))
                return k;

        throw new RuntimeException("Lattice does not have unique join for " + i + " " + j);
    }

    /**
     * Determine the meet of the elements i and j (i ^ j)
     *
     * Assumes i < j
     *
     * @param lattice The lattice in which i and j reside
     * @param latOrder The number of elements in the lattice
     * @param i The index of the first element
     * @param j The index of the second element
     * @return index-of(elem[i] ^ elem[j])
     */
    private static int DetermineMeet(BitSet lattice, int latOrder, int i, int j)
    {
        for (int k=j;k<latOrder;k++)
            if (lattice.get(ToSerialIndex(k,i,latOrder)) && lattice.get(ToSerialIndex(k,j,latOrder)))
                return k;

        throw new RuntimeException("Lattice does not have unique meet for " + i + " " + j);
    }

}
