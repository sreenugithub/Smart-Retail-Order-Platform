package com.retail.order.auth_service.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter

@Setter

@NoArgsConstructor

@AllArgsConstructor

@Builder

@Document(collection = "users")
public class User {

    @Id

    private String id;

    private String username;

    private String email;

    private String password;

    private String role;

}