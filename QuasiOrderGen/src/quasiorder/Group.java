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
    public final BitSet[] SubgroupMasks;
    public final int[][] SubgroupIntersections;

    public Group(
            int numElements, int numSubgroups, int numConjugacyClasses,
            BitSet[] elementMasks, String[] elementNames, BitSet[] subgroupMasks, String[] subgroupNames, int[][] subgroupIntersections, BitSet[] conjugacyClasses)
    {
        NumElements = numElements;
        NumSubgroups = numSubgroups;
        NumConjugacyClasses = numConjugacyClasses;
        ElementMasks = elementMasks;
        ElementNames = elementNames;
        SubgroupMasks = subgroupMasks;
        SubgroupNames = subgroupNames;
        SubgroupIntersections = subgroupIntersections;
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
        BitSet[] subgroupMasks = new BitSet[numSubgroups]; 

        for (int i=0;i<numElem;i++)
            elementMasks[i] = new BitSet(numSubgroups);

        for (int i=0;i<numSubgroups;i++)
            subgroupMasks[i] = new BitSet(numElem);

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

                    // set the subgroup membership, and the element membership.
                    int elemIndex = elementIndexMap.get(elem);
                    elementMasks[elemIndex].set(curSubgroupIndex);
                    subgroupMasks[curSubgroupIndex].set(elemIndex);
                }
                subgroupNames[curSubgroupIndex] = subgroupName.toString().trim();
                curSubgroupIndex++;
            }
        }

        // calculate the intersection of each pair of subgroups
        int[][] subgroupIntersections = GenerateIntersections(numSubgroups, subgroupMasks);

        return new Group(numElem, numSubgroups, numConjugacyClasses,
                elementMasks, elementNames, subgroupMasks, subgroupNames,
                subgroupIntersections, conjugacyClasses);
    }

    /**
     * Determine the intersections of each pair of subgroups.
     * @param numSubgroups The number of subgroups in this group.
     * @param subgroupMasks The membership masks of each subgroup (i.e. which elements are in each subgroup).
     * @return An array where if  entry(i,j) = k ; then the intersection of the i'th and j'th subgroup is the k'th (all indices 0-based).
     */
    public static int[][] GenerateIntersections(int numSubgroups, BitSet[] subgroupMasks)
    {
        int[][] res = new int[numSubgroups][numSubgroups];
        for (int i=0;i<numSubgroups;i++)
        {
            res[i][i] = i;
            for (int j=i+1;j<numSubgroups;j++)
            {
                int intersection = GenerateIntersection(i, j, subgroupMasks);
                res[i][j] = intersection;
                res[j][i] = intersection;
            }
        }
        return res;
    }

    /**
     * Calculate the intersection of the two given subgroups.
     * @param s1 The index of the first subgroup
     * @param s2 The index of the second
     * @param subgroups The subgroup membership masks
     * @return the index of the subgroup which is equal to (s1 intersect s2).
     */
    public static int GenerateIntersection(int s1, int s2, BitSet[] subgroups)
    {
        // find all elements that are in both sub1 and sub2.
        BitSet s12 = (BitSet)subgroups[s1].clone();
        s12.and(subgroups[s2]);

        for (int i=0;i<subgroups.length;i++)
            if (s12.equals(subgroups[i]))
                return i;

        throw new RuntimeException("Error: Subgroup not found" + subgroups[s1] + " ^ " + subgroups[s2] + " not a subgroup");
    }
}
