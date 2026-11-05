package org.java.expert.webservicewithlogin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.java.expert.webservicewithlogin.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
