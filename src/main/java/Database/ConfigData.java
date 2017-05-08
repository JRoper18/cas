package Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Ulysses Howard Smith on 4/14/2017.
 */
public class ConfigData {
    public static int simplifyOverflowLimit;
    public static boolean autoInitDatabase;
    private static ConfigData ourInstance = new ConfigData();

    public static ConfigData getInstance() {
        return ourInstance;
    }

    private ConfigData() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property values
            this.autoInitDatabase = Boolean.parseBoolean(prop.getProperty("autoinitdatabse"));
            this.simplifyOverflowLimit = Integer.parseInt(prop.getProperty("simplifyoverflowlimit"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
