package enigma;

import static enigma.EnigmaException.*;

import java.util.HashMap;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Nicholas Moy
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _foward = new HashMap<>();
        _backward = new HashMap<>();

        cycles = cycles.replaceAll("[\\(\\)]", " ");
        cycles = cycles.trim();
        String[] cycleArr = cycles.split("\\s+");

        for (int i = 0; i < _alphabet.size(); i++) {
            char c = _alphabet.toChar(i);
            _foward.put(c, c);
            _backward.put(c, c);
        }

        for (String s : cycleArr) {
            addCycle(s);
        }

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        if (cycle.length() == 0) {
            return;
        } else if (cycle.length() == 1) {
            char c = cycle.charAt(0);
            _foward.put(c, c);
            _backward.put(c, c);
        } else {
            for (int i = 0; i < cycle.length() - 1; i++) {
                char c1 = cycle.charAt(i);
                char c2 = cycle.charAt(i + 1);
                _foward.put(c1, c2);
                _backward.put(c2, c1);
            }
            _foward.put(cycle.charAt(cycle.length() - 1), cycle.charAt(0));
            _backward.put(cycle.charAt(0), cycle.charAt(cycle.length() - 1));
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char key = _alphabet.toChar(wrap(p));
        return _alphabet.toInt(permute(key));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char key = _alphabet.toChar(wrap(c));
        return _alphabet.toInt(invert(key));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _foward.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _backward.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            char c = _alphabet.toChar(i);
            if (permute(c) != c || (invert(c) != c)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Hashmap of all cycles of this permutation going foward. */
    private HashMap<Character, Character> _foward;

    /** Hashmap of all cycles of this permutation going backward. */
    private HashMap<Character, Character> _backward;
}
