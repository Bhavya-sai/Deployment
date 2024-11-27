package com.app.mountblue.controller;

import com.app.mountblue.entities.posts;
import com.app.mountblue.entities.comment;
import com.app.mountblue.entities.tags;
import com.app.mountblue.service.PostsService;
import com.app.mountblue.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class PostFormController {

    private final PostsService postsService;
    private final TagService tagsService;

    @Autowired
    public PostFormController(PostsService postsService, TagService tagsService) {
        this.postsService = postsService;
        this.tagsService = tagsService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/create-post")
    public String showCreatePostForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("post", new posts());
        model.addAttribute("tags", tagsService.findAllTags());
        return "create-post";
    }

    @PostMapping("/create-post")
    public String savePost(posts post, String publish, @RequestParam List<Long> tagIds) {
        if (post.getExcerpt() == null || post.getExcerpt().trim().isEmpty()) {
            post.setExcerpt("No excerpt provided");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        post.setAuthor(authentication.getName());
        if ("true".equals(publish)) {
            post.setPublishedAt(new Timestamp(System.currentTimeMillis()));
            post.setIsPublished(true);
        }

        List<tags> selectedTags = tagsService.findTagsByIds(tagIds);
        post.setTags(selectedTags);

        postsService.save(post);
        return "redirect:/available-posts";
    }

    @GetMapping("/available-posts")
    public String getAvailablePosts(Model model,
                                    @RequestParam(value = "search", required = false) String searchKeyword,
                                    @RequestParam(value = "filter", required = false) String filter,
                                    @RequestParam(value = "filterValue", required = false) String filterValue,
                                    @RequestParam(value = "sortBy", required = false) String sortBy,
                                    Pageable pageable) {

        Page<posts> postsPage = postsService.getPosts(searchKeyword, filter, filterValue,sortBy, pageable);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("posts", postsPage.getContent());
        model.addAttribute("postsPage", postsPage);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("selectedFilter", filter);
        model.addAttribute("filterValue",filterValue);
        model.addAttribute("sortBy", sortBy);

        return "availableposts";
    }

    @GetMapping("/posts/{id}")
    public String getPostDetails(@PathVariable Long id, Model model) {
        posts post = postsService.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<comment> comments = postsService.findCommentsByPostId(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        System.out.printf(isAdmin ? "YES" : "NO");

        model.addAttribute("role",isAdmin ? "ROLE_ADMIN" : "");
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("user",authentication.getName().equals("AnonymousUser") ? null :authentication.getName());

        return "postDetails";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id, String name, String commentText) {
        if (name == null || name.trim().isEmpty() || commentText == null || commentText.trim().isEmpty()) {
            return "redirect:/posts/{id}";
        }

        posts post = postsService.findById(id);
        comment newComment = new comment();
        newComment.setPost(post);
        newComment.setName(name);
        newComment.setComment(commentText);
        postsService.saveComment(newComment);

        return "redirect:/posts/{id}";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postsService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Your post has been deleted successfully!");
        return "redirect:/available-posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model model) {
        posts post = postsService.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("tags", tagsService.findAllTags());
        return "create-post";
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id, posts post, String publish) {
        posts existingPost = postsService.findById(id);

        if (existingPost != null) {
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            existingPost.setExcerpt(post.getExcerpt() != null ? post.getExcerpt() : "No excerpt provided");

            if ("true".equals(publish)) {
                existingPost.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                existingPost.setIsPublished(true);
            } else {
                existingPost.setIsPublished(false);
            }

            postsService.save(existingPost);
        }
        return "redirect:/available-posts";
    }
}
