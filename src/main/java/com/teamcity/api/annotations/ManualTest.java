package com.teamcity.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
/**
 * Annotation for marking tests that are manual and don't have implemented steps.
 * These tests only contain Allure step descriptions and logic behind the steps.
 */
public @interface ManualTest {
    String value() default "";
}
