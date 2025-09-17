package com.teamcity.api.models.comparison;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for comparing model objects using reflection.
 * <p>
 * This class provides methods for comparing fields between two objects using
 * configurable field mappings. It uses reflection to access field values
 * and compares them as strings for consistency.
 * </p>
 * 
 * <p>
 * The comparison supports field mapping, allowing request fields to be compared
 * against differently named response fields. This is useful when the API
 * response structure differs from the request structure.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see ComparisonResult
 * @see Mismatch
 */
public final class ModelComparator {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ModelComparator() {
        // Utility class - prevent instantiation
    }

    /**
     * Compares fields between two objects based on the provided field mappings.
     * <p>
     * This method iterates through the field mappings and compares the corresponding
     * fields in the request and response objects. All values are converted to strings
     * for comparison to ensure consistency.
     * </p>
     * 
     * @param <A> the type of the request object
     * @param <B> the type of the response object
     * @param request the request object to compare
     * @param response the response object to compare against
     * @param fieldMappings map of request field names to response field names
     * @return a ComparisonResult containing any mismatches found
     */
    public static <A, B> ComparisonResult compareFields(A request, B response, Map<String, String> fieldMappings) {
        List<Mismatch> mismatches = new ArrayList<>();

        for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
            String requestField = entry.getKey();
            String responseField = entry.getValue();

            Object value1 = getFieldValue(request, requestField);
            Object value2 = getFieldValue(response, responseField);

            if (!Objects.equals(String.valueOf(value1), String.valueOf(value2))) {
                mismatches.add(new Mismatch(requestField + " -> " + responseField, value1, value2));
            }
        }

        return new ComparisonResult(mismatches);
    }

    /**
     * Retrieves the value of a field from an object using reflection.
     * <p>
     * This method searches for the field in the object's class hierarchy,
     * starting from the most specific class and moving up to parent classes.
     * The field is made accessible before reading its value.
     * </p>
     * 
     * @param obj the object to read the field from
     * @param fieldName the name of the field to read
     * @return the value of the field
     * @throws RuntimeException if the field cannot be found or accessed
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field: " + fieldName, e);
            }
        }
        throw new RuntimeException("Field not found: " + fieldName + " in class " + obj.getClass().getName());
    }

    /**
     * Represents the result of a model comparison operation.
     * <p>
     * This class encapsulates the results of comparing two model objects,
     * including any mismatches found during the comparison process.
     * </p>
     */
    public static final class ComparisonResult {
        /**
         * List of mismatches found during comparison.
         */
        private final List<Mismatch> mismatches;

        /**
         * Constructs a new ComparisonResult with the given mismatches.
         * 
         * @param mismatches the list of mismatches found during comparison
         */
        public ComparisonResult(List<Mismatch> mismatches) {
            this.mismatches = mismatches;
        }

        /**
         * Determines if the comparison was successful.
         * <p>
         * A comparison is considered successful if no mismatches were found.
         * </p>
         * 
         * @return true if no mismatches were found, false otherwise
         */
        public boolean isSuccess() {
            return mismatches.isEmpty();
        }

        /**
         * Gets the list of mismatches found during comparison.
         * 
         * @return the list of mismatches
         */
        public List<Mismatch> getMismatches() {
            return mismatches;
        }

        /**
         * Returns a string representation of the comparison result.
         * <p>
         * If the comparison was successful, returns a success message.
         * Otherwise, returns a detailed list of all mismatches found.
         * </p>
         * 
         * @return a string representation of the comparison result
         */
        @Override
        public String toString() {
            if (isSuccess()) {
                return "All fields match.";
            }
            StringBuilder sb = new StringBuilder("Mismatched fields:\n");
            for (Mismatch m : mismatches) {
                sb.append("- ").append(m.getFieldName())
                        .append(": expected=").append(m.getExpected())
                        .append(", actual=").append(m.getActual()).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Represents a single field mismatch found during model comparison.
     * <p>
     * This class encapsulates information about a specific field that did not
     * match between the request and response objects during comparison.
     * </p>
     */
    public static final class Mismatch {
        /**
         * The name of the field that mismatched.
         */
        private final String fieldName;
        
        /**
         * The expected value from the request object.
         */
        private final Object expected;
        
        /**
         * The actual value from the response object.
         */
        private final Object actual;

        /**
         * Constructs a new Mismatch with the given field information.
         * 
         * @param fieldName the name of the field that mismatched
         * @param expected the expected value
         * @param actual the actual value
         */
        public Mismatch(String fieldName, Object expected, Object actual) {
            this.fieldName = fieldName;
            this.expected = expected;
            this.actual = actual;
        }

        /**
         * Gets the name of the field that mismatched.
         * 
         * @return the field name
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * Gets the expected value from the request object.
         * 
         * @return the expected value
         */
        public Object getExpected() {
            return expected;
        }

        /**
         * Gets the actual value from the response object.
         * 
         * @return the actual value
         */
        public Object getActual() {
            return actual;
        }
    }
}
