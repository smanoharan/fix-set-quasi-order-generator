package quasiorder;

import com.google.gson.Gson;

import java.io.*;
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
    public static void main(String[] args)
    {
        Group inputGroup = null;
        String title = "group";

        try
        {
            // input:
            if (args.length > 3)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 0-3.");
                System.err.println("Usage: java quasiorder.Generate title [-s] [-o]");
                System.err.println("\t-s means automatically sort the elements");
                System.err.println("\t-o means include graph files for all quasi-orders (in the file \"<title>.q<N>.lat\"");
                System.err.println("\tThe title is the name of the group. <prefix>.in will be shortened to <prefix>");
                System.err.println("\tThe input file is assumed to be \"<title>.in\".");
                System.err.println("\tThe raw output will be placed in \"<title>.out\".");
                System.err.println("\tThe lattice of all fix-set quasi-orders will be placed in \"<title>.full.lat\" and \"<title>.json\".");
                return;
            }

            boolean sortElements = false;
            boolean outputAllGraphs = false;
            for (String arg : args)
            {
                if (arg.equals("-s")) sortElements = true;
                else if (arg.equals("-o")) outputAllGraphs = true;
                else title = (arg.endsWith(".in")) ? arg.substring(0, arg.length() - 3) : arg;
            }

            // read and validate input:
            inputGroup = Group.FromRawGroup(RawGroup.FromJSON(new FileReader(title + ".in")), sortElements);
            inputGroup.Validate(System.err);

            // process:
            long numSubsets = (1 << inputGroup.NumConjugacyClasses); // 2^M
            RelationSet relations = new RelationSet();

            int iterCount = 0;
            long maxIter = numSubsets / 4;
            long minimumClasses = (1 | (maxIter << 1));
            for (long s=0;s<maxIter;s++)
            {
                long ccMask = (minimumClasses | (s << 1));
                BitSet familyMask = GroupUtil.ToSubgroupFamilyBitSet(
                        inputGroup.NumSubgroups, inputGroup.NumConjugacyClasses,
                        ccMask, inputGroup.ConjugacyClasses);

                List<Integer> family = GroupUtil.BitSetToList(familyMask);
                boolean isIntersectionClosed = GroupUtil.isIntersectionClosed(inputGroup.SubgroupIntersections, family, familyMask);
                boolean isUnionClosed = GroupUtil.isUnionClosed(inputGroup.SubgroupUnions, family, familyMask);

                // Note: This is still NOT unique as Union of 3 maybe a subgroup while Union of any two pairs in that 3 are not subgroups!
                if (isIntersectionClosed && isUnionClosed)
                {
                    iterCount++;
                    relations.Add(RelationSet.BuildRelation(inputGroup, familyMask), familyMask);
                }
            }

            PrintAllOutput(inputGroup, relations, title, iterCount, outputAllGraphs);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:\n\n" + e.getMessage());
            e.printStackTrace(); // TODO remove
        }
    }

    private static void PrintAllOutput(Group inputGroup, RelationSet relations, String title, int iterCount, boolean allGraphs) throws IOException
    {
        // create the output streams:
        PrintWriter rawOutput = new PrintWriter(title + ".out");
        PrintWriter latOutput = new PrintWriter(title + ".full.lat");
        PrintWriter jsonLatOutput = new PrintWriter(title + ".json");

        // sort all relations, by cardinality (size).
        relations.SortRelations();

        // print families:
        int curIndex = 0;
        for (FixedBitSet b : relations.Relations)
            OutputFormatter.PrintSubgroupFamilyList(inputGroup, relations.RelationsFamilyMap.get(b.Relation), curIndex++, rawOutput);

        // print quasi-orders:
        curIndex=0;
        for(FixedBitSet b : relations.Relations)
            OutputFormatter.PrintRelation(b.Relation, inputGroup.ElementNames, inputGroup.NumElements, curIndex++, rawOutput);

        // output all graphs (if needed)
        if (allGraphs)
        {
            curIndex=0;
            for(FixedBitSet b : relations.Relations)
            {
                PrintWriter graphWriter = new PrintWriter(title + ".g" + curIndex++ + ".lat");
                graphWriter.println(OutputFormatter.PrintRelationEdges(b.Relation, inputGroup.ElementNames, inputGroup.NumElements));
                graphWriter.close();
            }
        }

        // print the lattice of all fix-set quasi-orders:
        PrintLatticeOfAllFixSetQuasiOrders(inputGroup, rawOutput, latOutput, jsonLatOutput, relations, iterCount);

        rawOutput.flush();
        latOutput.flush();
        jsonLatOutput.flush();

        rawOutput.close();
        latOutput.close();
        jsonLatOutput.close();
    }

    private static void PrintLatticeOfAllFixSetQuasiOrders(
            Group inputGroup, PrintWriter rawOutput, PrintWriter latOutput,
            PrintWriter jsonLatOutput, RelationSet relations, int iterCount)
    {
        int numRels = relations.Relations.size();
        String[] relNames = new String[numRels];
        for (int i=0;i<numRels;i++)
            relNames[i] = Integer.toString(i);

        rawOutput.println("\n\n" + "Lattice of all fix set quasi orders: ");
        BitSet fixSetQOLattice = relations.GenerateOverallQuasiOrder();
        OutputFormatter.PrintRelation(fixSetQOLattice, relNames, numRels, 0, rawOutput);
        latOutput.println(OutputFormatter.PrintRelationEdges(fixSetQOLattice, relNames, numRels));

        String summaryString = String.format("Found %d unique relations, from %d investigated relations, [ out of 2^%d or 2^%d ]",
                relations.RelationsFamilyMap.keySet().size(), iterCount, inputGroup.NumConjugacyClasses, inputGroup.NumSubgroups);

        rawOutput.println("\n\n"+summaryString);
        System.err.println(summaryString);
        (new Gson()).toJson(fixSetQOLattice, BitSet.class, jsonLatOutput);
    }
}

