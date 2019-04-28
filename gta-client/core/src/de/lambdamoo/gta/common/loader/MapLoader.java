package de.lambdamoo.gta.common.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

import de.lambdamoo.gta.client.util.MyLogger;

public class MapLoader {

    public static final String FILE_PATH = "definitions/";

    /**
     * Load the definitions for: XP, Description, Hitpoints, Leveling
     *
     * @return
     */
    public GameDefinitions loadDefinitions() {
        GameDefinitions result = new GameDefinitions();

        result.experience = scanLines(loadFile(FILE_PATH + "Experience.CSV"), new LineScanner() {
            @Override
            public Object scanLine(String[] cells) {
                if (cells[0].trim().length() == 0) {
                    return null;
                }
                ExperienceEntry entry = new ExperienceEntry();
                entry.level = Integer.parseInt(cells[0].trim());
                entry.xp = Integer.parseInt(cells[1].trim());
                return entry;
            }
        }, true);

        result.description = scanLines(loadFile(FILE_PATH + "Description.CSV"), new LineScanner() {
            @Override
            public Object scanLine(String[] cells) {
                if (cells[0].trim().length() == 0) {
                    return null;
                }
                DescriptionEntry entry = new DescriptionEntry();
                entry.className = cells[0].trim();
                entry.text = cells[1].trim();
                return entry;
            }
        }, true);

        result.hitpoints = scanLines(loadFile(FILE_PATH + "Hitpoints.CSV"), new LineScanner() {
            @Override
            public Object scanLine(String[] cells) {
                if (cells[0].trim().length() == 0) {
                    return null;
                }
                HitpointEntry entry = new HitpointEntry();
                entry.className = cells[0].trim();
                entry.hp = cells[1].trim();
                return entry;
            }
        }, true);

        result.leveling = scanLines(loadFile(FILE_PATH + "Leveling.CSV"), new LineScanner() {
            @Override
            public Object scanLine(String[] cells) {
                if (cells[0].trim().length() == 0) {
                    return null;
                }
                LevelingEntry entry = new LevelingEntry();
                entry.className = cells[0].trim();
                entry.level = Integer.parseInt(cells[1].trim());
                entry.baseAttack = scanBaseAttack(cells[2].trim());
                entry.saveFortitude = Integer.parseInt(cells[3].trim());
                entry.saveReflex = Integer.parseInt(cells[4].trim());
                entry.saveWill = Integer.parseInt(cells[5].trim());
                entry.special = cells[6].trim();
                entry.spells = new int[9];
                for (int index = 0; index < 9; index++) {
                    if (cells.length <= index + 7) {
                        break;
                    }
                    entry.spells[index] = scanSpell(cells[index + 7].trim());
                }
                return entry;
            }
        }, true);

        return result;
    }

    private int[] scanBaseAttack(String str) {
        String[] values = str.split("/");
        int[] result = new int[values.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(values[i]);
        }
        return result;
    }

    /**
     * This method parses the spell description. Either the value or the value before the + sign is converted to Integer.
     *
     * @param str
     * @return
     */
    private int scanSpell(String str) {
        if (str == null || str.trim().length() == 0) {
            return 0;
        }
        int index = str.indexOf("+");
        if (index == -1) {
            return Integer.parseInt(str);
        } else {
            return Integer.parseInt(str.substring(0, index));
        }
    }

    public List scanLines(String[] lines, LineScanner scanner, boolean skipHeadline) {
        List result = new ArrayList();
        int counter = 0;
        int startLine = 0;
        if (skipHeadline) {
            startLine++;
        }
        for (int i = startLine; i < lines.length; i++) {
            String line = lines[i];
            String[] cells = line.split(";");
            if (cells[0].startsWith("###")) {
                break;
            }
            if (cells.length >= 1) {
                Object obj = scanner.scanLine(cells);
                if (obj != null) {
                    result.add(obj);
                }
            }
        }

        MyLogger.getInstance().log("MapLoader", "Loaded " + counter + " definitions");
        return result;
    }

    private String[] loadFile(String filename) {
        FileHandle handle = Gdx.files.internal(filename);
        String text = handle.readString();
        String[] lines = text.split("\\r?\\n");
        return lines;
    }

    private int occurenceOf(String str, char search) {
        int result = 0;
        int max = str.length();
        for (int i = 0; i < max; i++) {
            if (str.charAt(i) == search) {
                result++;
            }
        }
        return result;
    }

    interface LineScanner {
        Object scanLine(String[] cells);
    }


}
