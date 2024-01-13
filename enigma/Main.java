package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Nicholas Moy
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        String line = "";
        if (_input.hasNextLine()) {
            line = _input.nextLine();
        }
        if (!line.isEmpty() && line.charAt(0) == '*') {
            setUp(M, line);
        } else {
            throw error("First line must be setting");
        }
        while (_input.hasNextLine()) {
            line = _input.nextLine();
            if (line.isEmpty()) {
                printMessageLine(line);
            } else if (line.charAt(0) == '*') {
                setUp(M, line);
            } else {
                printMessageLine(M.convert(line.toUpperCase()));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String cur = "";
            if (_config.hasNext("[^-()\\s]-[^-()\\s]")) {
                cur = _config.next("[^-()\\s]-[^-()\\s]");
                _alphabet = new CharacterRange(cur.charAt(0),
                        cur.charAt(2));
            } else if (_config.hasNext("[^a-z()\\-*]+")) {
                cur = _config.next("[^a-z()\\-*]+");
                _alphabet = new CharacterRange(cur);
            } else {
                throw error("Bad config: missing or invalid alphabet");
            }
            checkGoToNextLine();

            int numRotors = 0;
            if (_config.hasNextInt()) {
                numRotors = _config.nextInt();
            } else {
                throw error("Bad config: No number of rotors given");
            }
            if (numRotors <= 0) {
                throw error("Bad number of rotors");
            }
            checkGoToNextLine();

            int pawls = 0;
            if (_config.hasNextInt()) {
                pawls = _config.nextInt();
            } else {
                throw error("Bad config: No number of pawls given");
            }
            if (pawls < 0 || pawls >= numRotors) {
                throw error("Bad number of pawls");
            }
            checkGoToNextLine();

            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }

            Machine M = new Machine(_alphabet, numRotors, pawls, allRotors);
            return M;
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Checks if _config should go to nextLine and does so if needed. */
    private void checkGoToNextLine() {
        if (!_config.hasNext() && _config.hasNextLine()) {
            _config.nextLine();
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = "";
            if (_config.hasNext("[^()]+")) {
                name = _config.next("[^()]+");
            } else {
                throw error("Bad rotor name in config file");
            }
            checkGoToNextLine();

            String type = "";
            if (_config.hasNext("(M[^a-z()\\-*\\s]+|N|R)")) {
                type = _config.next("(M[^a-z()\\-*\\s]+|N|R)");
            } else {
                throw error("Bad rotor type in config file");
            }
            checkGoToNextLine();

            String cycles = "";
            while (_config.hasNext("([(][^()]+\\)\\s*)+")) {
                cycles = cycles + _config.next("([(][^()]+\\)\\s*)+");
                checkGoToNextLine();
            }
            Permutation perm = new Permutation(cycles.trim(), _alphabet);

            Rotor rotor = null;
            char c = type.charAt(0);
            if (c == 'M') {
                if (type.length() < 2) {
                    throw error("Moving rotor has no notches");
                }
                for (int i = 1; i < type.length(); i++) {
                    char ch = type.charAt(i);
                    if (!_alphabet.contains(ch)) {
                        throw error("Bad notch: character not in alphabet");
                    }
                }
                rotor = new MovingRotor(name, perm, type.substring(1));
            } else if (c == 'N') {
                rotor = new FixedRotor(name, perm);
            } else if (c == 'R') {
                rotor = new Reflector(name, perm);
            } else {
                throw error("Bad rotor description in config file");
            }
            return rotor;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        settings = settings.trim();
        if (!settings.matches("\\*\\s(([^(]+\\s*)+([(][^)]+\\)\\s*)*)")) {
            error("Bad settings line");
        } else {
            settings = settings.substring(1).trim();
            String[] settingsArray = settings.split("\\s+");

            String[] rotors = new String[M.numRotors()];
            String rotorSettings = "";
            String cycles = "";

            int i = 0;
            for (; i < M.numRotors(); i++) {
                if (i >= settingsArray.length) {
                    throw error("Bad settings line: not enough arguments");
                }
                if (settingsArray[i].matches("([(][^()]+\\)\\s*)")) {
                    throw error("Either too few rotors or no rotor settings");
                }
                rotors[i] = settingsArray[i];
            }
            hasDuplicates(rotors);
            M.insertRotors(rotors);

            if (i >= settingsArray.length
                    || settingsArray[i].matches("([(][^()]+\\)\\s*)")) {
                throw error("No rotor settings given");
            }
            rotorSettings = settingsArray[i];
            i++;

            for (; i < settingsArray.length; i++) {
                if (!settingsArray[i].matches("([(][^()]+\\)\\s*)")) {
                    throw error("Bad rotor settings line: Invalid cycles");
                }
                cycles = cycles + settingsArray[i];
            }

            M.setRotors(rotorSettings);
            M.setPlugboard(new Permutation(cycles, M.getAlphabet()));
        }
    }

    /** Check if string array has duplicate elements.
     * @param s the String array
     */
    private void hasDuplicates(String[] s) {
        for (int i = 0; i < s.length - 1; i++) {
            for (int j = i + 1; j < s.length; j++) {
                if (s[i].equals(s[j])) {
                    throw error("Duplicate rotors in settings line");
                }
            }
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int count = 0;
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (count == 5) {
                _output.append(' ');
                count = 1;
            } else {
                count = count + 1;
            }
            _output.append(c);
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
