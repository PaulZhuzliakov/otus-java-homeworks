module org.java.expert.modulesystem.service {
    requires org.java.expert.modulesystem.core;
    requires org.java.expert.modulesystem.provider;
    requires spring.context;
    requires spring.tx;
    requires spring.web;

    exports org.java.expert.modulesystem.service;
}
