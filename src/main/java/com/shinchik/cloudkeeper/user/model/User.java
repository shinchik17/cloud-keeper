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
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
//    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq")
//    @SequenceGenerator(name = "seqGen")
    private Long id;

    @NotNull
    @Size(min = 4, max = 30, message = "Username length should be between {min} and {max} characters")
    private String username;

    @NotNull
    @Size(min = 3, message = "Password length should be at least {min} characters")
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    Role role = Role.USER;


}
