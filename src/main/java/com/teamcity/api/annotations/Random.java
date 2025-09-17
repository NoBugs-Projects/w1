package com.teamcity.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark fields that should be filled with random data during test data generation.
 * <p>
 * This annotation is used by the {@link com.teamcity.api.generators.TestDataGenerator} to
 * identify fields that should be populated with random values when generating test data.
 * Currently, only String fields are supported for random data generation.
 * </p>
 * 
 * <p>
 * When a field is marked with this annotation, the test data generator will automatically
 * generate random string values for that field, ensuring uniqueness across test runs.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see com.teamcity.api.generators.TestDataGenerator
 * @see com.teamcity.api.generators.RandomData
 */
@Target(FIELD)
@Retention(RUNTIME)
// Поля с этой аннотацией будут заполняться рандомными данными (реализовано только для строк)
public @interface Random {
}
