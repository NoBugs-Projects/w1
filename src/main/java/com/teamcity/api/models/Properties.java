package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamcity.api.annotations.Parameterizable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Represents a collection of TeamCity properties.
 * <p>
 * This class models a collection of key-value properties in the TeamCity system.
 * It is used to wrap a list of Property objects for API responses that return
 * multiple properties or for setting multiple properties at once.
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
 * @see Property
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties extends BaseModel {

    /**
     * List of properties.
     * <p>
     * This field contains a collection of Property objects representing key-value
     * pairs that can be configured for various TeamCity entities.
     * </p>
     *
     * <p>
     * This field is marked with @Parameterizable annotation, meaning it can be
     * populated with provided parameters during test data generation.
     * </p>
     */
    @Parameterizable
    private List<Property> property;

}
