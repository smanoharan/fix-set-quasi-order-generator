package quasiordergen;

/**
 *
 * @author sm264
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
        //      [Optionally] cayley table ;
        //  
        //  process:
        //      For each element of G, create a bitArray for subgroup membership.
        //      Generate all subsets, (2^K) represent them as bitArrays
        //      For each subset:
        //          [Optionally] Check if 
        //              Conjugate closed <(4)> ; 
        //              Intersection - trivial <(1)>;
        //          For each g1 \in G ; 
        //              For each g2 \in G;
        //                  where g1 != g2
        //                  check if g1 \in S => g2 \in S [ O(1) using bitMaps ]
        //                  create Relation - add to list
        //      [Optionally] Find all unique relations from list
        //
        //  output:        
        //      A list of relations 
        //      [Optionally] Convert it to a displayable form.
        //      
        
        
    }
}
