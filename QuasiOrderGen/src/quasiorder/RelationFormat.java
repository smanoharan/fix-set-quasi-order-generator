package quasiorder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.List;

public class RelationFormat
{
    public static void PrintRelationEdges(Lattice lattice, String filename) throws IOException
    {
        PrintWriter p = new PrintWriter(filename);
        p.println(PrintRelationEdges(lattice));
        p.close();
    }

    public static String PrintRelationEdges(Lattice lattice)
    {
        return PrintRelationEdges(lattice.latBit, lattice.names, lattice.nodeAttrs, lattice.latOrder);
    }

    /**
     * Output this relation as a dot file (for GraphViz), where the edges are spelled out in plaintext.
     *
     * @param relation The relation to output
     * @param elementNames The names of each element in the relation
     * @param numElem The number of elements
     * @return A string representing the relation in DOT form.
     */
    public static String PrintRelationEdges(BitSet relation, String[] elementNames, String[] nodeAttributes, int numElem)
    {
        StringBuilder res = new StringBuilder();
        res.append("strict digraph {\nedge [ arrowhead=\"none\", arrowtail=\"none\"]\n");
        for (int i=0;i<numElem;i++)
                res.append(String.format("%s [%s]\n",elementNames[i], nodeAttributes[i]));

        for(int i=relation.nextSetBit(0); i>=0; i=relation.nextSetBit(i + 1))
        {
            int y = i % numElem;
            int x = i / numElem;
            res.append(elementNames[y] + "->" + elementNames[x] + "\n");
        }

        res.append("}\n");
        return res.toString();
    }

    public static void PrintRelation(BitSet relation, String[] elementNames, int NE, int index, PrintWriter wOut)
    {
        wOut.println("\n"+index+">>>");
        for(int i=0;i<NE;i++)
        {
            wOut.print(String.format("%1$-20s \t:", elementNames[i]));

            for (int j=0;j<NE;j++)
                wOut.print(relation.get(FixOrderSet.ToSerialIndex(i, j, NE)) ? "x " : "  ");

            wOut.println();
        }
        wOut.println();
    }

    public static void PrintSubgroupFamily(Group inputGroup, BitSet familyMask, PrintWriter wOut)
    {
        for (int s=0;s<inputGroup.NumSubgroups;s++)
            if (familyMask.get(s)) // if s is part of the family, print it.
                wOut.print("{"+inputGroup.SubgroupNames[s]+"} ");
        wOut.println();
    }

    public static void PrintSubgroupFamilyList(Group inputGroup, List<BitSet> familyMasks, int index, PrintWriter wOut)
    {
        wOut.println("\n"+index+">>>");
        for(BitSet family : familyMasks) PrintSubgroupFamily(inputGroup, family, wOut);
        wOut.println();
    }
}
