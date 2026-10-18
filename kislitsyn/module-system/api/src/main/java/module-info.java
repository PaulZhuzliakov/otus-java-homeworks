module org.java.expert.modulesystem.api {
    requires jakarta.validation;
    requires org.java.expert.modulesystem.core;
    requires org.java.expert.modulesystem.service;
    requires org.hibernate.validator;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires tools.jackson.databind;

    opens org.java.expert.modulesystem.api.controller to spring.web;
    opens org.java.expert.modulesystem.api.dto to
            org.hibernate.validator,
            tools.jackson.databind;
}
