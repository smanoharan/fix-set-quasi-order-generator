package quasiorder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class RelationFormat
{
    public static void PrintRelationEdgesWithoutSubGraphs(Lattice lat, String filename) throws IOException
    {
        PrintRelationEdges(lat, filename, false);
    }

    public static void PrintRelationEdgesWithSubGraphs(Lattice lat, String filename) throws IOException
    {
        PrintRelationEdges(lat, filename, true);
    }

    private static void PrintRelationEdges(Lattice lattice, String filename, boolean showSubGraphs) throws IOException
    {
        PrintWriter p = new PrintWriter(filename);
        p.println(PrintRelationEdges(lattice, showSubGraphs));
        p.close();
    }

    private static LinkedList<ArrayList<Integer>> emptySubgraphList = new LinkedList<ArrayList<Integer>>();
    private static String PrintRelationEdges(Lattice lattice, boolean showSubGraphs)
    {
        return PrintRelationEdges(lattice.latBit, lattice.names, lattice.nodeAttr,
                (showSubGraphs ? lattice.subGraphs : emptySubgraphList), lattice.latOrder);
    }

    /**
     * Output this relation as a dot file (for GraphViz), where the edges are spelled out in plaintext.
     *
     * @param relation The relation to output
     * @param elementNames The names of each element in the relation
     * @param numElem The number of elements
     * @param nodeAttributes The presentation attributes of each node
     * @param subGraphs The partitions of the groups. The first partition is expected to be a list of all singletons.
     * @return A string representing the relation in DOT form.
     */
    public static String PrintRelationEdges(BitSet relation, String[] elementNames, String[] nodeAttributes,
                                            LinkedList<ArrayList<Integer>> subGraphs, int numElem)
    {
        StringBuilder res = new StringBuilder();
        res.append("strict digraph {\nedge [ arrowhead=\"none\", arrowtail=\"none\"]\n");
        for (int i=0;i<numElem;i++)
                res.append(String.format("%s [%s]\n",elementNames[i], nodeAttributes[i]));

        AppendSubgraphs(subGraphs, elementNames, res);

        for(int i=relation.nextSetBit(0); i>=0; i=relation.nextSetBit(i + 1))
        {
            int y = i % numElem;
            int x = i / numElem;
            res.append(elementNames[y]).append("->").append(elementNames[x]).append("\n");
        }

        res.append("}\n");
        return res.toString();
    }

    /**
     * Append the SubGraph description to the string builder
     * @param parts The partitions of the graph
     * @param names The name of each element
     * @param res The string builder to append to.
     */
    public static void AppendSubgraphs(LinkedList<ArrayList<Integer>> parts, String[] names, StringBuilder res)
    {
        // partitions become subGraphs:
        int partId = -1;
        for(ArrayList<Integer> part : parts)
        {
            if (partId++ == -1) continue; // skip the singletons

            res.append("subgraph cluster_").append(partId).append(" {");
            for (Integer i : part) res.append(" ").append(names[i]);
            res.append("; style=filled; color=lightgrey }\n");
        }
    }

    public static void PrintRelation(BitSet relation, String[] elementNames, int NE, int index, PrintWriter wOut)
    {
        wOut.println("\n"+index+">>>"+relation.cardinality());
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
