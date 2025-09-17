package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamcity.api.annotations.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a build step in TeamCity.
 * <p>
 * This class models a single build step in the TeamCity system. A build step
 * defines a specific action to be performed during the build process, such as
 * running a command, compiling code, or executing tests.
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
 * @see Properties
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Step extends BaseModel {

    /**
     * The name of the build step.
     * <p>
     * This field is marked with @Random annotation, meaning it will be automatically
     * generated with random values during test data creation to ensure uniqueness.
     * </p>
     */
    @Random
    private String name;
    
    /**
     * The type of the build step.
     * <p>
     * This field specifies the type of build step being configured. The default
     * value is "simpleRunner" for command-line execution steps.
     * </p>
     */
    @Builder.Default
    private String type = "simpleRunner";
    
    /**
     * The properties associated with this build step.
     * <p>
     * This field contains configuration properties specific to the build step type,
     * such as command to execute, working directory, or other step-specific settings.
     * </p>
     */
    private Properties properties;

}
