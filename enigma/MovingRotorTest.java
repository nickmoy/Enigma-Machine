package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import java.util.HashMap;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Nicholas Moy
 */
public class MovingRotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Rotor rotor;
    private String alpha = UPPER_STRING;

    /** Check that rotor has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkRotor(String testId,
                            String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, rotor.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d (%c)", ci, c),
                         ei, rotor.convertForward(ci));
            assertEquals(msg(testId, "wrong inverse of %d (%c)", ei, e),
                         ci, rotor.convertBackward(ei));
        }
    }

    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS, with given NOTCHES. */
    private void setRotor(String name, HashMap<String, String> rotors,
                          String notches) {
        rotor = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                                notches);
    }

    /* ***** TESTS ***** */

    @Test
    public void checkRotorAtA() {
        setRotor("I", NAVALA, "");
        checkRotor("Rotor I (A)", UPPER_STRING, NAVALA_MAP.get("I"));
    }

    @Test
    public void checkRotorAdvance() {
        setRotor("I", NAVALA, "");
        rotor.advance();
        checkRotor("Rotor I advanced", UPPER_STRING, NAVALB_MAP.get("I"));
    }

    @Test
    public void checkRotorSet() {
        setRotor("I", NAVALA, "");
        rotor.set(25);
        checkRotor("Rotor I set", UPPER_STRING, NAVALZ_MAP.get("I"));
    }

    /** Tests convertFoward and convertBackward methods. */
    @Test
    public void convertFowardandBackwardTest() {
        setRotor("I", NAVALA, "");
        rotor.set(15);
        assertEquals(18, rotor.convertForward(0));
        assertEquals(4, rotor.convertBackward(0));

        rotor.set(25);
        assertEquals(3, rotor.convertForward(25));
        assertEquals(15, rotor.convertBackward(25));
        assertEquals(10, rotor.convertForward(0));
        assertEquals(10, rotor.convertBackward(0));

        rotor.set(0);
        assertEquals(9, rotor.convertForward(25));
        assertEquals(9, rotor.convertBackward(25));
        assertEquals(4, rotor.convertForward(0));
        assertEquals(20, rotor.convertBackward(0));
    }

    @Test
    public void advanceTest() {
        setRotor("I", NAVALA, "");
        assertEquals(0, rotor.setting());

        rotor.advance();
        assertEquals(1, rotor.setting());

        rotor.advance();
        rotor.advance();
        assertEquals(3, rotor.setting());

        rotor.set(25);
        rotor.advance();
        assertEquals(0, rotor.setting());
    }

    /** Tests atNotch method. */
    @Test
    public void atNotchTest() {
        setRotor("I", NAVALA, "ABC");
        rotor.set(0);
        assertTrue(rotor.atNotch());
        rotor.set(1);
        assertTrue(rotor.atNotch());
        rotor.set(2);
        assertTrue(rotor.atNotch());
        rotor.set(3);
        assertTrue(!rotor.atNotch());
    }

}
