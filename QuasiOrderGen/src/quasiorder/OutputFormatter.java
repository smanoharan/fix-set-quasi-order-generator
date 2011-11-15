package quasiorder;

import java.util.BitSet;
import java.util.List;

public class OutputFormatter
{

    /**
     * Output this relation as a dot file (for GraphViz), where the edges are spelled out in plaintext.
     * @param relation The relation to output
     * @param elementNames The names of each element in the relation
     * @param numElem The number of elements
     * @return A string representing the relation in DOT form.
     */
    public static String PrintRelationEdges(BitSet relation, String[] elementNames, int numElem)
    {
        StringBuilder res = new StringBuilder();
        res.append("strict digraph {\n");

        for(int i=relation.nextSetBit(0); i>=0; i=relation.nextSetBit(i + 1))
        {
            int y = i % numElem;
            int x = i / numElem;
            res.append(elementNames[y] + "->" + elementNames[x] + "\n");
        }

        res.append("}\n");
        return res.toString();
    }


    // possible ideas: (TODO)
    //  sort each relation by the number of set bits.
    //  then perhaps combine those which produce the same relation?
    public static void PrintRelation(BitSet relation, String[] elementNames, int NE, int index)
    {
        System.out.println("\n"+index+">>>");

        for(int i=0;i<NE;i++)
        {
            System.out.print(String.format("%1$-20s \t:", elementNames[i]));

            for (int j=0;j<NE;j++)
                System.out.print(relation.get(RelationSet.ToSerialIndex(i, j, NE)) ? "x " : "  ");

            System.out.println();
        }
        System.out.println();
    }

    public static void PrintSubgroupFamily(Group inputGroup, BitSet familyMask)
    {
        for (int s=0;s<inputGroup.NumSubgroups;s++)
            if (familyMask.get(s)) // if s is part of the family, print it.
                System.out.print("{"+inputGroup.SubgroupNames[s]+"} ");

        System.out.println();
    }

    public static void PrintSubgroupFamilyList(Group inputGroup, List<BitSet> familyMasks, int index)
    {
        System.out.println();
        System.out.println(index+">>>");
        for(BitSet family : familyMasks) PrintSubgroupFamily(inputGroup, family);
        System.out.println();
    }
}
