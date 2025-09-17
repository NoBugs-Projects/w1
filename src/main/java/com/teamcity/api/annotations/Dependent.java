package com.teamcity.api.annotations;

import com.teamcity.api.models.BaseModel;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark fields that should be filled with values from related model classes.
 * <p>
 * This annotation is used by the {@link com.teamcity.api.generators.TestDataGenerator} to
 * identify fields that should be populated with values from fields with the same name
 * in a related model class. The related class must be generated earlier in the same
 * generation iteration.
 * </p>
 * 
 * <p>
 * This is useful for maintaining relationships between entities, such as when a BuildType
 * needs to reference a Project that was created earlier in the test data generation process.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see com.teamcity.api.generators.TestDataGenerator
 * @see BaseModel
 */
@Target(FIELD)
@Retention(RUNTIME)
// Поля с этой аннотацией будут заполняться значениями полей с такими же названиями из указанного класса
// Указанный класс должен генерироваться ранее в той же итерации генерации
public @interface Dependent {

    /**
     * The related model class that contains the field to copy from.
     * <p>
     * This field specifies the BaseModel subclass that contains a field with the same
     * name as the annotated field. The value from that field will be copied to the
     * annotated field during test data generation.
     * </p>
     * 
     * @return the related model class
     */
    Class<? extends BaseModel> relatedClass();

}
