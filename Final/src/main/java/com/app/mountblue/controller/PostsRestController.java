package com.app.mountblue.controller;

import com.app.mountblue.entities.posts;
import com.app.mountblue.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api")
public class PostsRestController {

    private final PostsService postsService;

    @Autowired
    public PostsRestController(PostsService thePostsService) {
        postsService = thePostsService;
    }

    @GetMapping("/posts")
    public Page<posts> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return postsService.findAll(pageable);
    }

    @GetMapping("/posts/{postsId}")
    public posts getPosts(@PathVariable Long postsId) {  // Change 'int' to 'Long'
        posts thePosts = postsService.findById(postsId);

        if (thePosts == null) {
            throw new RuntimeException("Post id not found -- " + postsId);
        }

        return thePosts;
    }

    @PostMapping("/posts")
    public posts addPosts(@RequestBody posts thePosts) {
        thePosts.setId(0L);  // Change 'int' to 'Long'

        if (thePosts.getExcerpt() == null || thePosts.getExcerpt().isEmpty()) {
            thePosts.setExcerpt(thePosts.getContent().length() > 100 ?
                    thePosts.getContent().substring(0, 100) + "..." : thePosts.getContent());
        }
        if (thePosts.getPublishedAt() == null) {
            thePosts.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        }
        if (thePosts.getIsPublished() == null) {
            thePosts.setIsPublished(false);
        }
        thePosts.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        thePosts.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return postsService.save(thePosts);
    }

    @PutMapping("/posts")
    public posts updatePosts(@RequestBody posts thePosts) {
        thePosts.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return postsService.save(thePosts);
    }

    @DeleteMapping("/posts/{postsId}")
    public String deletePosts(@PathVariable Long postsId) {  // Change 'int' to 'Long'
        posts tempPosts = postsService.findById(postsId);

        if (tempPosts == null) {
            throw new RuntimeException("Post id not found -- " + postsId);
        }

        postsService.deleteById(postsId);

        return "Deleted Post id -- " + postsId;
    }
}
