package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Nicholas Moy
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    /** Tests various Permutations. */
    @Test
    public void checkMiscellenousTransforms() {
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER);
        checkPerm("Shift by 1 Error", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "BCDEFGHIJKLMNOPQRSTUVWXYZA");

        perm = new Permutation("()", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);

        perm = new Permutation("(ABC) (DEF)(GHI)",
                new CharacterRange('A', 'J'));
        checkPerm("(ABC) (DEF)(GHI) Case Error",
                "ABCDEFGHIJ", "BCAEFDHIGJ");
    }

    /** Tests the size method. */
    @Test
    public void sizeTest() {
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER);
        assertEquals(26, perm.size());

        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)",
                new CharacterRange('A', 'A'));
        assertEquals(1, perm.size());
    }

    /** Tests the permute and invert methods. */
    @Test
    public void permuteAndInvertTest() {
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER);
        assertEquals('B', perm.permute('A'));
        assertEquals('A', perm.permute('Z'));
        assertEquals('N', perm.permute('M'));

        assertEquals('M', perm.invert('N'));
        assertEquals('Z', perm.invert('A'));
        assertEquals('Y', perm.invert('Z'));
    }

    /** Tests the derangement method. */
    @Test
    public void derangementTest() {
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER);
        assertTrue(!perm.derangement());

        perm = new Permutation("()", UPPER);
        assertTrue(perm.derangement());

        perm = new Permutation("", UPPER);
        assertTrue(perm.derangement());
    }


}
