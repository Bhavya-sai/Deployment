package com.app.mountblue.repository;

import com.app.mountblue.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{

    Optional<User> findByEmail(String email);

    Optional<User> findByfullName(String full_name);


}