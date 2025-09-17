package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Represents a collection of build steps in TeamCity.
 * <p>
 * This class models a collection of build steps in the TeamCity system. It is used
 * to wrap a list of Step objects for API responses that return multiple build steps
 * or for configuring multiple steps in a build type.
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
 * @see Step
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Steps extends BaseModel {

    /**
     * List of build steps.
     * <p>
     * This field contains a collection of Step objects representing the sequence
     * of build steps that will be executed during the build process.
     * </p>
     */
    private List<Step> step;

}
