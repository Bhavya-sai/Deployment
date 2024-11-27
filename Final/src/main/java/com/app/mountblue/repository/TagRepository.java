package com.app.mountblue.repository;

import com.app.mountblue.entities.tags;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface TagRepository extends JpaRepository<tags, Long>
{
    Optional<tags> findByName(String name);
}
