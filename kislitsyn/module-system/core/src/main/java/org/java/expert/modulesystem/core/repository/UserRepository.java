package org.java.expert.modulesystem.core.repository;

import org.java.expert.modulesystem.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
