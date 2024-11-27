package com.app.mountblue.service;

import com.app.mountblue.entities.posts;
import com.app.mountblue.entities.comment;
import com.app.mountblue.entities.tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostsService {

    Page<posts> findAll(Pageable pageable);

    Page<posts> getPosts(String searchKeyword, String filter,String filterValue, String sortBy, Pageable pageable);

    posts findById(Long theId);

    posts save(posts thePosts);

    void deleteById(Long theId);

    Page<posts> findPublishedPosts(Pageable pageable);

    List<comment> findCommentsByPostId(Long postId);

    void saveComment(comment comment);

    Page<posts> searchByTitleOrContentOrAuthor(String keyword, Pageable pageable);

    List<tags> findTagsByPostId(Long postId);

    void addTagsToPost(Long postId, List<tags> tags);

    void removeTagsFromPost(Long postId, List<tags> tags);

    List<tags> findAllTags();

    Page<posts> findPostsByAuthor(String author, Pageable pageable);

    Page<posts> findPostsByTag(String tagName, Pageable pageable);

    Page<posts> findPostsByDate(String date, Pageable pageable);

    Page<posts> getPaginatedPosts(Pageable pageable);
}
