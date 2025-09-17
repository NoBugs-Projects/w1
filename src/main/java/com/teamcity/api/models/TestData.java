package com.teamcity.api.models;

import lombok.Data;

/**
 * Container class for all test data used in TeamCity API tests.
 * <p>
 * This class serves as the central container for all test data objects used
 * throughout the test suite. It is generated using TestDataGenerator.generate()
 * and provides a single point of access to all test entities.
 * </p>
 * 
 * <p>
 * The order of field definitions is important and affects the TestDataGenerator.generate()
 * method, as it determines the order in which entities are created during test data
 * generation. This ensures proper dependency management between related entities.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see com.teamcity.api.generators.TestDataGenerator
 * @see NewProjectDescription
 * @see Project
 * @see User
 * @see BuildType
 */
@Data
// Набор тестовых данных, используемых в тестах. Генерируется с помощью TestDataGenerator.generate
// Порядок определения важен и влияет на этот метод
public class TestData {

    /**
     * New project description for project creation.
     * <p>
     * This field contains the data structure used when creating new projects
     * in the TeamCity system, including project identification and parent
     * project information.
     * </p>
     */
    private NewProjectDescription newProjectDescription;
    
    /**
     * Project entity for testing.
     * <p>
     * This field contains a Project object that can be used for various
     * project-related test operations.
     * </p>
     */
    private Project project;
    
    /**
     * User entity for testing.
     * <p>
     * This field contains a User object that can be used for various
     * user-related test operations and authentication.
     * </p>
     */
    private User user;
    
    /**
     * Build type entity for testing.
     * <p>
     * This field contains a BuildType object that can be used for various
     * build type-related test operations.
     * </p>
     */
    private BuildType buildType;

}
