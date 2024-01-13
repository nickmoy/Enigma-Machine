package enigma;

import static enigma.EnigmaException.*;

/** An Alphabet consisting of the Unicode characters in a certain range in
 *  order.
 *  @author P. N. Hilfinger
 */
class CharacterRange extends Alphabet {

    /** An alphabet consisting of all characters between FIRST and LAST,
     *  inclusive. */
    CharacterRange(char first, char last) {
        String ans = "";
        for (int i = 0; i < last - first + 1; i++) {
            ans = ans + (char) (first + i);
        }
        _alph = ans;
    }

    /** An alphabet consisting of all characters in s.
     * @param s is the string which represents the alphabet
     * */
    CharacterRange(String s) {
        if (s.isEmpty()) {
            throw error("Alphabet cannot be empty");
        }
        if (s.length() != s.replaceAll("[a-z()\\-*\\s]",
                "").length()) {
            throw error("Bad alphabet characters");
        }
        _alph = s;
    }

    @Override
    int size() {
        return _alph.length();
    }

    @Override
    boolean contains(char ch) {
        return _alph.indexOf(ch) != -1;
    }

    @Override
    char toChar(int index) {
        if (index < 0 || index >= _alph.length()) {
            throw error("Index out of range");
        }
        return _alph.charAt(index);
    }

    @Override
    int toInt(char ch) {
        if (_alph.indexOf(ch) == -1) {
            throw error("Character not in alphabet");
        }
        return _alph.indexOf(ch);
    }

    /** String containing the characters of this Alphabet. */
    private String _alph;

}
