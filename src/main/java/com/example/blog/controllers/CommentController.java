package com.example.blog.controllers;

import com.example.blog.models.Comment;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.CommentRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @PostMapping("/create-comment")
    public String createComment(@RequestParam long post_id, @RequestParam String text, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByLogin(authentication.getName());

        Post post = postRepository.findById(post_id).get();

        Comment comment = new Comment(text, new Date(), false, 0, 0.0);

        commentRepository.save(comment);

        user.getCommentList().add(comment);

        userRepository.save(user);

        post.getCommentList().add(comment);

        postRepository.save(post);

        return "redirect:/list?id="+String.valueOf(post_id);
    }
}
