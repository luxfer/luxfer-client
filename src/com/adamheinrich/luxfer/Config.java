package com.adamheinrich.luxfer;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private File configFile;

    private Properties config;

    public Config(String filename) {
        configFile = new File(filename);

        config = new Properties();

        load();
    }

    public void load() {
        try {
            config.load(new FileInputStream(configFile));
        } catch (IOException e) {
            System.out.println("ERROR: Can't load " + configFile.getName());
        }
    }

    public void save() {
        try {
            config.store(new FileOutputStream(configFile), "");
        } catch (IOException ex) {
            System.out.println("ERROR: Can't write to " + configFile.getName());
        }
    }

    public void delete() {
        configFile.delete();
        config.clear();
    }

    public int getInteger(String key, int defaultValue) {
        return Integer.parseInt(config.getProperty(key, "" + defaultValue));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(config.getProperty(key, "" + defaultValue));
    }

    public String getString(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    public Point2D[] getPoints(String key, int count, Point2D[] defaultValue) {
        Point2D points[] = new Point2D[count];

        for (int i = 0; i < count; i++) {
            String value = config.getProperty(key + "[" + i + "]", "");
            boolean loaded = false;

            if (value.length() > 0) {
                String parts[] = value.split(",");

                if (parts.length == 2) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);

                    points[i] = new Point2D.Double(x, y);
                    loaded = true;
                }
            }

            if (!loaded) {
                if (defaultValue.length > i && defaultValue[i] != null) {
                    points[i] = new Point2D.Double(defaultValue[i].getX(), defaultValue[i].getY());
                } else {
                    points[i] = new Point2D.Double(0, 0);
                }
            }
        }

        return points;
    }

    public void setString(String key, String value) {
        config.put(key, value);
    }
    
    public void setInteger(String key, int value) {
        config.put(key, "" + value);
    }

    public void setDouble(String key, double value) {
        config.put(key, "" + value);
    }

    public void setBoolean(String key, boolean value) {
        config.put(key, "" + value);
    }

    public void setPoints(String key, Point2D points[]) {
        for (int i = 0; i < points.length; i++) {
            String value = points[i].getX() + "," + points[i].getY();
            config.put(key + "[" + i + "]", value);
        }
    }
}
