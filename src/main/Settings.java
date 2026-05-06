package main;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static final String FILE_PATH = "settings.properties";
    private Properties props = new Properties();

    public Settings() { load(); }

    public void load() {
        try (FileInputStream in = new FileInputStream(FILE_PATH)) {
            props.load(in);
        } catch (IOException e) {
            props.setProperty("volume", "80");
        }
    }

    public void save() {
        try (FileOutputStream out = new FileOutputStream(FILE_PATH)) {
            props.store(out, "The Hunger Bites Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getVolume() {
        return Integer.parseInt(props.getProperty("volume", "80"));
    }

    public void setVolume(int volume) {
        props.setProperty("volume", String.valueOf(volume));
    }
}