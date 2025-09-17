package com.teamcity.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify which browsers a test can run on.
 * If the configured browser is not in the list, the test will be skipped.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Browsers {
    /**
     * Array of supported browser names (case-insensitive).
     * Common values: "chrome", "firefox", "safari", "edge", "ie"
     *
     * @return array of supported browser names
     */
    String[] value();
}
