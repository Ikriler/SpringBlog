package com.example.blog.controllers;

import com.example.blog.models.Post;
import com.example.blog.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String title, @RequestParam(required = false) Boolean accurate, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getName() != "anonymousUser") {
            model.addAttribute("auth", true);
        }
        else {
            model.addAttribute("auth", false);
        }

        Iterable<Post> posts = new ArrayList<Post>();

        if(title != null && title != "") {
            if(accurate != null && accurate == true) {
                posts = postRepository.findByTitle(title);
            }
            else {
                posts = postRepository.findByTitleContains(title);
            }
        }
        else {
            posts = postRepository.findAll();
        }
        model.addAttribute("posts", posts);
        return "main";
    }

    @GetMapping("/register-form")
    public String registerForm(Model model) { return "register-form";}

    @GetMapping("/add-post")
    public String goPostAddForm(Model model) {return "post-add-form";}

}
