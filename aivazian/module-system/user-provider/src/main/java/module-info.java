module user.provider {
    requires user.core;
    requires static lombok;

    exports ru.otus.user.provider.api;
    exports ru.otus.user.provider.impl;
    exports ru.otus.user.provider.entity;
}