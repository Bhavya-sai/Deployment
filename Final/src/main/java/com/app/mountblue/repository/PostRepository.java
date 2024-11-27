package com.app.mountblue.repository;

import com.app.mountblue.entities.posts;
import com.app.mountblue.entities.tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface PostRepository extends JpaRepository<posts, Long> {


    Page<posts> findByIsPublishedTrue(Pageable pageable);


    Page<posts> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<posts> findByAuthorContainingIgnoreCase(String keyword, Pageable pageable);
    Page<posts> findByContentContainingIgnoreCase(String keyword, Pageable pageable);


    Page<posts> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String titleKeyword, String contentKeyword, String authorKeyword, Pageable pageable);

    Page<posts> findByAuthorIgnoreCase(String author, Pageable pageable);

    Page<posts> findByTagsIn(List<tags> tags, Pageable pageable);

    Page<posts> findByTagsContaining(tags tag, Pageable pageable);

    Page<posts> findByCreatedAtBetween(Timestamp startDate, Timestamp endDate, Pageable pageable);

    @Query("SELECT p FROM posts p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<posts> findPostsByDateRange(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, Pageable pageable);

    Page<posts> findByIsPublishedFalse(Pageable pageable);

    @Query("SELECT p FROM posts p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR " +
            "LOWER(p.author) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))")
    Page<posts> searchByTitleOrContentOrAuthor(@Param("searchKeyword") String searchKeyword, Pageable pageable);
}
