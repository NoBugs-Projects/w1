package com.teamcity.api.generators;

import com.teamcity.api.annotations.Dependent;
import com.teamcity.api.annotations.Optional;
import com.teamcity.api.annotations.Parameterizable;
import com.teamcity.api.annotations.Random;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.models.TestData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for generating test data using reflection and annotations.
 * <p>
 * This class provides methods for automatically generating test data objects
 * based on field annotations and type information. It uses reflection to
 * inspect model classes and generate appropriate test data based on the
 * annotations present on each field.
 * </p>
 *
 * <p>
 * The generator supports several annotation types for controlling data generation:
 * <ul>
 * <li>{@link Random} - generates random string values</li>
 * <li>{@link Optional} - skips field generation</li>
 * <li>{@link Parameterizable} - uses provided parameters</li>
 * <li>{@link Dependent} - uses values from related models</li>
 * </ul>
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see BaseModel
 * @see TestData
 * @see Random
 * @see Optional
 * @see Parameterizable
 * @see Dependent
 */
public final class TestDataGenerator {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestDataGenerator() {
    }

    /* Основной метод генерации тестовых данных. Если у поля аннотация Optional, оно пропускается, иначе выбор:
    1) если у поля аннотация Parameterizable, и в метод были переданы параметры, то поочередно (по мере встречи полей с
    этой аннотацией) устанавливаются переданные параметры. То есть, если по ходу генерации было пройдено 4 поля с
    аннотацией Parameterizable, но параметров в метод было передано 3, то значения будут установлены только у первых
    трех встретившихся элементов в порядке их передачи в метод. Поэтому также важно следить за порядком полей
    в @Data классе; 2) иначе, если у поля аннотация Dependent, то значение поля устанавливается значением поля
    с таким же названием из модели, находящейся в generatedModels и принадлежащей классу relatedClass (если такая
    присутствует); 3) иначе, если у поля аннотация Random и это строка, оно заполняется рандомными данными; 4) иначе,
    если поле - наследник класса BaseModel, то оно генерируется, рекурсивно отправляясь в новый метод generate;
    5) иначе, если поле - List, у которого generic type - наследник класса BaseModel, то оно устанавливается списком
    из одного элемента, который генерируется, рекурсивно отправляясь в новый метод generate.
    Параметр generatedModels передается, когда генерируется несколько сущностей в цикле, и содержит в себе
    сгенерированные на предыдущих шагах сущности. Позволяет при генерации сложной сущности, которая своим полем содержит
    другую сущность, сгенерированную на предыдущем шаге, установить ее, а не генерировать новую. Данная логика
    применяется только для пунктов 3 и 4. Например, если был сгенерирован Project, то передав его параметром
    generatedModels при генерации BuildType, он будет переиспользоваться при установке поля Project project,
    вместо генерации нового */

