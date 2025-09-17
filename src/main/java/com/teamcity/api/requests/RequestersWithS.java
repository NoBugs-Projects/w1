package com.teamcity.api.requests;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.withS.RequesterWithS;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.EnumMap;

@Getter
public final class RequestersWithS {

    private final EnumMap<Endpoint, RequesterWithS<?>> checkedRequests = new EnumMap<>(Endpoint.class);

    public RequestersWithS(RequestSpecification spec) {
        // Создаем все виды реквестов (указанные в Endpoint) для юзера, переданного в spec
        for (var endpoint : Endpoint.values()) {
            checkedRequests.put(endpoint, new RequesterWithS<>(spec, endpoint));
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseModel> RequesterWithS<T> getRequest(Endpoint endpoint) {
        return (RequesterWithS<T>) checkedRequests.get(endpoint);
    }

}
