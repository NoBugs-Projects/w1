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
 * Represents a collection of TeamCity build agents.
 * <p>
 * This class models a collection of build agents in the TeamCity system. It is used
 * to wrap a list of Agent objects for API responses that return multiple agents.
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
 * @see Agent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agents extends BaseModel {

    /**
     * List of build agents.
     * <p>
     * This field contains a collection of Agent objects representing all the
     * build agents available in the TeamCity system.
     * </p>
     */
    private List<Agent> agent;

}
