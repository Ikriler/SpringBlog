package com.example.blog.controllers;

import com.example.blog.models.User;
import com.example.blog.repositories.ContactsRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    private AuthService authService = new AuthService();


    @PostMapping("/user-create")
    public String create(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                         Model model) {
        authService.authModelAdvice(model, userRepository);
        List<String> loginErrors = new ArrayList<>();

        if(userRepository.findByLogin(user.getLogin()) != null) {
            ObjectError error = new ObjectError("login", "Логин уже существует");
            bindingResult.addError(error);
            loginErrors.add("Логин уже существует");
        }

        if(user.getLogin().trim().length() < 6 || user.getLogin().trim().length() > 20 ) {
            ObjectError error = new ObjectError("login", "Поле логин должно содержать от 6 до 20 символов");
            bindingResult.addError(error);
            loginErrors.add("Поле логин должно содержать от 6 до 20 символов");
        }

        if(loginErrors.size() != 0) {
            model.addAttribute("loginErrors", loginErrors);
        }

        if(bindingResult.hasErrors()) {
            return "register-form";
        }

        User userDB = new User(user.getLogin(), user.getPassword(), user.getAge(), user.getGrowth(), user.getBd_date(), false);

        userRepository.save(userDB);

        return "redirect:/";
    }

    @GetMapping("/user/list")
    public String list(@RequestParam(required = false) String login, @RequestParam(required = false) Boolean accurate, Model model) {
        authService.authModelAdvice(model, userRepository);
        Iterable<User> users = new ArrayList<User>();

        if (login != null && login != "") {
            if (accurate != null && accurate == true) {
                if(userRepository.findByLogin(login) != null)
                users = Arrays.asList(userRepository.findByLogin(login));
            } else {
                users = userRepository.findByLoginContains(login);
            }
        } else {
            users = userRepository.findAll();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != "anonymousUser") {
            model.addAttribute("auth", true);
        } else {
            model.addAttribute("auth", false);
        }


        model.addAttribute("users", users);

        return "users-list";
    }

    @GetMapping("/profile")
    public String profile(@RequestParam long id, Model model) {
        authService.authModelAdvice(model, userRepository);
        if (!userRepository.existsById(id)) {
            return "redirect:/";
        }
        User user = userRepository.findById(id).get();

        model.addAttribute("user", user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != "anonymousUser") {
            User currentUser = userRepository.findByLogin(auth.getName());
            if (currentUser.getId() == user.getId()) {
                model.addAttribute("current_user", true);
            } else {
                model.addAttribute("current_user", false);
            }
            model.addAttribute("auth", true);
        } else {
            model.addAttribute("current_user", false);
            model.addAttribute("auth", false);
        }

        if(user.getContacts() != null) {
            model.addAttribute("contacts", user.getContacts());
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
        authService.authModelAdvice(model, userRepository);
        if (!userRepository.existsById(id)) {
            return "redirect:/";
        }
        User user = userRepository.findById(id).get();

        Date date = user.getBd_date();

        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");

        String outDate = dt1.format(date);

        authService.authModelAdvice(model, userRepository);

        model.addAttribute("user", user);
        model.addAttribute("outDate", outDate);

        return "user-edit";
    }

    @PostMapping("user/edit")
    public String userEdit(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           Model model) {

        authService.authModelAdvice(model, userRepository);

        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");

        if(user.getBd_date() == null) user.setBd_date(new Date());
        String outDate = dt1.format(user.getBd_date());

        if (bindingResult.hasErrors()) {
            model.addAttribute("outDate", outDate);
            return "user-edit";
        }

        User userBD = userRepository.findById(user.getId()).get();

        userBD.setPassword(user.getPassword());
        userBD.setAge(user.getAge());
        userBD.setGrowth(user.getGrowth());
        userBD.setBd_date(user.getBd_date());

        userRepository.save(userBD);

        return "redirect:/profile?id=" + String.valueOf(userBD.getId());
    }

}
