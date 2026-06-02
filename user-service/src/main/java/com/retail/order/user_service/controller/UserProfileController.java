package com.retail.order.user_service.controller;

import com.retail.order.user_service.entity.UserProfile;
import com.retail.order.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    @PostMapping
    public UserProfile create(
            @RequestBody UserProfile profile){

        return service.save(profile);
    }

    @GetMapping
    public List<UserProfile> getAll(){

        return service.getAllUsers();
    }


//    @GetMapping("/admin")
//    public String admin() {
//        return "Admin Endpoint";
//    }

    @GetMapping("/test")
    public String test(@RequestHeader(value = "X-Correlation-Id", required = false)
                       String correlationId) {
        MDC.put( "correlationId", correlationId );
        log.info(  "User Service Request Received"  );

        return "User Service Working";
    }
    @GetMapping("/admin")
    public String admin(@RequestHeader(value = "X-Correlation-Id", required = false)
                        String correlationId) {

         MDC.put( "correlationId", correlationId );
        log.info(  "User Service Request Received"  );
        return "Admin Endpoint";
    }
}