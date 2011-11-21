package quasiorder;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
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
    public final int[][] SubgroupUnions;
    public final BitSet IsSubgroupNormal;

    public Group(
            int numElements, int numSubgroups, int numConjugacyClasses,
            BitSet[] elementMasks, String[] elementNames, BitSet[] subgroupMasks, String[] subgroupNames, int[][] subgroupIntersections, int[][] subgroupUnions, BitSet[] conjugacyClasses, BitSet conjugacyClassNormal)
    {
        NumElements = numElements;
        NumSubgroups = numSubgroups;
        NumConjugacyClasses = numConjugacyClasses;
        ElementMasks = elementMasks;
        ElementNames = elementNames;
        SubgroupMasks = subgroupMasks;
        SubgroupNames = subgroupNames;
        SubgroupIntersections = subgroupIntersections;
        SubgroupUnions = subgroupUnions;
        ConjugacyClasses = conjugacyClasses;
        IsSubgroupNormal = conjugacyClassNormal;
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
        BitSet isSubgroupNormal = new BitSet(numSubgroups);
        String[] subgroupNames = new String[numSubgroups];

        for (int m=0;m<numConjugacyClasses;m++)
        {
            String[][] conjClass = rawgroup.ConjugacyClasses[m];
            int conjClassSize = conjClass.length;

            // determine if subgroup is normal (which is iff conj-class is singleton):
            if (conjClassSize==1) isSubgroupNormal.set(curSubgroupIndex);

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
        int[][] subgroupIntersections = GroupUtil.GenerateIntersections(numSubgroups, subgroupMasks);
        int[][] subgroupUnions = GroupUtil.GenerateUnions(numSubgroups, subgroupMasks);

        return new Group(numElem, numSubgroups, numConjugacyClasses,
                elementMasks, elementNames, subgroupMasks, subgroupNames,
                subgroupIntersections, subgroupUnions, conjugacyClasses, isSubgroupNormal);
    }

    public static RawGroup FromJSON(Reader jsonReader) throws IOException
    {
        // parse JSON (which has arrays of arrays of arrays of strings)
        String[][][][] groupProp = ( new Gson()).fromJson(jsonReader, String[][][][].class);

        // try closing the input-stream. If this fails, nothing we can do.
        try { jsonReader.close(); } catch (Exception e) {}

        // places to look:
        String[] elements = groupProp[0][0][0];

        int numElements = elements.length;
        String[][][] conjugacyClasses = groupProp[1];
        int numConjugacyClasses = conjugacyClasses.length;

        // calculate number of subgroups
        int NumSubgroups = 0;
        for(String[][] arr : conjugacyClasses) NumSubgroups += arr.length;

        return new RawGroup(numElements, NumSubgroups, numConjugacyClasses, elements, conjugacyClasses);
    }

    interface IBitSetOperation
    {
        void combine(BitSet b1, BitSet b2);
    }

    public void Validate(PrintStream out) throws Exception
    {
        // check identity: must be an element of each subgroup.
        //out.println("Checking identity: \"" + ElementNames[0] + "\"");
        int nextClearBit = ElementMasks[0].nextClearBit(0);
        if (nextClearBit >= 0 && nextClearBit < NumSubgroups) throw new Exception("Trivial element is not a member of some subgroup(" + nextClearBit + "): " + ElementMasks[0]);

        // check subgroups: 1st must be trivial, last must have all subgroups.
        //out.println("Checking subgroups: Count=" + NumSubgroups);

        BitSet trivialSubgroup = SubgroupMasks[0];
        int nextSetBit = trivialSubgroup.nextSetBit(1);
        if (!trivialSubgroup.get(0) || (nextSetBit >= 0 && nextSetBit < NumElements))
            throw new Exception("1st subgroup is not trivial");

        BitSet fullGroup = SubgroupMasks[SubgroupMasks.length-1];
        nextClearBit = fullGroup.nextClearBit(1);
        if (nextClearBit >= 0 && nextClearBit < NumElements) throw new Exception("Last subgroup is not the whole group.");
    }

    public static class RawGroup
    {
        public final int NumElements;
        public final int NumSubgroups;
        public final int NumConjugacyClasses;
        public final String[] Elements;
        public final String[][][] ConjugacyClasses;

        protected RawGroup(int numElements, int numSubgroups, int numConjugacyClasses, String[] elements, String[][][] conjugacyClasses)
        {
            NumElements = numElements;
            NumSubgroups = numSubgroups;
            NumConjugacyClasses = numConjugacyClasses;
            Elements = elements;
            ConjugacyClasses = conjugacyClasses;
        }
    }
}
