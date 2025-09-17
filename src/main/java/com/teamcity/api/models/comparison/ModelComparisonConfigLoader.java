package com.teamcity.api.models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration loader for model comparison rules.
 * <p>
 * This class loads comparison rules from a properties file that defines how
 * request and response model fields should be compared. The configuration
 * file specifies field mappings and which fields should be compared for
 * each model class.
 * </p>
 *
 * <p>
 * The configuration file format is:
 * <pre>
 * RequestClass=ResponseClass:field1=field1Response,field2=field2Response
 * </pre>
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see ComparisonRule
 */
public final class ModelComparisonConfigLoader {

    /**
     * Map of request class names to their corresponding comparison rules.
     */
    private final Map<String, ComparisonRule> rules = new HashMap<>();

    /**
     * Constructs a new ModelComparisonConfigLoader and loads rules from the specified file.
     * <p>
     * This constructor loads the comparison configuration from the specified properties file.
     * The file should be located in the classpath and contain comparison rules in the
     * format: RequestClass=ResponseClass:field1=field1Response,field2=field2Response
     * </p>
     *
     * @param configFile the name of the configuration file to load
     * @throws IllegalArgumentException if the config file is not found
     * @throws RuntimeException if there is an error loading the configuration
     */
    public ModelComparisonConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IllegalArgumentException("Config file not found: " + configFile);
            }
            Properties props = new Properties();
            props.load(input);
            for (String key : props.stringPropertyNames()) {
                String[] target = props.getProperty(key).split(":");
                if (target.length != 2) {
                    continue;
                }

                String responseClassName = target[0].trim();
                List<String> fields = Arrays.asList(target[1].split(","));

                rules.put(key.trim(), new ComparisonRule(responseClassName, fields));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DTO comparison config", e);
        }
    }

    /**
     * Gets the comparison rule for the specified request class.
     * <p>
     * This method looks up the comparison rule for the given request class
     * and returns it if found, or null if no rule is configured for that class.
     * </p>
     *
     * @param requestClass the request class to get the rule for
     * @return the comparison rule for the class, or null if not found
     */
    public ComparisonRule getRuleFor(Class<?> requestClass) {
        return rules.get(requestClass.getSimpleName());
    }

    /**
     * Represents a comparison rule for a specific model class.
     * <p>
     * This class encapsulates the configuration for comparing a request model
     * with its corresponding response model, including field mappings and
     * the target response class name.
     * </p>
     */
    public static final class ComparisonRule {
        /**
         * The simple name of the response class to compare against.
         */
        private final String responseClassSimpleName;

        /**
         * Map of request field names to response field names.
         */
        private final Map<String, String> fieldMappings;

        /**
         * Constructs a new ComparisonRule with the specified response class and field mappings.
         * <p>
         * This constructor parses the field pairs and creates the field mappings.
         * Each field pair can be in the format "requestField=responseField" or just
         * "fieldName" (which maps to itself).
         * </p>
         *
         * @param responseClassSimpleName the simple name of the response class
         * @param fieldPairs the list of field pairs to map
         */
        public ComparisonRule(String responseClassSimpleName, List<String> fieldPairs) {
            this.responseClassSimpleName = responseClassSimpleName;
            this.fieldMappings = new HashMap<>();

            for (String pair : fieldPairs) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    fieldMappings.put(parts[0].trim(), parts[1].trim());
                } else {
                    // fallback: same field name if mapping not explicitly given
                    fieldMappings.put(pair.trim(), pair.trim());
                }
            }
        }

        /**
         * Gets the simple name of the response class.
         *
         * @return the response class simple name
         */
        public String getResponseClassSimpleName() {
            return responseClassSimpleName;
        }

        /**
         * Gets the field mappings for this comparison rule.
         *
         * @return the map of request field names to response field names
         */
        public Map<String, String> getFieldMappings() {
            return fieldMappings;
        }
    }
}
