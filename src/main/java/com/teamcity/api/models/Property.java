package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamcity.api.annotations.Parameterizable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a single TeamCity property (key-value pair).
 * <p>
 * This class models a single property in the TeamCity system. Properties are
 * used to configure various aspects of TeamCity entities such as build types,
 * projects, and agents.
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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property extends BaseModel {

    /**
     * The name of the property.
     * <p>
     * This field represents the key part of the key-value pair. It identifies
     * the specific configuration setting being defined.
     * </p>
     * 
     * <p>
     * This field is marked with @Parameterizable annotation, meaning it can be
     * populated with provided parameters during test data generation.
     * </p>
     */
    @Parameterizable
    private String name;
    
    /**
     * The value of the property.
     * <p>
     * This field represents the value part of the key-value pair. It contains
     * the actual configuration value for the property.
     * </p>
     * 
     * <p>
     * This field is marked with @Parameterizable annotation, meaning it can be
     * populated with provided parameters during test data generation.
     * </p>
     */
    @Parameterizable
    private String value;

}
