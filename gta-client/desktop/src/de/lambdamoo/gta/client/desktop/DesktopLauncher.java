package de.lambdamoo.gta.client.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.Properties;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.ScreenResolution;

public class DesktopLauncher {
    public static void main(String[] args) {
        Properties props = parseOptions(args, new Properties());
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = (int) ScreenResolution.getResolution().getVirtualWidth();
        config.height = (int) ScreenResolution.getResolution().getVirtualHeight();
        Core core = new Core(new PlayServiceLogger());
        if (props.containsKey("dev")) {
            config.x = -1300;
            core.setDevelopment(true);
        }
        new LwjglApplication(core, config);
    }

    /**
     * Parst die übergebenen Kommandozeilenparameter und gibt sie als Properties
     * zurück. Wenn kein Wert angegeben wurde, wird true verwendet. Beispiel:
     * <p>
     * <pre>
     *    -option1=value1 --option2 -option3 value3
     * </pre>
     * <p>
     * wird zu
     * <p>
     * <pre>
     * { option1 = value1, option2 = true, option3 = value3 }
     * </pre>
     */
    private static Properties parseOptions(String[] args, Properties defaults) {
        Properties options = new Properties(defaults);

        String key;
        String value;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                int begin = (arg.startsWith("--")) ? 2 : 1;
                if (arg.indexOf('=') > 0) {
                    key = arg.substring(begin, arg.indexOf('='));
                    value = arg.substring(arg.indexOf('=') + 1, arg.length());
                } else {
                    key = arg.substring(begin);
                    if ((args.length > (i + 1)) && !args[i + 1].startsWith("-")) {
                        value = args[i + 1];
                        i++;
                    } else
                        value = "true";
                }

                options.setProperty(key, value);
            }
        }

        return options;
    }
}
