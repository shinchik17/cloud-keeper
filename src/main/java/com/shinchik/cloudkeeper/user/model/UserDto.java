package com.shinchik.cloudkeeper.user.model;


import com.shinchik.cloudkeeper.user.util.PasswordConstraint;
import com.shinchik.cloudkeeper.user.util.UsernameConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    @UsernameConstraint
    private String username;

    @PasswordConstraint
    private String password;

    @PasswordConstraint
    private String passwordConfirmation;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

}
