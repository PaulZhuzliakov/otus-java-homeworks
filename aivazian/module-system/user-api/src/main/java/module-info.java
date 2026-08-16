module user.api {
    requires user.service;
    requires user.starter;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.web;

    requires static lombok;

    opens ru.otus.user.api;
    opens ru.otus.user.api.controller;
    opens ru.otus.user.api.dto;
}