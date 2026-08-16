package ru.otus.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.user.core.api.DataStore;
import ru.otus.user.core.impl.InMemoryDataStore;
import ru.otus.user.provider.api.UserProvider;
import ru.otus.user.provider.entity.UserEntity;
import ru.otus.user.provider.impl.UserProviderImpl;
import ru.otus.user.service.api.UserService;
import ru.otus.user.service.impl.UserServiceImpl;

@Configuration
public class UserConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DataStore<UserEntity, Long> dataStore() {
        return new InMemoryDataStore<>();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserProvider userProvider(DataStore<UserEntity, Long> dataStore) {
        return new UserProviderImpl(dataStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserService userService(UserProvider userProvider) {
        return new UserServiceImpl(userProvider);
    }
}
