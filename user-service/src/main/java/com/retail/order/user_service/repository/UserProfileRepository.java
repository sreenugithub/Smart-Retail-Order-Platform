package com.retail.order.user_service.repository;

import com.retail.order.user_service.entity.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository
        extends MongoRepository<UserProfile,String> {

}