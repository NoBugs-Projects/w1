package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamcity.api.annotations.Parameterizable;
import com.teamcity.api.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a user role assignment in TeamCity.
 * <p>
 * This class models a role assignment for a user in the TeamCity system. It defines
 * the specific role and scope for which the user has permissions.
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
 * @see UserRole
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role extends BaseModel {

    /**
     * The role identifier.
     * <p>
     * This field specifies the type of role being assigned to the user.
     * The default value is SYSTEM_ADMIN for administrative access.
     * </p>
     * 
     * <p>
     * This field is marked with @Parameterizable annotation, meaning it can be
     * populated with provided parameters during test data generation.
     * </p>
     */
    @Builder.Default
    @Parameterizable
    private UserRole roleId = UserRole.SYSTEM_ADMIN;
    
    /**
     * The scope of the role assignment.
     * <p>
     * This field defines the scope or context in which the role applies.
     * The default value is "g" for global scope.
     * </p>
     * 
     * <p>
     * This field is marked with @Parameterizable annotation, meaning it can be
     * populated with provided parameters during test data generation.
     * </p>
     */
    @Builder.Default
    @Parameterizable
    private String scope = "g";

}
