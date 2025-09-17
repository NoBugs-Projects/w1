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
 * Represents a collection of user roles in TeamCity.
 * <p>
 * This class models a collection of role assignments in the TeamCity system.
 * It is used to wrap a list of Role objects for API responses that return
 * multiple role assignments or for setting multiple roles at once.
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
 * @see Role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Roles extends BaseModel {

    /**
     * List of role assignments.
     * <p>
     * This field contains a collection of Role objects representing the various
     * roles and permissions assigned to a user in the TeamCity system.
     * </p>
     */
    private List<Role> role;

}
