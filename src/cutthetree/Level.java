package cutthetree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used for generating new levels for a given level type.
 */
public class Level {
    private static final int NUM_LEVELS = 8;
    private static final Random random = new Random();

    /**
     * Disable creating instances of this class as it only exposes a static api
     */
    private Level() {
    }

    /**
     * Generate a new random level for the given level type
     */
    static ArrayList<ArrayList<Field>> generateLevel(LevelType type, int levelNumber) {
        URL resource;

        int number = levelNumber > 0 ? levelNumber : random.nextInt(NUM_LEVELS) + 1;
        if (type != LevelType.TUTORIAL && type != LevelType.BONUS) {
            Game.setCurrentLevel(number);
            resource = Level.class.getResource("/level/" + type.toString().toLowerCase().substring(0, 1) + number + ".txt");
        } else {
            resource = Level.class.getResource("/level/" + type.toString().toLowerCase() + ".txt");
        }

        if (resource != null) {
            try {
                return loadLevel(resource.openStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("Levels of type " + type.toString().toLowerCase() + " are not yet supported.");
        }
    }

    /**
     * Converts an input stream of a level file to a list of Fields
     */
    private static ArrayList<ArrayList<Field>> loadLevel(InputStream input) {
        // Load a level from a given file path following the given spec:
        // W: Wall
        // F: Finish
        // 1-6: Different colored Lumberaxe
        // a-f: Different colored Tree
        // Anything else as Field

        ArrayList<ArrayList<Field>> fields = new ArrayList<>();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(input));

            String line;
            int y = 0;
            while ((line = in.readLine()) != null) {
                if (line.trim().isEmpty()) break;

                for (int x = 0; x < line.length(); x++) {
                    if (fields.size() <= x) fields.add(new ArrayList<Field>());

                    char chr = line.charAt(x);
                    if (chr == 'W') {
                        fields.get(x).add(new Wall(x, y));
                    } else if (chr == 'F') {
                        fields.get(x).add(new Finish(x, y));
                    } else if (chr >= '1' && chr <= '6') {
                        fields.get(x).add(new Lumberaxe(x, y, Color.values()[chr - '1']));
                    } else if (chr >= 'a' && chr <= 'f') {
                        fields.get(x).add(new Tree(x, y, Color.values()[chr - 'a']));
                    } else {
                        fields.get(x).add(new Field(x, y));
                    }
                }

                y++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fields;
    }
}
