module user.starter {
    requires user.core;
    requires user.provider;
    requires user.service;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;

    opens ru.otus.configuration;
}