package com.teamcity.api.models.comparison;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ModelComparator {

    private ModelComparator() {
        // Utility class - prevent instantiation
    }

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

    public static final class ComparisonResult {
        private final List<Mismatch> mismatches;

        public ComparisonResult(List<Mismatch> mismatches) {
            this.mismatches = mismatches;
        }

        public boolean isSuccess() {
            return mismatches.isEmpty();
        }

        public List<Mismatch> getMismatches() {
            return mismatches;
        }

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

    public static final class Mismatch {
        private final String fieldName;
        private final Object expected;
        private final Object actual;

        public Mismatch(String fieldName, Object expected, Object actual) {
            this.fieldName = fieldName;
            this.expected = expected;
            this.actual = actual;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getExpected() {
            return expected;
        }

        public Object getActual() {
            return actual;
        }
    }
}
