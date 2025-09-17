package com.teamcity.api.requests.withoutS;

import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.interfaces.CrudInterface;
import com.teamcity.api.requests.Request;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Implementation of unvalidated API requester for TeamCity server authentication settings.
 * <p>
 * This class provides concrete implementation of the CrudInterface for making
 * unvalidated API calls to TeamCity server authentication settings endpoints.
 * It extends the base Request class and implements server auth settings-specific
 * CRUD operations.
 * </p>
 *
 * <p>
 * This requester is specialized for server authentication settings management
 * and provides methods for reading and updating authentication configuration.
 * Create and delete operations are not supported for server auth settings
 * as they are managed by the TeamCity server.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Request
 * @see CrudInterface
 * @see BaseModel
 */
public final class ServerAuthSettingsRequester extends Request implements CrudInterface {

    private static final String SERVER_AUTH_SETTINGS_URL = "/app/rest/server/authSettings";

    public ServerAuthSettingsRequester(RequestSpecification spec) {
        super(spec, null);
    }

    @Override
    public Object create(BaseModel model) {
        return null;
    }

    @Override
    @Step("Read ServerAuthSettings")
    public Response read(String id) {
        return RestAssured.given()
                .spec(spec)
                .get(SERVER_AUTH_SETTINGS_URL);
    }

    @Override
    @Step("Update ServerAuthSettings")
    public Response update(String id, BaseModel model) {
        return RestAssured.given()
                .spec(spec)
                .body(model)
                .put(SERVER_AUTH_SETTINGS_URL);
    }

    @Override
    public Object delete(String id) {
        return null;
    }

}
