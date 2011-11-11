package quasiorder;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;

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
       
        Reader inputReader = null;
        Group inputGroup = null;

        // input:
        try
        {
            if (args.length > 2)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 0-2.");
                System.err.println("Usage: java quasiorder.Generate [-s] [inputfile]");
                System.err.println("If inputfile is omitted, input is assumed to be from stdin.");
                System.err.println("-s means automatically sort the elements");
                return;
            }

            boolean sortElements = false;
            for (String arg : args)
            {
                if (arg.equals("-s")) sortElements = true;
                else inputReader = new FileReader(args[0]);
            }

            // if no file is specified, read from std-in.
            if (inputReader==null)
                inputReader = new InputStreamReader(System.in);

            inputGroup = Group.FromRawGroup(RawGroup.FromJSON(inputReader), sortElements);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:\n\n"+e.getMessage());
            e.printStackTrace();
            return;
        }
        finally
        {
            if (inputReader != null)
            {
                try { inputReader.close(); } catch (Exception e) {} // Nothing we can do here.
            }
        }

        // process:
        long numSubsets = (1 << inputGroup.NumConjugacyClasses); // 2^M
        RelationSet relations = new RelationSet();

        for (long s=1;s<numSubsets;s++)
        {
            BitSet familyMask = GroupUtil.ToSubgroupFamilyBitSet(inputGroup.NumSubgroups, inputGroup.NumConjugacyClasses, s, inputGroup.ConjugacyClasses);
            if (!GroupUtil.isIntersectionTrivial(inputGroup.ElementMasks, familyMask)) continue;
            relations.Add(RelationSet.BuildRelation(inputGroup, familyMask), familyMask);
        }

        // output? TODO
        ArrayList<BitSet> finalRelations = new ArrayList<BitSet>(relations.uniqRelations.size());
        int curIndex = 0;
        for (Map.Entry<BitSet, ArrayList<BitSet>> e : relations.uniqRelations.entrySet())
        {
            finalRelations.add(e.getKey());
            OutputFormatter.PrintSubgroupFamilyList(inputGroup, e.getValue(), curIndex++);
        }

        curIndex=0;
        for(BitSet b : finalRelations)
            OutputFormatter.PrintRelation(b, inputGroup, curIndex++);

        System.out.println("Found " + relations.uniqRelations.keySet().size() + " unique relations (out of maximum possible " + (1 << inputGroup.NumConjugacyClasses) + ");");
    }
}

