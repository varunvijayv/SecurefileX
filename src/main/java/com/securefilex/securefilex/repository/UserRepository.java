package com.securefilex.securefilex.repository;

import org.springframework.data.repository.CrudRepository;
import com.securefilex.securefilex.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
