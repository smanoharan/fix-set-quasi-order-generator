package quasiorder;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Generate all possible (faithful) Quasi-Orders on a given group
 *  (specified by it's elements, subgroups and subgroup conjugacy classes)
 *
 * @author Siva Manoharan [avismanoharan@hotmail.com]
 */
public class Generate
{
    private static final int REL_MAX_SIZE = 500;
    private static int iterCount;

    public static void main(String[] args)
    {
        Group inputGroup = null;
        String title = null;

        try
        {
            // parse command line arguments:
            if (args.length < 1 || args.length > 7)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 1-7.");
                System.err.println("Usage: java quasiorder.Generate title [-s] [-o] [-t] [-f] [-n]");
                System.err.println("\t-s means automatically sort the elements");
                System.err.println("\t-o means include graph files for all quasi-orders (in the file \"<title>.q<N>.lat\"");
                System.err.println("\t-t means do not output lattices for relations larger than " + REL_MAX_SIZE + " fix-orders.");
                System.err.println();
                System.err.println("\tThe title is the name of the group. <prefix>.in will be shortened to <prefix>");
                System.err.println("\tThe input file is assumed to be \"<title>.in\".");
                System.err.println("\tThe raw output will be placed in \"<title>.out\".");
                System.err.println("\tThe lattice of all fix-set quasi-orders will be placed in \"<title>.<type>.lat\" and \"<title>.json\".");
                return;
            }

            boolean sortElements = false;
            boolean outputAllGraphs = false;
            boolean thresholdRelationsBySize = false;
            for (String arg : args)
            {
                if (arg.equals("-s")) sortElements = true;
                else if (arg.equals("-o")) outputAllGraphs = true;
                else if (arg.equals("-t")) thresholdRelationsBySize = true;
                else title = (arg.endsWith(".in")) ? arg.substring(0, arg.length() - 3) : arg;
            }

            if (title == null) throw new RuntimeException("Title not included!");

            // read and validate input:
            inputGroup = Group.FromRawGroup(Group.FromJSON(new FileReader(title + ".in")), sortElements);
            inputGroup.Validate(System.err);

            // process:
            long numSubsets = (1 << inputGroup.NumConjugacyClasses); // 2^M
            FixOrderSet relations = new FixOrderSet();

            iterCount = 0;
            long maxIter = numSubsets / 2;
            if (maxIter==0) ProcessConjugacyFamily(inputGroup, relations, 1); // only 1 conj-class.
            else for (long s=0;s<maxIter;s++) ProcessConjugacyFamily(inputGroup, relations, (maxIter | s));

            PrintAllOutput(inputGroup, relations, title, outputAllGraphs, thresholdRelationsBySize);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:\n\n" + e.getMessage());
        }
    }

    // TODO test - somehow?
    private static void ProcessConjugacyFamily(Group inputGroup, FixOrderSet relations, long ccMask)
    {
        BitSet familyMask = GroupUtil.ToSubgroupFamilyBitSet(inputGroup.NumSubgroups, inputGroup.NumConjugacyClasses,
                ccMask, inputGroup.ConjugacyClasses);

        List<Integer> family = GroupUtil.BitSetToList(familyMask);
        boolean isIntersectionClosed = GroupUtil.isIntersectionClosed(inputGroup.SubgroupIntersections, family, familyMask);
        boolean isUnionClosed = GroupUtil.isUnionClosed(inputGroup.SubgroupUnions, family, familyMask);

        // Note: This is still NOT unique as Union of 3 maybe a subgroup while Union of any two pairs in that 3 are not subgroups!
        if (isIntersectionClosed && isUnionClosed)
        {
            iterCount++;
            BitSet relation = FixOrderSet.BuildRelation(inputGroup, familyMask);
            relations.Add(FixOrder.FromRelation(relation, familyMask, inputGroup), familyMask);
        }
    }

    private static void PrintAllOutput(Group inputGroup, FixOrderSet relations, String title, boolean allGraphs, boolean thresholdRelationsBySize) throws IOException
    {
        // create the output streams:
        PrintWriter rawOutput = new PrintWriter(title + ".out");

        // sort all relations, by cardinality (size).
        relations.SortRelations();

        // print families:
        String colours[] = new String[relations.FixOrders.size()];
        int curIndex = 0;
        for (FixOrder b : relations.FixOrders)
        {
            RelationFormat.PrintSubgroupFamilyList(inputGroup, relations.FixOrderToFamilyMap.get(b), curIndex, rawOutput);
            colours[curIndex] = b.isFaithful ? (b.isNormal ? "chartreuse1" : "yellow") : (b.isNormal ? "cadetblue1" : "gray");
            curIndex++;
        }

        // print quasi-orders:
        curIndex=0;
        for(FixOrder b : relations.FixOrders)
            RelationFormat.PrintRelation(b.Relation, inputGroup.ElementNames, inputGroup.NumElements, curIndex++, rawOutput);

        // output all graphs (if needed)
        if (allGraphs)
        {
            curIndex=0;
            boolean[] include = new boolean[inputGroup.NumElements];
            for (int i=0;i<inputGroup.NumElements;i++) include[i]=true;
            for(FixOrder b : relations.FixOrders)
            {
                PrintWriter graphWriter = new PrintWriter(title + ".g" + curIndex++ + ".lat");
                graphWriter.println(RelationFormat.PrintRelationEdges(b.Relation, inputGroup.ElementNames, colours, inputGroup.NumElements, include));
                graphWriter.close();
            }
        }

        // print the lattice of all fix-set quasi-orders:
        if (!thresholdRelationsBySize || relations.FixOrders.size() < REL_MAX_SIZE)
            PrintLatticeOfAllFixSetQuasiOrders(rawOutput, title, relations, colours);
        else System.err.println("Skipped lattice: size=" + relations.FixOrderToFamilyMap.keySet().size() + " is too big");

        // print summary
        String summaryString = String.format("Found %d unique relations, from %d investigated relations, [ out of 2^%d or 2^%d ]",
                relations.FixOrderToFamilyMap.keySet().size(), iterCount, inputGroup.NumConjugacyClasses, inputGroup.NumSubgroups);

        rawOutput.println("\n\n"+summaryString);
        System.err.println(summaryString);

        rawOutput.flush();
        rawOutput.close();
    }

    private static void PrintLatticeOfAllFixSetQuasiOrders(PrintWriter rawOutput, String title, FixOrderSet relations, String[] colours) throws IOException
    {
        // need to print 4 lattices: { all, faithful, normal, normal-faithful }
        PrintWriter latAllOutput = new PrintWriter(title + ".all.lat");
        PrintWriter latFaithfulOutput = new PrintWriter(title + ".faithful.lat");
        PrintWriter latNormalOutput = new PrintWriter(title + ".normal.lat");
        PrintWriter latFaithfulNormalOutput = new PrintWriter(title + ".faithful-normal.lat");
        //PrintWriter jsonLatOutput = new PrintWriter(title + ".json");

        int numRels = relations.FixOrders.size();
        String[] relNames = new String[numRels];
        boolean[] include = new boolean[numRels];
        for (int i=0;i<numRels;i++)
        {
            relNames[i] = Integer.toString(i);
            include[i] = true;
        }

        rawOutput.println("\n\n" + "Lattice of all fix set quasi orders: ");
        latAllOutput.println(RelationFormat.PrintRelationEdges(
                relations.GenerateOverallQuasiOrder(), relNames, colours, numRels, include));

        toFilter(include, true, false, relations.FixOrders, numRels);
        latFaithfulOutput.println(RelationFormat.PrintRelationEdges(
                relations.GenerateOverallQuasiOrder(), relNames, colours, numRels, include));

        toFilter(include, false, true, relations.FixOrders, numRels);
        latNormalOutput.println(RelationFormat.PrintRelationEdges(
                relations.GenerateOverallQuasiOrder(), relNames, colours, numRels, include));

        toFilter(include, true, true, relations.FixOrders, numRels);
        latFaithfulNormalOutput.println(RelationFormat.PrintRelationEdges(
                relations.GenerateOverallQuasiOrder(), relNames, colours, numRels, include));
        //(new Gson()).toJson(fixSetQOLattice, BitSet.class, jsonLatOutput);

        latAllOutput.close();
        latFaithfulOutput.close();
        latNormalOutput.close();
        latFaithfulNormalOutput.close();
        //jsonLatOutput.close();
    }

    private static void toFilter(boolean[] include, boolean faithfulOnly, boolean normalOnly, ArrayList<FixOrder> relations, int numRels)
    {
        for (int i=0;i<numRels;i++)
        {
            FixOrder f =  relations.get(i);
            include[i] = (!faithfulOnly || f.isFaithful) && (!normalOnly || f.isNormal);
        }
    }
}

