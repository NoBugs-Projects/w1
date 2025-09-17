package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamcity.api.annotations.Optional;
import com.teamcity.api.annotations.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a TeamCity build type (build configuration) entity.
 * <p>
 * This class models a build configuration in the TeamCity system. A build type
 * defines how builds should be executed, including build steps, triggers,
 * and other configuration settings.
 * </p>
 * 
 * <p>
 * The class uses Lombok annotations for automatic generation of getters, setters,
 * constructors, and other boilerplate code. It also uses Jackson annotations for
 * JSON serialization/deserialization with the TeamCity API.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see BaseModel
 * @see Project
 * @see Steps
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildType extends BaseModel {

    /**
     * Unique identifier for the build type.
     * <p>
     * This field is marked with @Random annotation, meaning it will be automatically
     * generated with random values during test data creation to ensure uniqueness.
     * </p>
     */
    @Random
    private String id;
    
    /**
     * The display name of the build type.
     * <p>
     * This field is marked with @Random annotation, meaning it will be automatically
     * generated with random values during test data creation to ensure uniqueness.
     * </p>
     */
    @Random
    private String name;
    
    /**
     * The project that contains this build type.
     * <p>
     * Contains information about the parent project that owns this build configuration.
     * </p>
     * 
     * @see Project
     */
    private Project project;
    
    /**
     * The build steps associated with this build type.
     * <p>
     * This field is marked with @Optional annotation, meaning it may be null
     * and is not required for basic build type creation. Contains the sequence
     * of build steps that will be executed when this build type is triggered.
     * </p>
     * 
     * @see Steps
     */
    @Optional
    private Steps steps;

}
