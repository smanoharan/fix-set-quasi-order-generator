package quasiorder;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;

class Group
{
    public final int NumElements;
    public final int NumSubgroups;
    public final int NumConjugacyClasses;
    public final BitSet[] ElementMasks;
    public final String[] ElementNames;
    public final String[] SubgroupNames;
    public final BitSet[] ConjugacyClasses;

    public Group(
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

    /**
     * Create a group by processing a RawGroup.
     * @param rawgroup a group which is parsed json, with no processing.
     * @param sortElem Whether or not to sort the elements.
     * @return A fully processed group.
     */
    public static Group FromRawGroup(RawGroup rawgroup, boolean sortElem)
    {
        // counts of each type:
        int numElem = rawgroup.NumElements;
        int numSubgroups = rawgroup.NumSubgroups;
        int numConjugacyClasses = rawgroup.NumConjugacyClasses;

        // element names:
        HashMap<String, Integer> elementIndexMap = new HashMap<String, Integer>();
        String[] elementNames = rawgroup.Elements;

        if (sortElem)
        {
            // sort the names (first by length, then lexically)
            Arrays.sort(elementNames, new Comparator<String>()
            {
                public int compare(String o1, String o2)
                {
                    int diff = (o1.length() - o2.length());
                    return diff == 0 ? diff = o1.compareTo(o2) : diff;
                }
            });
        }

        for (int i=0;i<numElem;i++)
            elementIndexMap.put(rawgroup.Elements[i], i);

        BitSet[] conjugacyClasses = new BitSet[numConjugacyClasses];
        BitSet[] elementMasks = new BitSet[numElem];

        for (int i=0;i<numElem;i++)
            elementMasks[i] = new BitSet(numSubgroups);

        int curSubgroupIndex = 0;
        String[] subgroupNames = new String[numSubgroups];
        for (int m=0;m<numConjugacyClasses;m++)
        {
            String[][] conjClass = rawgroup.ConjugacyClasses[m];
            int conjClassSize = conjClass.length;

            // assign subgroups correct conjugacy class
            conjugacyClasses[m] = new BitSet(numSubgroups);
            conjugacyClasses[m].set(curSubgroupIndex, curSubgroupIndex+conjClassSize);

            // assign elements to corresponding subgroups & build up subgroup-name.
            for (int i=0;i<conjClassSize;i++)
            {
                StringBuilder subgroupName = new StringBuilder();
                for(String elem : conjClass[i])
                {
                    subgroupName.append(elem);
                    subgroupName.append(' ');
                    elementMasks[elementIndexMap.get(elem)].set(curSubgroupIndex);
                }
                subgroupNames[curSubgroupIndex] = subgroupName.toString().trim();
                curSubgroupIndex++;
            }
        }

        return new Group(numElem, numSubgroups, numConjugacyClasses, elementMasks, elementNames, subgroupNames, conjugacyClasses);
    }
}
