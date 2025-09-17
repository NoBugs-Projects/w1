package com.teamcity.api.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Configuration utility class for loading and accessing application properties.
 * <p>
 * This class provides a thread-safe way to load and access configuration properties
 * from the config.properties file. It uses a ThreadLocal pattern to ensure that
 * each thread has its own instance of the configuration, preventing thread safety issues.
 * </p>
 * 
 * <p>
 * The configuration is loaded once per thread and cached for the lifetime of that thread.
 * This provides good performance while maintaining thread safety.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 */
public final class Config {

    /**
     * The name of the configuration properties file.
     */
    private static final String CONFIG_PROPERTIES = "config.properties";
    
    /**
     * ThreadLocal instance of the Config class to ensure thread safety.
     */
    private static final ThreadLocal<Config> CONFIG = ThreadLocal.withInitial(Config::new);
    
    /**
     * The properties object containing the loaded configuration.
     */
    private final Properties properties;

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This constructor loads the properties file during initialization.
     * </p>
     */
    private Config() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Gets the current thread's Config instance.
     * <p>
     * This method returns the Config instance associated with the current thread.
     * If no instance exists, a new one will be created automatically.
     * </p>
     * 
     * @return the Config instance for the current thread
     */
    private static Config getConfig() {
        return CONFIG.get();
    }

    /**
     * Retrieves a property value by its key.
     * <p>
     * This method provides access to configuration properties loaded from the
     * config.properties file. It is thread-safe and can be called from any thread.
     * </p>
     * 
     * @param key the property key to retrieve
     * @return the property value, or null if the key is not found
     */
    public static String getProperty(String key) {
        return getConfig().properties.getProperty(key);
    }

    /**
     * Loads properties from the config.properties file.
     * <p>
     * This method attempts to load the configuration file from the classpath.
     * If the file cannot be loaded or is not found, an IllegalStateException
     * will be thrown.
     * </p>
     * 
     * @throws IllegalStateException if the properties file cannot be loaded
     */
    private void loadProperties() {
        try (var inputStream = Config.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES)) {
            properties.load(inputStream);
            // Убираем проверку на null (если файла не существует), это будет отлавливаться в блоке catch
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Cannot load properties file", e);
        }
    }

}
