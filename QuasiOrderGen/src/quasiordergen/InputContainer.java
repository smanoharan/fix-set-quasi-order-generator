package quasiordergen;

import java.util.BitSet;

class InputContainer
{
    public final int NumElements;
    public final int NumSubgroups;
    public final int NumConjugacyClasses;
    public final BitSet[] ElementMasks;
    //public final BitSet[] SubgroupMasks;
    //public final BitSet[] ConjugateSubgroupMasks;
    public final BitSet[] ConjugacyClasses;

    InputContainer(int numElements, int numSubgroups, int numConjugacyClasses, BitSet[] elementMasks, BitSet[] conjugacyClasses)
    {
        NumElements = numElements;
        NumSubgroups = numSubgroups;
        NumConjugacyClasses = numConjugacyClasses;
        ElementMasks = elementMasks;
        ConjugacyClasses = conjugacyClasses;
        //SubgroupMasks = subgroupMasks;
        //ConjugateSubgroupMasks = conjugateSubgroupMasks;
    }
}
