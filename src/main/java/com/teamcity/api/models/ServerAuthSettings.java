package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents server authentication settings in TeamCity.
 * <p>
 * This class models the authentication configuration settings for the TeamCity
 * server. It includes settings for permission management and authentication
 * modules configuration.
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
 * @see AuthModules
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerAuthSettings extends BaseModel {

    /**
     * Flag indicating whether per-project permissions are enabled.
     * <p>
     * This field determines whether the TeamCity server uses per-project
     * permission management or global permission management.
     * </p>
     */
    private Boolean perProjectPermissions;

    /**
     * The authentication modules configuration.
     * <p>
     * This field contains the collection of authentication modules that are
     * configured for the TeamCity server.
     * </p>
     */
    private AuthModules modules;

}
