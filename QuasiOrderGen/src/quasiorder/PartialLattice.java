package quasiorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class PartialLattice implements Serializable
{
    public BitSet relation;
    public String[] names;
    public String[] colors;
    public int numRels;

    // TODO test
    public static PartialLattice FilterBy(
            ArrayList<FixOrder> relations, BitSet fullRelation, int numRels,
            boolean faithfulOnly, boolean normalOnly, String[] relationNames, String[] colors)
    {
        ArrayList<Integer> inclElem = new ArrayList<Integer>();
        for (int i=0;i<numRels;i++)
        {
            FixOrder f = relations.get(i);
            if ((!faithfulOnly || f.isFaithful) && (!normalOnly || f.isNormal))
                inclElem.add(i);
        }

        int numInclRels = inclElem.size();
        String[] partNames = new String[numInclRels];
        String[] partColors = new String[numInclRels];
        BitSet partRelation = new BitSet(numInclRels*numInclRels);

        // setup part-relations, colors and names.
        for (int i=0;i<numInclRels;i++)
        {
            int oldI = inclElem.get(i);
            partNames[i] = relationNames[oldI];
            partColors[i] = colors[oldI];
            for (int j=0;j<numInclRels;j++)
            {
                int oldJ = inclElem.get(j);
                if (fullRelation.get(ToSerialIndex(oldI, oldJ, numRels)))
                    partRelation.set(ToSerialIndex(i, j, numInclRels));
            }
        }

        PartialLattice p = new PartialLattice();
        p.colors = partColors;
        p.names = partNames;
        p.numRels = numInclRels;
        p.relation = partRelation;
        return p;
    }
}
