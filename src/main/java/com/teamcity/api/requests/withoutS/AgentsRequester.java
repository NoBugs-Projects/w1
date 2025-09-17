package com.teamcity.api.requests.withoutS;

import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.interfaces.CrudInterface;
import com.teamcity.api.requests.Request;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Implementation of unvalidated API requester for TeamCity agents.
 * <p>
 * This class provides concrete implementation of the CrudInterface for making
 * unvalidated API calls to TeamCity agent endpoints. It extends the base Request
 * class and implements agent-specific CRUD operations.
 * </p>
 * 
 * <p>
 * This requester is specialized for agent management operations and provides
 * methods for reading and updating agent information. Create and delete operations
 * are not supported for agents as they are managed by the TeamCity server.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Request
 * @see CrudInterface
 * @see BaseModel
 */
public final class AgentsRequester extends Request implements CrudInterface {

    private static final String AGENTS_URL = "/app/rest/agents";

    public AgentsRequester(RequestSpecification spec) {
        super(spec, null);
    }

    @Override
    public Object create(BaseModel model) {
        return null;
    }

    @Override
    @Step("Read Agents")
    public Response read(String locator) {
        return RestAssured.given()
                .spec(spec)
                .get(AGENTS_URL + "?locator=" + locator);
    }

    @Override
    @Step("Update Agent")
    public Response update(String id, BaseModel model) {
        return RestAssured.given()
                .spec(spec)
                .body(model)
                .put(AGENTS_URL + "/id:" + id);
    }

    @Override
    public Object delete(String id) {
        return null;
    }

}
