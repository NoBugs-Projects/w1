package com.teamcity.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking tests that are manual and don't have implemented steps.
 * <p>
 * This annotation is used to identify test methods that are designed for manual execution
 * and contain only Allure step descriptions and logic behind the steps, without actual
 * automated test implementation.
 * </p>
 *
 * <p>
 * These tests serve as documentation for manual testing procedures and can be used
 * to track test coverage and requirements without implementing full automation.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 */
@Target(METHOD)
@Retention(RUNTIME)
// Annotation for marking tests that are manual and don't have implemented steps.
// These tests only contain Allure step descriptions and logic behind the steps.
public @interface ManualTest {

    /**
     * Optional description for the manual test.
     * <p>
     * This field can be used to provide additional information about the manual test,
     * such as specific steps to follow or expected outcomes.
     * </p>
     *
     * @return the description of the manual test, or empty string if not specified
     */
    String value() default "";
}
