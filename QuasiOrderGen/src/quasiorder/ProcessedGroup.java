package quasiorder;

import java.util.BitSet;

public class ProcessedGroup
{
    public final RawGroup InputGroup;
    public final BitSet[] ElementMasks;
    public final String[] ElementNames;
    public final BitSet[] ConjugacyClassMasks;
    public final String[] ConjugacyClassNames;
    public final String[] SubgroupNames;

    private ProcessedGroup(
            RawGroup rawGroup,
            BitSet[] elementMasks, String[] elementNames,
            BitSet[] conjugacyClassMasks, String[] conjugacyClassNames, String[] subgroupNames)
    {
        InputGroup = rawGroup;
        ElementMasks = elementMasks;
        ElementNames = elementNames;
        ConjugacyClassMasks = conjugacyClassMasks;
        ConjugacyClassNames = conjugacyClassNames;
        SubgroupNames = subgroupNames;
    }

    // TODO test
    public static ProcessedGroup FromRawGroup(RawGroup rawGroup)
    {
        // TODO
        return null;
    }
}
