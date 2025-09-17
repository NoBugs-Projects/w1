package com.teamcity.api.requests;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.requests.withoutS.Requester;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.EnumMap;

@Getter
public final class Requesters {

    private final EnumMap<Endpoint, Requester> uncheckedRequests = new EnumMap<>(Endpoint.class);

    public Requesters(RequestSpecification spec) {
        for (var endpoint : Endpoint.values()) {
            uncheckedRequests.put(endpoint, new Requester(spec, endpoint));
        }
    }

    public Requester getRequest(Endpoint endpoint) {
        return uncheckedRequests.get(endpoint);
    }

}
