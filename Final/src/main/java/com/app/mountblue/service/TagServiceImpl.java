package com.app.mountblue.service;

import com.app.mountblue.repository.TagRepository;
import com.app.mountblue.entities.tags;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<tags> findAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public tags findById(Long theId) {
        Optional<tags> result = tagRepository.findById(theId);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new RuntimeException("Tag ID not found: " + theId);
        }
    }

    @Override
    public tags save(tags theTag) {
        return tagRepository.save(theTag);
    }

    @Override
    public void deleteById(Long theId) {
        tagRepository.deleteById(theId);
    }

    @Override
    public tags findByName(String name) {

        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + name));
    }

    @Override
    public List<tags> findTagsByIds(List<Long> tagIds) {
        return tagRepository.findAllById(tagIds);
    }
}
