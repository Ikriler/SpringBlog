package com.example.blog.controllers;

import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/profile")
    public String profile(@RequestParam long id, Model model) {
        if(!userRepository.existsById(id)) {
            return "redirect:/";
        }
        User user = userRepository.findById(id).get();

        model.addAttribute("user", user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName() != "anonymousUser") {
            User currentUser = userRepository.findByLogin(auth.getName());
            if(currentUser.getId() == user.getId()) {
                model.addAttribute("current_user", true);
            }
            else {
                model.addAttribute("current_user", false);
            }
        }
        else {
            model.addAttribute("current_user", false);
        }

        return "profile";
    }


    @PostMapping("user/delete")
    public String userDelete(@RequestParam long id, Model model) {
        User user = userRepository.findById(id).get();

        userRepository.delete(user);

        return "redirect:/logout";
    }

    @PostMapping("user/edit-page")
    public String goEditPage(@RequestParam long id, Model model) {
        if(!userRepository.existsById(id)) {
            return "redirect:/";
        }
        User user = userRepository.findById(id).get();

        Date date = user.getBd_date();

        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");

        String outDate = dt1.format(date);

        model.addAttribute("user", user);
        model.addAttribute("outDate", outDate);

        return "user-edit";
    }

    @PostMapping("user/edit")
    public String userEdit(@RequestParam long id,
                           @RequestParam String password,
                           @RequestParam int age,
                           @RequestParam Double growth,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date bd_date,
                           Model model) {

        User user = userRepository.findById(id).get();

        user.setPassword(password);
        user.setAge(age);
        user.setGrowth(growth);
        user.setBd_date(bd_date);

        userRepository.save(user);

        return "redirect:/profile?id="+String.valueOf(id);
    }

}
