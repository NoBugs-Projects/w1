package com.teamcity.api.requests.withS;

import com.teamcity.api.models.Agents;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.interfaces.CrudInterface;
import com.teamcity.api.requests.Request;
import com.teamcity.api.requests.withoutS.AgentsRequester;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * Implementation of validated API requester for TeamCity agents with automatic response validation.
 * <p>
 * This class provides concrete implementation of the CrudInterface for making
 * validated API calls to TeamCity agent endpoints. It extends the base Request
 * class and implements agent-specific CRUD operations with automatic response
 * validation and type-safe return values.
 * </p>
 *
 * <p>
 * This requester is specialized for agent management operations and provides
 * methods for reading and updating agent information with automatic status code
 * validation. Create and delete operations are not supported for agents as they
 * are managed by the TeamCity server.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Request
 * @see CrudInterface
 * @see BaseModel
 * @see Agents
 */
public final class AgentsRequesterWithS extends Request implements CrudInterface {

    public AgentsRequesterWithS(RequestSpecification spec) {
        super(spec, null);
    }

    @Override
    public Object create(BaseModel model) {
        return null;
    }

    @Override
    public Agents read(String id) {
        return new AgentsRequester(spec)
                .read(id)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(Agents.class);
    }

    @Override
    public BaseModel update(String id, BaseModel model) {
        var operation = model.getClass().getSimpleName();
        // Превращаем переданную модель в операцию (так как данный эндпоинт поддерживает несколько видов операций)
        // Если model принадлежит классу AuthorizedInfo, то после айди допишется операция /authorizedInfo
        operation = "/" + StringUtils.uncapitalize(operation);
        return new AgentsRequester(spec)
                .update(id + operation, model)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(model.getClass());
    }

    @Override
    public Object delete(String id) {
        return null;
    }

}
