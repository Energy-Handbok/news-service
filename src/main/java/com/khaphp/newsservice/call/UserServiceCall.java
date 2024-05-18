package com.khaphp.newsservice.call;

import com.khaphp.common.entity.UserSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceCall {
    public static final String CALL_OTHER_SERVICE_ERROR = "Call other service error: {}";
    public static final String CALL_OTHER_SERVICE = "Call other service";
    private final UserServiceFeignClient userServiceFeignClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    public UserSystem getObject(String id) {
        UserSystem userSystem = (UserSystem) circuitBreakerFactory.create("getdetail").run(
                () -> {
                    log.info("[getObject]" + CALL_OTHER_SERVICE);
                    ResponseEntity<?> responseEntity = userServiceFeignClient.getObject(id);
                    log.info("response: " + responseEntity);
                    LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseEntity.getBody();
                    UserSystem user = null;
                    try {
                        user = UserSystem.linkedHashMapToEntity((LinkedHashMap<String, Object>) data.get("data"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    return user;
                },
                throwable -> {
                    log.error("[getObject]" + CALL_OTHER_SERVICE_ERROR, throwable.getMessage());
                    return null;
                });
        return userSystem;
    }
}
