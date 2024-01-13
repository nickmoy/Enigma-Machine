package enigma;

import org.junit.Before;
import org.junit.Test;
import ucb.junit.textui;

import java.util.ArrayList;

import static enigma.TestUtils.*;
import static org.junit.Assert.*;


/** The suite of all JUnit tests for the enigma package.
 *  @author Nicholas Moy
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class, MovingRotorTest.class);
    }

    private ArrayList<Rotor> rotorSet;

    /** Tests whether the rotor settings match a given string. */
    private boolean checkRotorSettings(Machine m, String s) {
        ArrayList<Rotor> rotors = m.getRotors();
        String sets = "";
        for (Rotor r : rotors) {
            sets = sets + m.getAlphabet().toChar(r.setting());
        }
        sets = sets.substring(1);

        return sets.equals(s);
    }

    @Before
    public void setMachine() {
        rotorSet = new ArrayList<>();
        rotorSet.add(new Reflector("B",
                new Permutation(NAVALA.get("B"), UPPER)));
        rotorSet.add(new MovingRotor("Beta",
                new Permutation(NAVALA.get("Beta"), UPPER), ""));
        rotorSet.add(new MovingRotor("III",
                new Permutation(NAVALA.get("III"), UPPER), "V"));
        rotorSet.add(new MovingRotor("IV",
                new Permutation(NAVALA.get("IV"), UPPER), "J"));
        rotorSet.add(new MovingRotor("I",
                new Permutation(NAVALA.get("I"), UPPER), "Q"));
    }

    /** Tests the Machine.java implementation. */
    @Test
    public void checkMachine() {
        Machine m = new Machine(UPPER, 5, 4, rotorSet);
        String[] s = {"B", "Beta", "III", "IV", "I"};
        m.insertRotors(s);
        m.setRotors("AXLE");
        m.setPlugboard(new Permutation("(YF) (ZH)", UPPER));

        assertEquals("Z", m.convert("Y"));

        m.setRotors("AXLE");
        m.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", UPPER));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                m.convert("FROM his shoulder Hiawatha"));

        m.setRotors("AXLE");
        m.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", UPPER));
        assertEquals("FROMHISSHOULDERHIAWATHA",
                m.convert("QVPQSOKOILPUBKJZPISFXDW"));

        m.setRotors("AXLE");
        assertEquals("", m.convert(""));
    }

    /** Tests the advance method. */
    @Test
    public void checkAdvance() {
        Machine m = new Machine(UPPER, 5, 4, rotorSet);
        String[] s = {"B", "Beta", "III", "IV", "I"};
        m.insertRotors(s);
        m.setRotors("AXLE");
        m.setPlugboard(new Permutation("(YF) (ZH)", UPPER));

        assertTrue(checkRotorSettings(m, "AXLE"));

        m.convert(0);
        assertTrue(checkRotorSettings(m, "AXLF"));

        m.setRotors("AVJQ");
        m.convert(0);
        assertTrue(checkRotorSettings(m, "BWKR"));

        m.setRotors("AVAQ");
        m.convert(0);
        assertTrue(checkRotorSettings(m, "BWBR"));
    }
}


