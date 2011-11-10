package quasiorder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
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
       
        BufferedReader inputStream = null;
        InputContainer inputGroup = null;

        // input:
        try
        {
            if (args.length > 1)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 1.");
                System.err.println("Usage: java quasiorder.Generate [inputfile]");
                System.err.println("If inputfile is omitted, input is assumed to be from stdin.");
                return;
            }
            else if (args.length==1)
            {
                // input is from a file
                inputStream = new BufferedReader(new FileReader(args[0]));
            }
            else
            {
                // input is from stdin
                inputStream = new BufferedReader(new InputStreamReader(System.in));
            }

            inputGroup = InputContainer.FromInput(inputStream);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (Exception e)
                {
                    // Nothing we can do here.
                }
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
        for (Map.Entry<BitSet, ArrayList<BitSet>> e : relations.uniqRelations.entrySet())
        {
            OutputFormatter.PrintRelation(e.getKey(), e.getValue(), inputGroup);
        }

        System.out.println("Found " + relations.uniqRelations.keySet().size() + " unique relations (out of maximum possible " + (1 << inputGroup.NumConjugacyClasses) + ");");
    }

}

