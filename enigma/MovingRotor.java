package enigma;

import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Nicholas Moy
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _setting = 0;

        _notches = new HashSet<>();
        for (int i = 0; i < notches.length(); i++) {
            char c = notches.charAt(i);
            _notches.add(c);
        }
    }

    @Override
    public boolean rotates() {
        return true;
    }

    @Override
    public boolean atNotch() {
        return (_notches.contains(alphabet().toChar(_setting)));
    }

    @Override
    void advance() {
        _setting = permutation().wrap(_setting + 1);
    }

    /** HashSet of all notches of this rotor. */
    private HashSet<Character> _notches;
}
