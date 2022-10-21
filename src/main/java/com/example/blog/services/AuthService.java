package com.example.blog.services;

import com.example.blog.models.User;
import com.example.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class AuthService {

    public void authModelAdvice(Model model, UserRepository userRepository) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != "anonymousUser") {
            User user = userRepository.findByLogin(auth.getName());
            if(user.getAdmin() == true) model.addAttribute("admin", true);
            model.addAttribute("auth", true);
        } else {
            model.addAttribute("auth", false);
        }
    }

}
