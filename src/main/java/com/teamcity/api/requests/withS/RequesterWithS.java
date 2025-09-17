package com.teamcity.api.requests.withS;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.generators.TestDataStorage;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.interfaces.CrudInterface;
import com.teamcity.api.requests.Request;
import com.teamcity.api.requests.interfaces.SearchInterface;
import com.teamcity.api.requests.withoutS.Requester;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.util.List;

/**
 * Implementation of validated API requester for TeamCity endpoints with automatic response validation.
 * <p>
 * This class provides concrete implementation of the CrudInterface and SearchInterface
 * for making validated API calls to TeamCity endpoints. It extends the base Request
 * class and implements all standard CRUD operations plus search functionality with
 * automatic response validation and type-safe return values.
 * </p>
 * 
 * <p>
 * This implementation uses generics to provide type-safe responses and automatically
 * validates HTTP status codes. All created entities are automatically tracked for
 * cleanup during test teardown.
 * </p>
 * 
 * @param <T> the type of BaseModel that this requester handles
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Request
 * @see CrudInterface
 * @see SearchInterface
 * @see BaseModel
 * @see Endpoint
 * @see TestDataStorage
 */
@SuppressWarnings("unchecked")
// Реализация checked реквестов с помощью дженериков. Позволяет получать респонс с конкретным нужным типом модели
public final class RequesterWithS<T extends BaseModel> extends Request implements CrudInterface, SearchInterface {

    private final Requester requester;

    // Все реквесты, имеющие одинаковую реализацию CRUD методов, можно создать через общий конструктор
    public RequesterWithS(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
        requester = new Requester(spec, endpoint);
    }

    @Override
    public T create(BaseModel model) {
        var createdModel = (T) requester
                .create(model)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
        // После создания сущности ее айди добавляется в список созданных сущностей (для их удаления в конце)
        TestDataStorage.getStorage().addCreatedEntity(endpoint, createdModel);
        return createdModel;
    }

    @Override
    public T read(String id) {
        return (T) requester
                .read(id)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
    }

    @Override
    public T update(String id, BaseModel model) {
        return (T) requester
                .update(id, model)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
    }

    @Override
    public String delete(String id) {
        return requester
                .delete(id)
                .then().assertThat().statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().asString();
    }

    @Override
    public List<T> search() {
        return (List<T>) requester
                .search()
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath()
                .getList(StringUtils.uncapitalize(endpoint.getModelClass().getSimpleName()), endpoint.getModelClass());
    }

}
