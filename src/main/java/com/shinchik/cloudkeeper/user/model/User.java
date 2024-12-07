package com.shinchik.cloudkeeper.user.model;


import com.shinchik.cloudkeeper.user.util.PasswordConstraint;
import com.shinchik.cloudkeeper.user.util.UsernameConstraint;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
    @SequenceGenerator(name = "seqGen", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @UsernameConstraint
    private String username;

    @NotNull
    @PasswordConstraint
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    Role role = Role.USER;

    public boolean passwordsMatch(String passConfirmation){
        return password.equals(passConfirmation);
    }


}
