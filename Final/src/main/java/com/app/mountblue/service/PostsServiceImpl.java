package com.app.mountblue.service;

import com.app.mountblue.repository.PostRepository;
import com.app.mountblue.repository.TagRepository;
import com.app.mountblue.repository.CommentRepository;
import com.app.mountblue.entities.posts;
import com.app.mountblue.entities.comment;
import com.app.mountblue.entities.tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PostsServiceImpl implements PostsService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    public PostsServiceImpl(PostRepository postRepository, CommentRepository commentRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<posts> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<posts> getPosts(String searchKeyword, String filter, String filterValue, String sortBy, Pageable pageable) {

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            return postRepository.searchByTitleOrContentOrAuthor(searchKeyword, pageable);
        }

        if ("published".equalsIgnoreCase(filter)) {
            return postRepository.findByIsPublishedTrue(pageable);
        } else if ("draft".equalsIgnoreCase(filter)) {
            return postRepository.findByIsPublishedFalse(pageable);
        } else if ("author".equalsIgnoreCase(filter) && filterValue != null) {
            return postRepository.findByAuthorIgnoreCase(filterValue, pageable);
        } else if ("tags".equalsIgnoreCase(filter) && filterValue != null)
        {
            Optional<tags> tagOptional = tagRepository.findByName(filterValue);
            if (tagOptional.isPresent()) {
                tags tag = tagOptional.get();  // Extract the tag from Optional
                return postRepository.findByTagsIn(List.of(tag), pageable);
            }
            else {

                return Page.empty();
            }

        } else if ("date".equalsIgnoreCase(filter) && filterValue != null) {
            // Assuming filterValue is in "yyyy-MM-dd" format
            LocalDate localDate = LocalDate.parse(filterValue);
            Timestamp startDate = Timestamp.valueOf(localDate.atStartOfDay());
            Timestamp endDate = Timestamp.valueOf(localDate.plusDays(1).atStartOfDay());
            return postRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        }

        if (sortBy != null) {
            if ("newest".equalsIgnoreCase(sortBy)) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
            } else if ("oldest".equalsIgnoreCase(sortBy)) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("createdAt")));
            }
        }

        return postRepository.findAll(pageable);
    }

    @Override
    public posts findById(Long theId) {
        Optional<posts> result = postRepository.findById(theId);
        return result.orElseThrow(() -> new RuntimeException("Did not find post id -- " + theId));
    }

    @Override
    public posts save(posts thePosts) {
        if (thePosts.getCreatedAt() == null) {
            thePosts.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        if (thePosts.getUpdatedAt() == null) {
            thePosts.setUpdatedAt(thePosts.getCreatedAt());
        }

        if (thePosts.getExcerpt() == null || thePosts.getExcerpt().trim().isEmpty()) {
            thePosts.setExcerpt("No excerpt provided");
        }

        return postRepository.save(thePosts);
    }

    @Override
    public void deleteById(Long theId) {
        postRepository.deleteById(theId);
    }

    @Override
    public Page<posts> findPublishedPosts(Pageable pageable) {
        return postRepository.findByIsPublishedTrue(pageable);
    }

    @Override
    public List<comment> findCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void saveComment(comment newComment) {
        commentRepository.save(newComment);
    }


    @Override
    public Page<posts> searchByTitleOrContentOrAuthor(String keyword, Pageable pageable) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                keyword, keyword, keyword, pageable);
    }

    @Override
    public Page<posts> getPaginatedPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void addTagsToPost(Long postId, List<tags> tags) {
        posts post = findById(postId);
        post.setTags(tags);
        postRepository.save(post);
    }

    @Override
    public List<tags> findTagsByPostId(Long postId) {
        posts post = findById(postId);
        return post.getTags();
    }

    @Override
    public List<tags> findAllTags() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional
    public void removeTagsFromPost(Long postId, List<tags> tags) {
        posts post = findById(postId);
        post.getTags().removeAll(tags);
        postRepository.save(post);
    }

    @Override
    public Page<posts> findPostsByAuthor(String author, Pageable pageable) {
        return postRepository.findByAuthorIgnoreCase(author, pageable);
    }

    @Override
    public Page<posts> findPostsByTag(String tagName, Pageable pageable) {
        Optional<tags> tagOptional = tagRepository.findByName(tagName);
        if (tagOptional.isPresent()) {
            tags tag = tagOptional.get();
            return postRepository.findByTagsContaining(tag, pageable);
        } else {
            return Page.empty();
        }
    }


    @Override
    public Page<posts> findPostsByDate(String date, Pageable pageable) {
        Timestamp startDate = Timestamp.valueOf(date + " 00:00:00");
        Timestamp endDate = Timestamp.valueOf(date + " 23:59:59");
        return postRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }
}
