package com.teamcity.api.generators;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.Requesters;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Thread-safe storage for tracking created test entities and managing cleanup.
 * <p>
 * This class provides a centralized way to track entities created during test execution
 * and ensure they are properly cleaned up after tests complete. It uses a ThreadLocal
 * pattern to ensure thread safety in parallel test execution.
 * </p>
 *
 * <p>
 * The storage maintains a mapping of endpoints to sets of entity IDs, allowing for
 * efficient cleanup operations. The cleanup order is guaranteed to follow the order
 * defined in the Endpoint enum, ensuring that dependent entities are deleted before
 * their dependencies.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Endpoint
 * @see BaseModel
 * @see Requesters
 */
public final class TestDataStorage {

    /**
     * ThreadLocal instance of TestDataStorage for thread safety.
     * <p>
     * This ensures that each thread has its own instance of TestDataStorage,
     * preventing conflicts in parallel test execution.
     * </p>
     */
    private static final ThreadLocal<TestDataStorage> TEST_DATA_STORAGE = ThreadLocal.withInitial(TestDataStorage::new);

    /**
     * Map storing created entities with their corresponding endpoints.
     * <p>
     * This EnumMap maintains a mapping of endpoints to sets of entity IDs.
     * The order of iteration is guaranteed to follow the order defined in
     * the Endpoint enum, which is important for proper cleanup order.
     * </p>
     *
     * <p>
     * For the Endpoint enum, the order is always:
     * BUILD_QUEUE > BUILDS > BUILD_TYPES > USERS > PROJECTS
     * </p>
     */
    /* Набор, хранящий список созданных сущностей, с привязкой их к эндпоинтам (несколько сущностей на 1 эндпоинт).
    При обращении ко всем элементам EnumMap, например, с помощью .forEach(),
    порядок гарантировано будет соответствовать тому, в котором элементы определены в Enum файле.
    Для класса Endpoint это всегда будет BUILD_QUEUE > BUILDS > BUILD_TYPES > USERS > PROJECTS,
    даже если добавление элементов в Map было в другом порядке. В нашем случае, это полезно для
    удаления сущностей в правильном порядке (чтобы не пытаться удалять build type после project и т.п.) */
    private final EnumMap<Endpoint, Set<String>> createdEntitiesMap;

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This constructor initializes the createdEntitiesMap with an empty EnumMap.
     * </p>
     */
    private TestDataStorage() {
        createdEntitiesMap = new EnumMap<>(Endpoint.class);
    }

    /**
     * Gets the current thread's TestDataStorage instance.
     * <p>
     * This method returns the TestDataStorage instance associated with the current thread.
     * If no instance exists, a new one will be created automatically.
     * </p>
     *
     * @return the TestDataStorage instance for the current thread
     */
    public static TestDataStorage getStorage() {
        return TEST_DATA_STORAGE.get();
    }

    /**
     * Adds a created entity ID to the storage for the specified endpoint.
     * <p>
     * This method tracks entity IDs that have been created during test execution.
     * Only non-null IDs are stored. If no set exists for the endpoint, a new
     * HashSet is created automatically.
     * </p>
     *
     * @param endpoint the endpoint associated with the created entity
     * @param id the ID of the created entity
     */
    /* В Map добавляется только id созданной сущности, этого достаточно для удаления
    Условие .computeIfAbsent() создает пустое множество, если данному эндпоинту еще не соответствует ни одно.
    Далее в созданное или в ранее существовавшее множество добавляется новый id */
    public void addCreatedEntity(Endpoint endpoint, String id) {
        if (id != null) {
            createdEntitiesMap.computeIfAbsent(endpoint, key -> new HashSet<>()).add(id);
        }
    }

    /**
     * Adds a created entity to the storage for the specified endpoint.
     * <p>
     * This method extracts the ID from the provided model and adds it to the storage.
     * It's a convenience method that combines ID extraction with storage addition.
     * </p>
     *
     * @param endpoint the endpoint associated with the created entity
     * @param model the model containing the entity to track
     */
    public void addCreatedEntity(Endpoint endpoint, BaseModel model) {
        addCreatedEntity(endpoint, getEntityId(model));
    }

    /**
     * Deletes all tracked entities and clears the storage.
     * <p>
     * This method iterates through all stored entities and deletes them using
     * the provided requester. The deletion order follows the Endpoint enum order,
     * ensuring that dependent entities are deleted before their dependencies.
     * </p>
     *
     * <p>
     * After deletion, the storage is cleared to prevent attempts to delete
     * already-deleted entities in subsequent test runs.
     * </p>
     *
     * @param uncheckedSuperUser the requester to use for deletion operations
     */
    public void deleteCreatedEntities(Requesters uncheckedSuperUser) {
        createdEntitiesMap.forEach((endpoint, ids) -> ids.forEach(id ->
                uncheckedSuperUser.getRequest(endpoint).delete(id)));
        // Очистка Map необходима, так как если этого не делать и запускать более 1-ого теста, то со второго
        // будут попытки удалить уже удаленные сущности
        createdEntitiesMap.clear();
    }

    /**
     * Extracts the ID field from a BaseModel using reflection.
     * <p>
     * This method uses reflection to access the "id" field of the provided model
     * and returns its string representation. This is necessary because not all
     * BaseModel subclasses have a public getter for the ID field.
     * </p>
     *
     * @param model the model to extract the ID from
     * @return the string representation of the model's ID, or null if not found
     * @throws IllegalStateException if the ID field cannot be accessed
     */
    // Так как не все классы, наследующие BaseModel, имеют поле id, то получаем его с помощью рефлексии
    private String getEntityId(BaseModel model) {
        try {
            var idField = model.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            var idFieldValue = Objects.toString(idField.get(model), null);
            idField.setAccessible(false);
            return idFieldValue;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot get entity id", e);
        }
    }

}
