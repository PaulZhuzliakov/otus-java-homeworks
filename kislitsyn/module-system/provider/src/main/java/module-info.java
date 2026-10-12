module org.java.expert.modulesystem.provider {
    requires org.apache.commons.logging;
    requires spring.core;
    requires spring.security.crypto;

    exports org.java.expert.modulesystem.provider.password;
}
