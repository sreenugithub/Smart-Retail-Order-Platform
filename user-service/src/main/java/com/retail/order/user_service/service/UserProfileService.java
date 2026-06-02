package com.retail.order.user_service.service;

import com.retail.order.user_service.entity.UserProfile;
import com.retail.order.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfile save(UserProfile profile){
        return repository.save(profile);
    }

    public List<UserProfile> getAllUsers(){
        return repository.findAll();
    }
}