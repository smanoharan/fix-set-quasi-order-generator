package quasiordergen;

import java.util.BitSet;

public class QuasiOrderGenFixture
{
    // 5 elements : { A, B, C, D, E }
    protected static final int NumElem = 5;
    protected static final int IA = 0;
    protected static final int IB = 1;
    protected static final int IC = 2;
    protected static final int ID = 3;
    protected static final int IE = 4;

    protected BitSet[] elementMasks;

    protected static BitSet StringToBitSet(String BitString)
    {
        BitSet result = new BitSet(BitString.length());

        for(int i=0;i<BitString.length();i++)
            if (BitString.charAt(i)=='1')
                result.set(i);

        return result;
    }

    protected static BitSet MaskToBitSet(int mask, int length)
    {
        BitSet result = new BitSet(length);

        int bitValue = 1;
        for (int i=0;i<length;i++)
        {
            if (0 != (mask & bitValue)) result.set(i);
            bitValue *= 2;
        }
        
        return result;
    }

        
}
