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

        Post post = new Post(title, anons, full_text, user);

        postRepository.save(post);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String getOnePost(@RequestParam(required = false) String text, @RequestParam(required = false) Boolean accurate, @RequestParam long id, Model model) {
        Post post = postRepository.findById(id).get();

        User user = new User();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getName() != "anonymousUser") {
            user = userRepository.findByLogin(auth.getName());
            model.addAttribute("auth", true);
        }
        else {
            user.setId(-1);
            model.addAttribute("auth", false);
        }

        if(post.getUser().equals(user)) {
            model.addAttribute("allowEdit", true);
            model.addAttribute("allowDelete", true);
        }
        else {
            model.addAttribute("allowEdit", false);
            model.addAttribute("allowDelete", false);
        }

        List<Comment> commentList = commentRepository.findCommentByPost(post);

        Collections.reverse(commentList);

        if(text != null && text != "") {
            if(accurate != null && accurate == true) {
                commentList = commentRepository.findCommentByPostAndTextOrderByDateTimeDesc(post, text.trim());
            }
            else {
                //commentList = commentList.stream().filter(c -> c.getText().contains(text)).collect(Collectors.toList());
                commentList = commentRepository.findCommentByPostAndTextContainsOrderByDateTimeDesc(post, text.trim());
            }
        }

        model.addAttribute("comments", commentList);

        model.addAttribute("post", post);

        model.addAttribute("currentUser", user);
        return "one-post";
    }

    @PostMapping("/post/edit")
    public String editPost(@RequestParam String title,
                           @RequestParam String anons,
                           @RequestParam String full_text,
                           @RequestParam long id,
                           Model model) {

        Post post = postRepository.findById(id).get();

        post.setTitle(title);
        post.setAnons(anons);
        post.setFull_text(full_text);

        postRepository.save(post);

        return "redirect:/list?id="+String.valueOf(id);
    }

    @PostMapping("/post/page-edit")
    public String goEditPost(@RequestParam long id,
                           Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getName() != "anonymousUser") {
            model.addAttribute("auth", true);
        }
        else {
            model.addAttribute("auth", false);
        }

        Post post = postRepository.findById(id).get();

        model.addAttribute("post", post);

        return "post-edit";
    }

    @PostMapping("/post/delete")
    public String deletePost(@RequestParam long id, Model model){

        Post post = postRepository.findById(id).get();

        postRepository.delete(post);

        return "redirect:/";
    }

}
