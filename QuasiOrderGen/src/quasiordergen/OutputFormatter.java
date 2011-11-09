package quasiordergen;

import java.util.BitSet;
import java.util.List;

public class OutputFormatter
{
    public static void PrintRelation(BitSet relation, List<BitSet> familyMasks, InputContainer inputGroup)
    {
        int NE = inputGroup.NumElements;

        for(BitSet mask : familyMasks)
            PrintSubgroupFamily(inputGroup, mask);

        for(int i=0;i<NE;i++)
        {
            System.out.print(inputGroup.ElementNames[i] + "  \t:");

            for (int j=0;j<NE;j++)
                System.out.print(relation.get(QuasiOrderGen.ToIndex(i,j,NE)) ? "x " : "  ");

            System.out.println();
        }
    }

    public static void PrintSubgroupFamily(InputContainer inputGroup, BitSet familyMask)
    {
        for (int s=0;s<inputGroup.NumSubgroups;s++)
            if (familyMask.get(s)) // if s is part of the family, print it.
                System.out.print("{"+inputGroup.SubgroupNames[s]+"} ");

        System.out.println();
    }
}
