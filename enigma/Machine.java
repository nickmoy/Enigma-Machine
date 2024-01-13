package enigma;

import java.util.Collection;
import java.util.ArrayList;
import static enigma.EnigmaException.*;


/** Class that represents a complete enigma machine.
 *  @author Nicholas Moy
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _rotors = new ArrayList<>();

        _allRotors = new ArrayList<>();
        for (Rotor r : allRotors) {
            _allRotors.add(r);
        }

        _numRotors = numRotors;
        _pawls = pawls;
        _plugboard = new Permutation("", _alphabet);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < rotors.length; i++) {
            String s = rotors[i];
            boolean rotorAdded = false;
            for (Rotor r : _allRotors) {
                if (r.name().toUpperCase().equals(s.toUpperCase())) {
                    if ((i == 0 && !r.reflecting())
                            || (i != 0 && r.reflecting())) {
                        throw error("Reflector in wrong place");
                    }
                    if (r.rotates()) {
                        counter++;
                    }
                    _rotors.add(r);
                    rotorAdded = true;
                    break;
                }
            }
            if (!rotorAdded) {
                throw error("Bad rotor name in settings line");
            }
        }
        if (counter != _pawls) {
            throw error("Wrong number of moving rotors");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _rotors.size() - 1) {
            throw error("Bad rotor settings");
        } else {
            for (int i = 0; i < setting.length(); i++) {
                char c = setting.charAt(i);
                if (!_alphabet.contains(c)) {
                    throw error("Bad rotor settings not in alphabet");
                }
                _rotors.get(i + 1).set(c);
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advance();

        c = _plugboard.permute(c);
        for (int i = _rotors.size() - 1; i >= 0; i--) {
            Rotor r = _rotors.get(i);
            c = r.convertForward(c);
        }
        for (int i = 1; i < _rotors.size(); i++) {
            Rotor r = _rotors.get(i);
            c = r.convertBackward(c);
        }
        c = _plugboard.permute(c);
        return c;
    }

    /** Returns the settings of the machine (for testing). */
    String getSettings() {
        String ans = "";
        for (Rotor r : _rotors) {
            ans = ans + r.setting() + " ";
        }
        return ans;
    }

    /** Advances all the rotors of this machine. */
    private void advance() {
        for (int i = 0; i < _rotors.size(); i++) {
            Rotor r = _rotors.get(i);

            if (i == _rotors.size() - 1) {
                r.advance();
            } else if (i != 0) {
                Rotor rp = _rotors.get(i - 1);
                Rotor rn = _rotors.get(i + 1);
                if ((r.atNotch() && rp.rotates())
                        || (rn.atNotch() && r.rotates())) {
                    r.advance();
                }
            }
        }
    }

    /** Returns the rotors of the machine (for testing). */
    ArrayList<Rotor> getRotors() {
        return _rotors;
    }

    /** Returns the alphabet of the machine (for resting).*/
    Alphabet getAlphabet() {
        return _alphabet;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll(" ", "");
        msg = msg.toUpperCase();
        String ans = "";
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            int index = _alphabet.toInt(c);
            index = convert(index);
            ans = ans + _alphabet.toChar(index);
        }
        return ans;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Collection of all possible rotors this machine could have. */
    private final ArrayList<Rotor> _allRotors;

    /** List of all rotors this machine has in order starting from
     *  reflector.
     */
    private ArrayList<Rotor> _rotors;

    /** The number of rotors this machine has. */
    private final int _numRotors;

    /** The number of pawls this machine has. */
    private final int _pawls;

    /** The pluboard permutation of this machine. */
    private Permutation _plugboard;
}
