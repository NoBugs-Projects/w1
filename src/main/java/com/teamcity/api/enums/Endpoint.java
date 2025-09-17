package com.teamcity.api.enums;

import com.teamcity.api.models.BaseModel;
import com.teamcity.api.models.Build;
import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.Project;
import com.teamcity.api.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of TeamCity API endpoints and their corresponding model classes.
 * <p>
 * This enum defines the mapping between TeamCity API endpoints and the model classes
 * that represent the data returned by those endpoints. It serves as a central
 * configuration point for API request routing and response deserialization.
 * </p>
 * 
 * <p>
 * The order of enum constants is important and affects the TestDataStorage.createdEntitiesMap
 * ordering, which is used for proper cleanup of created entities during test teardown.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see BaseModel
 */
@AllArgsConstructor
@Getter
public enum Endpoint {

    // Описываем соответствие между эндпоинтом и моделью, которую он возвращает
    // Порядок определения важен и влияет на TestDataStorage.createdEntitiesMap
    
    /**
     * Endpoint for build queue operations.
     * <p>
     * This endpoint is used for managing the build queue, including queuing new builds
     * and retrieving information about queued builds.
     * </p>
     */
    BUILD_QUEUE("/app/rest/buildQueue", Build.class),
    
    /**
     * Endpoint for build operations.
     * <p>
     * This endpoint is used for managing individual builds, including retrieving
     * build information, updating build status, and performing build-related operations.
     * </p>
     */
    BUILDS("/app/rest/builds", Build.class),
    
    /**
     * Endpoint for build type operations.
     * <p>
     * This endpoint is used for managing build configurations (build types),
     * including creating, updating, and deleting build type definitions.
     * </p>
     */
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class),
    
    /**
     * Endpoint for user operations.
     * <p>
     * This endpoint is used for managing user accounts, including creating users,
     * updating user information, and managing user permissions.
     * </p>
     */
    USERS("/app/rest/users", User.class),
    
    /**
     * Endpoint for project operations.
     * <p>
     * This endpoint is used for managing projects, including creating projects,
     * updating project settings, and managing project hierarchy.
     * </p>
     */
    PROJECTS("/app/rest/projects", Project.class);

    /**
     * The URL path for this API endpoint.
     * <p>
     * This field contains the relative URL path that will be appended to the base URI
     * when making requests to this endpoint.
     * </p>
     */
    private final String url;
    
    /**
     * The model class associated with this endpoint.
     * <p>
     * This field specifies the BaseModel subclass that represents the data structure
     * returned by this endpoint. It is used for response deserialization and type safety.
     * </p>
     */
    // Все классы, наследующие BaseModel
    private final Class<? extends BaseModel> modelClass;

}
