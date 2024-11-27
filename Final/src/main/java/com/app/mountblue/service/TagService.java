package com.app.mountblue.service;

import com.app.mountblue.entities.tags;

import java.util.List;

public interface TagService {

    List<tags> findAllTags();

    tags findById(Long theId);

    tags save(tags theTag);

    void deleteById(Long theId);

    List<tags> findTagsByIds(List<Long> tagIds);

    tags findByName(String name);
}
