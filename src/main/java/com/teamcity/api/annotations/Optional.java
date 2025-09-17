package com.teamcity.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark fields that should not be automatically generated during test data generation.
 * <p>
 * This annotation is used by the {@link com.teamcity.api.generators.TestDataGenerator} to
 * identify fields that should be skipped during automatic test data generation. Fields
 * marked with this annotation must be set manually in the test code.
 * </p>
 *
 * <p>
 * This is useful for fields that require specific values or complex setup that cannot
 * be automatically generated, such as build steps or other configuration objects that
 * need to be created with specific parameters.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see com.teamcity.api.generators.TestDataGenerator
 */
@Target(FIELD)
@Retention(RUNTIME)
// Поля с этой аннотацией не будут генерироваться автоматически, при генерации классов, их содержащих, или их родителей
// Такие поля надо сетать вручную. Например, как Steps в api.StartBuildTest.userStartsBuildTest
public @interface Optional {
}
