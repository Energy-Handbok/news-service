package com.khaphp.newsservice.call;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {
    @GetMapping("/api/v1/user-system/detail")
    public ResponseEntity<Object> getObject(@RequestParam String id);
}
