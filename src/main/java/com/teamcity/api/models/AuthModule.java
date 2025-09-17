package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a TeamCity authentication module.
 * <p>
 * This class models an authentication module in the TeamCity system. Authentication
 * modules define how users authenticate with the TeamCity server, such as through
 * HTTP Basic authentication, LDAP, or other methods.
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
// Необходимая аннотация для десериализации с помощью Jackson, позволяет отказаться от использования Gson
@Jacksonized
// Без этой аннотации сериализация в объект производилась бы по всем полям, которые пришли в респонсе,
// даже если такие поля не указаны в классе-модели (в таком случае, был бы exception)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthModule extends BaseModel {

    /**
     * The name of the authentication module.
     * <p>
     * This field specifies the type of authentication module being used.
     * The default value is "HTTP-Basic" for basic HTTP authentication.
     * </p>
     */
    @Builder.Default
    private String name = "HTTP-Basic";

}
