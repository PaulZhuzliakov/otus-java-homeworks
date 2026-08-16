package org.java.expert.emulatorforfilecache;

import org.java.expert.Cache;
import org.java.expert.CacheImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCacheConfiguration {

    @Bean
    public Cache<String, String> fileContentCache(
            @Value("${file-cache.capacity:50}") int capacity
    ) {
        return new CacheImpl<>(capacity);
    }
}
