module org.java.expert.modulesystem.core {
    requires jakarta.persistence;
    requires spring.data.jpa;
    requires static lombok;

    exports org.java.expert.modulesystem.core.model;
    exports org.java.expert.modulesystem.core.repository;
}
