package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamcity.api.annotations.Dependent;
import com.teamcity.api.annotations.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a TeamCity project entity.
 * <p>
 * This class models a project in the TeamCity system. A project is a container
 * for build configurations and serves as the top-level organizational unit
 * in TeamCity's hierarchy.
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
 * @see NewProjectDescription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends BaseModel {

    /**
     * Unique identifier for the project.
     * <p>
     * This field is marked with @Dependent and @Random annotations, meaning it will be
     * automatically generated with random values during test data creation and is
     * dependent on the NewProjectDescription class for proper test data generation.
     * </p>
     */
    @Dependent(relatedClass = NewProjectDescription.class)
    @Random
    private String id;

    /**
     * The display name of the project.
     * <p>
     * This field is marked with @Dependent and @Random annotations, meaning it will be
     * automatically generated with random values during test data creation and is
     * dependent on the NewProjectDescription class for proper test data generation.
     * </p>
     */
    @Dependent(relatedClass = NewProjectDescription.class)
    @Random
    private String name;

    /**
     * The locator string used for project identification in API calls.
     * <p>
     * This field is used for referencing the project in various TeamCity API operations
     * and is typically used in URL paths and query parameters.
     * </p>
     */
    private String locator;

}
