package com.teamcity.api.requests.withS;

import com.teamcity.api.models.BaseModel;
import com.teamcity.api.models.ServerAuthSettings;
import com.teamcity.api.requests.interfaces.CrudInterface;
import com.teamcity.api.requests.Request;
import com.teamcity.api.requests.withoutS.ServerAuthSettingsRequester;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

/**
 * Implementation of validated API requester for TeamCity server authentication settings
 * with automatic response validation.
 * <p>
 * This class provides concrete implementation of the CrudInterface for making
 * validated API calls to TeamCity server authentication settings endpoints.
 * It extends the base Request class and implements server auth settings-specific
 * CRUD operations with automatic response validation and type-safe return values.
 * </p>
 *
 * <p>
 * This requester is specialized for server authentication settings management
 * and provides methods for reading and updating authentication configuration
 * with automatic status code validation. Create and delete operations are not
 * supported for server auth settings as they are managed by the TeamCity server.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Request
 * @see CrudInterface
 * @see BaseModel
 * @see ServerAuthSettings
 */
// Данный реквест имеет отличительную реализацию CRUD методов, поэтому находится в отдельном классе
public final class ServerAuthSettingsRequesterWithS extends Request implements CrudInterface {

    public ServerAuthSettingsRequesterWithS(RequestSpecification spec) {
        super(spec, null);
    }

    @Override
    public Object create(BaseModel model) {
        return null;
    }

    @Override
    public ServerAuthSettings read(String id) {
        return new ServerAuthSettingsRequester(spec)
                .read(id)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(ServerAuthSettings.class);
    }

    @Override
    public ServerAuthSettings update(String id, BaseModel model) {
        return new ServerAuthSettingsRequester(spec)
                .update(id, model)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(ServerAuthSettings.class);
    }

    @Override
    public Object delete(String id) {
        return null;
    }

}
