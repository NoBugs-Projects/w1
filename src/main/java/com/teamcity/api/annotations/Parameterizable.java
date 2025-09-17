package com.teamcity.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark fields that should be parameterized during test data generation.
 * <p>
 * This annotation is used by the {@link com.teamcity.api.generators.TestDataGenerator} to
 * identify fields that should be populated with provided parameters during test data
 * generation. If parameters are provided to the generator, they will be used to fill
 * fields marked with this annotation in the order they appear in the class.
 * </p>
 * 
 * <p>
 * This is useful for test scenarios where specific values need to be set for certain
 * fields, such as when testing with specific user roles or project configurations.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see com.teamcity.api.generators.TestDataGenerator
 */
@Target(FIELD)
@Retention(RUNTIME)
// Поля с этой аннотацией будут параметризироваться при генерации, если параметры были переданы
// Например, как в api.BuildTypeTest.projectAdminCreatesBuildTypeForAnotherUserProjectTest
public @interface Parameterizable {
}
