package com.app.mountblue.repository;

import com.app.mountblue.entities.comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<comment, Long> {

    List<comment> findByPostId(Long postId);
}
