package ru.otus.user.provider.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UserEntity {
    private final Long id;
    private final String name;
    private final String lastname;
}
