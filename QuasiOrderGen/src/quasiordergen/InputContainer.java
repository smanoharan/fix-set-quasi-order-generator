package quasiordergen;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;

class InputContainer
{
    public final int NumElements;
    public final int NumSubgroups;
    public final int NumConjugacyClasses;
    public final BitSet[] ElementMasks;
    public final String[] ElementNames;
    public final String[] SubgroupNames;
    public final BitSet[] ConjugacyClasses;

    private InputContainer(
            int numElements, int numSubgroups, int numConjugacyClasses,
            BitSet[] elementMasks, String[] elementNames, String[] subgroupNames, BitSet[] conjugacyClasses)
    {
        NumElements = numElements;
        NumSubgroups = numSubgroups;
        NumConjugacyClasses = numConjugacyClasses;
        ElementMasks = elementMasks;
        ElementNames = elementNames;
        SubgroupNames = subgroupNames;
        ConjugacyClasses = conjugacyClasses;
    }

    // TODO test
    public static InputContainer FromInput(BufferedReader input) throws IOException, NumberFormatException
    {
        // ignore the first line, which is a textual description of the group:
        input.readLine();

        // lines 2-4: number of group elements=N, number of subgroups=K, number of conjugacy-classes=M
        int numElem             = Integer.parseInt(input.readLine());
        int numSubgroups        = Integer.parseInt(input.readLine());
        int numConjugacyClasses = Integer.parseInt(input.readLine());

        // next N lines: name of each element (a string)
        // map each group element name to an index
        HashMap<String, Integer> elementIndexMap = new HashMap<String, Integer>();
        String[] elementNames = new String[numElem];
        for (int i=0;i<numElem;i++)
        {
            String elementName = input.readLine().trim();
            elementNames[i] = String.format("%1$-20s", elementName);
            elementIndexMap.put(elementName, i);
        }

        // Next K+M lines: Represents the conjugacy classes.
        //  Each class starts with Ji which is the number of subgroups in each class.
        //      For each class, Ji lines follow, each describing a subgroup, as a space separated list.

        // create bitsets to store memberships:
        BitSet[] conjugacyClasses = new BitSet[numConjugacyClasses];
        BitSet[] elementMasks = new BitSet[numElem];

        for (int i=0;i<numElem;i++)
            elementMasks[i] = new BitSet(numSubgroups);

        int curSubgroupIndex = 0;
        String[] subgroupNames = new String[numSubgroups];
        for (int m=0;m<numConjugacyClasses;m++)
        {
            int J = Integer.parseInt(input.readLine());

            // assign these subgroups to the correct conjugacy class
            conjugacyClasses[m] = new BitSet(numSubgroups);
            conjugacyClasses[m].set(curSubgroupIndex, curSubgroupIndex+J);

            // modify the bitSets as per corresponding subgroups.
            for (int i=0;i<J;i++)
            {
                String subgroupName = input.readLine().trim();
                subgroupNames[curSubgroupIndex] = subgroupName;
                ParseLine(subgroupName, curSubgroupIndex, elementMasks, elementIndexMap);
                curSubgroupIndex++;
            }
        }

        return new InputContainer(numElem, numSubgroups, numConjugacyClasses, elementMasks, elementNames, subgroupNames, conjugacyClasses);
    }

    // TODO Test
    static void ParseLine(String line, int subgroupIndex, BitSet[] elementMasks, HashMap<String, Integer> elementIndexMap)
    {
        // set each element in this line to be a member of the corresponding subgroup.
        for (String elem : line.split(" "))
            elementMasks[elementIndexMap.get(elem)].set(subgroupIndex);
    }
}
