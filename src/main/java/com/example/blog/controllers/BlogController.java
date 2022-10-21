package com.example.blog.controllers;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.Date;


@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;


    private AuthService authService = new AuthService();

    @GetMapping("/")
    public String index(@RequestParam(required = false) String title, @RequestParam(required = false) Boolean accurate, Model model) {
        authService.authModelAdvice(model, userRepository);

        Iterable<Post> posts = new ArrayList<Post>();

        if (title != null && title != "") {
            if (accurate != null && accurate == true) {
                posts = postRepository.findByTitle(title);
            } else {
                posts = postRepository.findByTitleContains(title);
            }
        } else {
            posts = postRepository.findAll();
        }
        model.addAttribute("posts", posts);

        return "main";
    }

    @GetMapping("/register-form")
    public String registerForm(Model model) {
        authService.authModelAdvice(model, userRepository);
        User user = new User();
        user.setBd_date(new Date());
        user.setGrowth(170.0);
        model.addAttribute("user", user);
        return "register-form";
    }



}
