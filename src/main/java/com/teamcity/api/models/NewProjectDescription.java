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
 * Represents a new project description for TeamCity project creation.
 * <p>
 * This class models the data structure used when creating a new project in the
 * TeamCity system. It includes the project's identification information and
 * its parent project in the project hierarchy.
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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewProjectDescription extends BaseModel {

    /**
     * Unique identifier for the new project.
     * <p>
     * This field is marked with @Random annotation, meaning it will be automatically
     * generated with random values during test data creation to ensure uniqueness.
     * </p>
     */
    @Random
    private String id;

    /**
     * The display name of the new project.
     * <p>
     * This field is marked with @Random annotation, meaning it will be automatically
     * generated with random values during test data creation to ensure uniqueness.
     * </p>
     */
    @Random
    private String name;

    /**
     * The parent project in the project hierarchy.
     * <p>
     * This field is marked with @Optional annotation, meaning it may be null
     * and is not required for basic project creation. The default value is
     * set to the root project ("_Root").
     * </p>
     */
    @Builder.Default
    @Optional
    private Project parentProject = new Project(null, null, "_Root");

}
