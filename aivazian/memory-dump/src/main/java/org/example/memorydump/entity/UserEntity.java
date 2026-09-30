package org.example.memorydump.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table("USERS")
public class UserEntity {
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column
    private String login;

    @Column
    private String encodedPassword;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email;
}
