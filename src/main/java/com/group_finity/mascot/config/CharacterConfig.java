package com.group_finity.mascot.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;
import java.util.logging.*;
import com.group_finity.mascotapp.Constants;

public class CharacterConfig {
    private static final Logger log = Logger.getLogger(CharacterConfig.class.getName());
    private static final String DEFAULT_NAME = "Shimeji";
    private static final String DEFAULT_PERSONALITY = "a cute and friendly desktop companion";
    private static final String DEFAULT_GREETING = "How can I help you today?";
    
    private static Properties loadProperties(String imageSet) {
        Properties props = new Properties();
        
        try {
            Path configPath = Constants.JAR_DIR.resolve("img").resolve(imageSet).resolve("character.properties");
            
            log.info("Loading properties for imageSet: " + imageSet);
            log.info("Trying to load from path: " + configPath.toAbsolutePath());
            
            if (!Files.exists(configPath)) {
                log.info("File does not exist, creating default config");
                createDefaultConfig(configPath);
            }
            
            // 使用 UTF-8 编码读取文件
            try (InputStreamReader reader = new InputStreamReader(
                    Files.newInputStream(configPath))) {
                props.load(reader);
                log.info("Loaded properties: " + props);
            }
            
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to load character properties", e);
        }
        
        return props;
    }
    
    private static void createDefaultConfig(Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());
        Properties defaults = new Properties();
        defaults.setProperty("name", DEFAULT_NAME);
        defaults.setProperty("personality", DEFAULT_PERSONALITY);
        defaults.setProperty("greeting", DEFAULT_GREETING);
        
        // 使用 UTF-8 编码写入文件
        try (OutputStreamWriter writer = new OutputStreamWriter(
                Files.newOutputStream(configPath))) {
            defaults.store(writer, "Character configuration (UTF-8 encoding)");
        }
    }
    
    public static void saveCharacterConfig(String imageSet, String name, String personality, String greeting) {
        try {
            Properties props = new Properties();
            props.setProperty("name", name);
            props.setProperty("personality", personality);
            props.setProperty("greeting", greeting);
            
            Path configPath = Constants.JAR_DIR.resolve("img").resolve(imageSet).resolve("character.properties");
            
            // 使用 UTF-8 编码写入文件
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    Files.newOutputStream(configPath))) {
                props.store(writer, "Character configuration (UTF-8 encoding)");
                log.info("Saved character properties to: " + configPath);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to save character properties", e);
        }
    }
    
    public static String getPersonality(String imageSet) {
        return loadProperties(imageSet).getProperty("personality", DEFAULT_PERSONALITY);
    }
    
    public static String getCharacterName(String imageSet) {
        return loadProperties(imageSet).getProperty("name", DEFAULT_NAME);
    }
    
    public static String getGreeting(String imageSet) {
        Properties props = loadProperties(imageSet);
        String name = props.getProperty("name", DEFAULT_NAME);
        String greeting = props.getProperty("greeting", DEFAULT_GREETING);
        
        log.info("Getting greeting for imageSet: " + imageSet);
        log.info("Name: " + name);
        log.info("Greeting: " + greeting);
        
        return greeting;
    }
    
    public static void showConfigDialog(String imageSet) {
        showConfigDialog(imageSet, null);
    }
    
    public static void showConfigDialog(String imageSet, Runnable onSaveCallback) {
        CharacterConfigDialog dialog = new CharacterConfigDialog(imageSet, onSaveCallback);
        dialog.setVisible(true);
    }
}