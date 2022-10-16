package com.example.blog.controllers;

import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;


    @PostMapping("/user-create")
    public String create(@RequestParam String login,
                         @RequestParam String password,
                         @RequestParam int age,
                         @RequestParam Double growth,
                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date bd_date,
                         Model model) {
        User user = new User(login, password, age, growth, bd_date, false);

        userRepository.save(user);

        return "/";
    }

    @GetMapping("/user/list")
    public String list(@RequestParam(required = false) String login, @RequestParam(required = false) Boolean accurate, Model model) {

        Iterable<User> users = new ArrayList<User>();

        if(login != null && login != "") {
            if(accurate != null && accurate == true) {
                ((ArrayList<User>) users).add(userRepository.findByLogin(login));
            }
            else {
                users = userRepository.findByLoginContains(login);
            }
        }
        else {
            users = userRepository.findAll();
        }

        model.addAttribute("users", users);

        return "users-list";
    }
}
