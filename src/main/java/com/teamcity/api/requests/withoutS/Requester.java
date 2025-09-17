package com.teamcity.api.requests.withoutS;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.interfaces.CrudInterface;
import com.teamcity.api.requests.Request;
import com.teamcity.api.requests.interfaces.SearchInterface;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Implementation of unvalidated API requester for TeamCity endpoints.
 * <p>
 * This class provides concrete implementation of the CrudInterface and SearchInterface
 * for making unvalidated API calls to TeamCity endpoints. It extends the base Request
 * class and implements all standard CRUD operations plus search functionality.
 * </p>
 *
 * <p>
 * All methods return raw Response objects without automatic validation, allowing
 * for manual response handling and custom validation logic in test code.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Request
 * @see CrudInterface
 * @see SearchInterface
 * @see BaseModel
 * @see Endpoint
 */
public final class Requester extends Request implements CrudInterface, SearchInterface {

    public Requester(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
    }

    @Override
    @Step("Create {model}")
    public Response create(BaseModel model) {
        return RestAssured.given()
                .spec(spec)
                .body(model)
                .post(endpoint.getUrl());
    }

    @Override
    @Step("Read {id}")
    public Response read(String id) {
        return RestAssured.given()
                .spec(spec)
                .get(endpoint.getUrl() + "/id:" + id);
    }

    @Override
    @Step("Update {id}")
    public Response update(String id, BaseModel model) {
        return RestAssured.given()
                .spec(spec)
                .body(model)
                .put(endpoint.getUrl() + "/id:" + id);
    }

    @Override
    @Step("Delete {id}")
    public Response delete(String id) {
        return RestAssured.given()
                .spec(spec)
                .delete(endpoint.getUrl() + "/id:" + id);
    }

    @Override
    @Step("Search models")
    public Response search() {
        return RestAssured.given()
                .spec(spec)
                .get(endpoint.getUrl());
    }

}
