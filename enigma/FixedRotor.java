package enigma;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Nicholas Moy
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _setting = 0;
    }

    @Override
    boolean rotates() {
        return false;
    }

    @Override
    boolean reflecting() {
        return false;
    }
}
