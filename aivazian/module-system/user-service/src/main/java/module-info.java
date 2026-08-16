module user.service {
    requires user.provider;
    requires static lombok;

    exports ru.otus.user.service.api;
    exports ru.otus.user.service.impl;
    exports ru.otus.user.service.model;
    exports ru.otus.user.service.exception;
}