    /**
     * Generates a test data object of the specified class using reflection and annotations.
     * <p>
     * This is the main method for generating test data. It processes fields in the following order:
     * <ol>
     * <li>If a field has the {@link Optional} annotation, it is skipped</li>
     * <li>If a field has the {@link Parameterizable} annotation and parameters were provided,
     *     the provided parameters are used in order of field appearance</li>
     * <li>If a field has the {@link Dependent} annotation, its value is set from a field with
     *     the same name in a related model from generatedModels</li>
     * <li>If a field has the {@link Random} annotation and is a String, it is filled with random data</li>
     * <li>If a field is a BaseModel subclass, it is generated recursively</li>
     * <li>If a field is a List of BaseModel subclasses, it is set to a list containing one generated element</li>
     * </ol>
     * </p>
     *
     * <p>
     * The generatedModels parameter is used when generating multiple entities in a loop and contains
     * entities generated in previous steps. This allows for reuse of previously generated entities
     * when generating complex entities that contain other entities as fields.
     * </p>
     *
     * @param <T> the type of the model to generate
     * @param generatedModels list of previously generated models for reuse
     * @param generatorClass the class of the model to generate
     * @param parameters optional parameters for Parameterizable fields
     * @return a generated instance of the specified class
     * @throws IllegalStateException if generation fails due to reflection errors
     */
    public static <T extends BaseModel> T generate(List<BaseModel> generatedModels, Class<T> generatorClass,
                                                   Object... parameters) {
        try {
            var instance = generatorClass.getDeclaredConstructor().newInstance();
            for (var field : generatorClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(Optional.class)) {
                    var generatedClass = generatedModels.stream().filter(m
                            -> m.getClass().equals(field.getType())).findFirst();
                    if (field.isAnnotationPresent(Parameterizable.class) && parameters.length > 0) {
                        field.set(instance, parameters[0]);
                        parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
                    } else if (field.isAnnotationPresent(Dependent.class) && generatedModels.stream().anyMatch(m
                            -> m.getClass().equals(field.getAnnotation(Dependent.class).relatedClass()))) {
                        var relatedClass = field.getAnnotation(Dependent.class).relatedClass();
                        var generatedRelatedModel = generatedModels.stream().filter(m
                                -> m.getClass().equals(relatedClass)).findFirst();
                        if (generatedRelatedModel.isPresent()) {
                            var relatedField = relatedClass.getDeclaredField(field.getName());
                            relatedField.setAccessible(true);
                            var relatedValue = relatedField.get(generatedRelatedModel.get());
                            relatedField.setAccessible(false);
                            field.set(instance, relatedValue);
                        }
                    } else if (field.isAnnotationPresent(Random.class) && String.class.equals(field.getType())) {
                        field.set(instance, RandomData.getString());
                    } else if (BaseModel.class.isAssignableFrom(field.getType())) {
                        var finalParameters = parameters;
                        field.set(instance, generatedClass.orElseGet(() -> generate(
                                generatedModels, field.getType().asSubclass(BaseModel.class), finalParameters)));
                    } else if (List.class.isAssignableFrom(field.getType())
                            && field.getGenericType() instanceof ParameterizedType pt) {
                        var typeClass = (Class<?>) pt.getActualTypeArguments()[0];
                        if (BaseModel.class.isAssignableFrom(typeClass)) {
                            var finalParameters = parameters;
                            field.set(instance, generatedClass.map(List::of).orElseGet(() -> List.of(generate(
                                    generatedModels, typeClass.asSubclass(BaseModel.class), finalParameters))));
                        }
                    }
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException | NoSuchFieldException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }

    /**
     * Generates a single entity without any previously generated models.
     * <p>
     * This is a convenience method that calls the main generate method with an empty
     * list of generated models.
     * </p>
     *
     * @param <T> the type of the model to generate
     * @param generatorClass the class of the model to generate
     * @param parameters optional parameters for Parameterizable fields
     * @return a generated instance of the specified class
     */
    // Метод, чтобы сгенерировать одну сущность. Передает пустой параметр generatedModels
    public static <T extends BaseModel> T generate(Class<T> generatorClass, Object... parameters) {
        return generate(Collections.emptyList(), generatorClass, parameters);
    }

    /**
     * Generates all entities based on all fields specified in TestData.
     * <p>
     * This method makes the TestData class the single point of scalability.
     * It is sufficient to add a new field only there for a new object to start
     * being generated in test data. The iteration goes in the order in which
     * fields are defined in the file.
     * </p>
     *
     * @return a complete TestData instance with all fields populated
     * @throws IllegalStateException if generation fails due to reflection errors
     */
    /* Генерация всех сущностей, на основании всех полей, указанных в TestData. Делает класс TestData единственной
    точкой масштабируемости. Достаточно добавить новое поле только туда, чтобы новый объект начал генерироваться
    в тестовых данных. Перебор идет в порядке, в котором поля определены в файле */
    public static TestData generate() {
        try {
            var instance = TestData.class.getDeclaredConstructor().newInstance();
            var generatedModels = new ArrayList<BaseModel>();
            for (var field : TestData.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (BaseModel.class.isAssignableFrom(field.getType())) {
                    var generatedModel = generate(generatedModels, field.getType().asSubclass(BaseModel.class));
                    field.set(instance, generatedModel);
                    generatedModels.add(generatedModel);
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }

}
