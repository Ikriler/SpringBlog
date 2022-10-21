package com.example.blog.controllers;

import com.example.blog.models.User;
import com.example.blog.repositories.UserRepository;
import com.example.blog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PermissionController {

    @Autowired
    private UserRepository userRepository;

    private AuthService authService = new AuthService();

    @GetMapping("/permission/page")
    public String goPermissionPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        authService.authModelAdvice(model, userRepository);

        List<User> users = userRepository.findByAdmin(false);
        List<User> admins = userRepository.findByAdmin(true);

        User current_user = userRepository.findByLogin(auth.getName());

        admins.remove(current_user);

        model.addAttribute("users", users);
        model.addAttribute("admins", admins);

        return "permission-add";
    }

    @PostMapping("/permission/add")
    public String permissionAdd(@RequestParam long user_id, Model model)  {

        User user = userRepository.findById(user_id).get();

        user.setAdmin(true);

        userRepository.save(user);

        return "redirect:/permission/page";
    }

    @PostMapping("/permission/delete")
    public String permissionDelete(@RequestParam long user_id, Model model)  {

        User user = userRepository.findById(user_id).get();

        user.setAdmin(false);

        userRepository.save(user);

        return "redirect:/permission/page";
    }

}
