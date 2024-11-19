package com.shinchik.cloudkeeper.user.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")

// TODO: fix redis inserting username
public class User implements Serializable {
    // TODO: validation patterns
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 4, max = 30, message = "Username length should be between {min} and {max} characters")
    private String username;

    @NotNull
    @Size(min = 3, message = "Password length should be at least {min} characters")
    private String password;

    // TODO: implement via roles enum
    @NotNull
    private String roles = "USER";


}
