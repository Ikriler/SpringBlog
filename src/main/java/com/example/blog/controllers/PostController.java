package com.example.blog.controllers;

import com.example.blog.models.Comment;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.CommentRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/post-create")
    public String postCreate(@RequestParam String title,
                             @RequestParam String anons,
                             @RequestParam String full_text, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByLogin(auth.getName());

        Post post = new Post(title, anons, full_text);

        postRepository.save(post);

        user.getPostList().add(post);

        userRepository.save(user);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String getOnePost(@RequestParam(required = false) String text, @RequestParam(required = false) Boolean accurate, @RequestParam long id, Model model) {
        Optional<Post> post = postRepository.findById(id);

        List<Comment> commentList = post.get().getCommentList();

        Collections.reverse(commentList);

        if(text != null && text != "") {
            if(accurate != null && accurate == true) {
                commentList = commentList.stream().filter(c -> text.trim().equals(c.getText().trim())).collect(Collectors.toList());
            }
            else {
                commentList = commentList.stream().filter(c -> c.getText().contains(text)).collect(Collectors.toList());
            }
        }

        model.addAttribute("comments", commentList);

        model.addAttribute("post", post.get());
        return "one-post";
    }
}
