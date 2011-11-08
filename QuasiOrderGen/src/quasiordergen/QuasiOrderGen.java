package quasiordergen;
import java.util.BitSet;

/**
 *
 * @author Siva Manoharan [avismanoharan@hotmail.com]
 */
public class QuasiOrderGen 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // TODO 
        //  input: 
        //      Group of N elements(ArrayList<String>) ; 
        //      K Subgroups ( List<BitArray> ) ;
        //      [Optionally] Cayley table ;

        //  process:
        //      For each element of G, create a bitArray for subgroup membership.
        //      Generate all subsets, (2^K) represent them as bitArrays
        //      For each subset:
        //          [Optionally] Check if 
        //              Conjugate closed <(4)> ; 
        //              Intersection - trivial <(1)>;               [ IN PROGRESS ]
        //          For each g1 \in G ; 
        //              For each g2 \in G;
        //                  where g1 != g2
        //                  check if g1 \in S => g2 \in S           [ DONE! ]
        //                  create Relation - add to list
        //      [Optionally] Find all unique relations from list

        //  output:        
        //      A list of relations 
        //      [Optionally] Convert it to a displayable form.
        //      
        
        
    }

    /**
     * Determine if g1 <= g2 (i.e. if (g1,g2) \in Relation ) given their subgroup memberships
     *  and the subgroup family under consideration.
     *
     * @param g1Mask A bitmask representing g1's subgroup membership. The i'th bit is 1 iff g1 \in subgroup_i
     * @param g2Mask A bitmask representing g2's subgroup membership. The i'th bit is 1 iff q2 \in subgroup_i
     * @param subgroupFamilyMask A bitmask representing the subgroup family under consideration. The i'th bit is 1 iff subgroup_i \in family
     * @return whether g1 <= g2
     */
    public static boolean isRelated(BitSet g1Mask, BitSet g2Mask, BitSet subgroupFamilyMask)
    {
        // start with b: 1 iff g1 \in sub-i
        BitSet b = (BitSet)g1Mask.clone();

        // 'AND' with subgroup family: 1 iff ( g1 \in sub-i && sub-i \in family )
        b.and(subgroupFamilyMask);

        // 'AND' with NOT of g2Mask: 1 iff ( g1 \in sub-i && sub-i \in family  && g2 \in sub-i)
        b.andNot(g2Mask);

        // if any bits are set, the relation is false (there is a counter example)
        return b.isEmpty();
    }

    /**
     * Determine if the intersection of all the subgroups in this family is trivial (i.e. = {1})
     * Assumes that the elements[0] is the unity element (1).
     * Note: If the intersection is empty (which should never occur as all subgroups must contain 1), then the intersection is not trivial.
     *
     * @param elements The bitmasks representing the subgroup memberships of each element in the group.
     * @param subgroupFamilyMask The bitmask representing the subgroups in this family.
     * @return Whether the intersection of all the subgroups is trivial.
     */
    public static boolean isIntersectionTrivial(BitSet[] elements, BitSet subgroupFamilyMask)
    {
        int numElem = elements.length;
        for (int i=1;i<numElem;i++) // ignore elements[0] - unity.
        {
            BitSet b = (BitSet)subgroupFamilyMask.clone();
            b.and(elements[i]); // if elem was in all subsets, b would not have changed here.
            if (b.equals(subgroupFamilyMask)) return false;
        }
        return true;
    }
}
