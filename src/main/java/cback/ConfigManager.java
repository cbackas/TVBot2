package cback;

import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.util.*;

public class ConfigManager {

    private TestBot bot;

    private File configFile;
    private JSONObject configJson;

    private static Map<String, Object> defaultConfig = new HashMap<>();
    static {
        //Insert all default config values here. They will be added on startup if they do not exist.
        defaultConfig.put("botToken", "TOKEN");
        defaultConfig.put("userCount", "0");
        defaultConfig.put("joined", "0");
        defaultConfig.put("left", "0");
        defaultConfig.put("muted", new ArrayList<String>());
        defaultConfig.put("bot_color", "023563");
        defaultConfig.put("HOMESERVER_ID", "ID");
        defaultConfig.put("COMMANDLOG_ID", "ID"); // Hub channel
        defaultConfig.put("ERORRLOG_ID", "ID"); // Hub channel
        defaultConfig.put("SERVERLOG_ID", "ID"); // Home channel
        defaultConfig.put("MESSAGELOGS_ID", "ID"); // Home channel
    }

    public ConfigManager(TestBot bot) {
        this.bot = bot;
        initConfig();
    }

    private void initConfig() {
        try {
            configFile = new File(botPath, "tvconfig.json");
            if (configFile.exists()) {
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(configFile);
                configJson = (JSONObject) parser.parse(reader);
                reader.close();
                ensureDefaultsExist();
            } else {
                writeDefaultConfig();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDefaultConfig() {
        System.out.println("Config file does not exist, writing default config.");
        configJson = new JSONObject();
        configJson.putAll(defaultConfig);

        writeConfig();
    }

    private void ensureDefaultsExist() {
        boolean addedDefaults = false;
        for (String key : defaultConfig.keySet()) {
            if (!configJson.containsKey(key)) {
                addedDefaults = true;
                configJson.put(key, defaultConfig.get(key));
            }
        }
        if (addedDefaults) {
            System.out.println("Default config value(s) not found in file, adding them...");
            writeConfig();
        }
    }

    private void writeConfig() {
        String prettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(configJson);
        try {
            FileWriter writer = new FileWriter(configFile);
            writer.write(prettyPrint);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a value from the config
     */
    public String getConfigValue(String key) {
        return (String) configJson.get(key);
    }

    /**
     * Gets array value from the config
     */
    public List<String> getConfigArray(String key) {
        return (List<String>) configJson.get(key);
    }

    /**
     * Sets a config value and writes it to the file
     */
    public void setConfigValue(String key, Object value) {
        configJson.put(key, value);
        writeConfig();
    }

    /**
     * Returns a token value, empty optional if the token has not been set or does not exist
     */
    public Optional<String> getTokenValue(String key) {
        String token = getConfigValue(key);
        if (token != null && !token.isEmpty() && !token.equalsIgnoreCase("TOKEN")) {
            return Optional.of(token);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Establishes config file path
     */
    public static File botPath;

    static {
        try {
            botPath = new File(TestBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
