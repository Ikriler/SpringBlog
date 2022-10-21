package com.example.blog.controllers;

import com.example.blog.models.Contacts;
import com.example.blog.models.User;
import com.example.blog.repositories.ContactsRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;

@Controller
public class ContactsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    private AuthService authService = new AuthService();

    @PostMapping("/contacts/goAddPage")
    public String goAddPage(@RequestParam long id, Model model) {
        authService.authModelAdvice(model, userRepository);
        User user = userRepository.findById(id).get();
        Contacts contacts = new Contacts();
        contacts.setUser(user);
        model.addAttribute("contacts", contacts);
        return "contacts-add";
    }


    @PostMapping("/contacts/create")
    public String createContacts(@ModelAttribute("contacts") @Valid Contacts contacts, BindingResult bindingResult, Model model) {

        authService.authModelAdvice(model, userRepository);

        Boolean haveErrors = false;

        if(contactsRepository.findByEmail(contacts.getEmail()) != null && !contactsRepository.findByEmail(contacts.getEmail()).getId().equals(contacts.getId())){
            model.addAttribute("email_errors", "Данный email уже существует");
            haveErrors = true;
        }


        if(contactsRepository.findByPhoneNumber(contacts.getPhoneNumber()) != null && !contactsRepository.findByPhoneNumber(contacts.getPhoneNumber()).getId().equals(contacts.getId())){
            model.addAttribute("phoneNumber_errors", "Данный телефон уже существует");
            haveErrors = true;
        }

        if(bindingResult.hasErrors() || haveErrors) {
            return "contacts-add";
        }

        contactsRepository.save(contacts);

        contacts.getUser().setContacts(contacts);

        userRepository.save(contacts.getUser());

        return "redirect:/profile?id=" + contacts.getUser().getId();
    }

    @PostMapping("/contacts/delete")
    public String deleteContacts(@RequestParam long id, Model model) {

        User user = userRepository.findById(id).get();

        Contacts contacts = user.getContacts();

        user.setContacts(null);

        userRepository.save(user);

        contactsRepository.delete(contacts);

        return "redirect:/profile?id=" + contacts.getUser().getId();
    }

    @PostMapping("/contacts/edit-page")
    public String goContactsEditPage(@RequestParam long id, Model model)  {

        authService.authModelAdvice(model, userRepository);

        User user = userRepository.findById(id).get();

        model.addAttribute("contacts", user.getContacts());

        return "contacts-edit";
    }

    @PostMapping("/contacts/edit")
    public String contactsEdit(@ModelAttribute("contacts") @Valid Contacts contacts, BindingResult bindingResult, Model model) {
        authService.authModelAdvice(model, userRepository);

        Boolean haveErrors = false;

        if(contactsRepository.findByEmail(contacts.getEmail()) != null && !contactsRepository.findByEmail(contacts.getEmail()).getId().equals(contacts.getId())){
            model.addAttribute("email_errors", "Данный email уже существует");
            haveErrors = true;
        }


        if(contactsRepository.findByPhoneNumber(contacts.getPhoneNumber()) != null && !contactsRepository.findByPhoneNumber(contacts.getPhoneNumber()).getId().equals(contacts.getId())){
            model.addAttribute("phoneNumber_errors", "Данный телефон уже существует");
            haveErrors = true;
        }

        if(bindingResult.hasErrors() || haveErrors) {
            return "contacts-edit";
        }

        contactsRepository.save(contacts);

        contacts.getUser().setContacts(contacts);

        userRepository.save(contacts.getUser());

        return "redirect:/profile?id=" + contacts.getUser().getId();
    }
}
