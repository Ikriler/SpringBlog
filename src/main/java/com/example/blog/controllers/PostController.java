package com.example.blog.controllers;

import com.example.blog.models.Comment;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.CommentRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private AuthService authService = new AuthService();

    @PostMapping("/post-create")
    public String postCreate(@ModelAttribute("post") @Valid Post post, BindingResult bindingResult, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByLogin(auth.getName());

        authService.authModelAdvice(model, userRepository);

        if(bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post-add-form";
        }

        Post postDB = new Post(post.getTitle(), post.getAnons(), post.getFull_text(), user);

        postRepository.save(postDB);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String getOnePost(@RequestParam(required = false) String text, @RequestParam(required = false) Boolean accurate, @RequestParam long id, Model model, HttpSession session) {
        Post post = postRepository.findById(id).get();
        authService.authModelAdvice(model, userRepository);
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

        if(post.getUserEditorsList().contains(user)) {
            model.addAttribute("allowEdit", true);
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

        if(session.getAttribute("comment") != null && session.getAttribute("flash") != null) {
            model.addAttribute("comment", session.getAttribute("comment"));
            model.addAttribute("org.springframework.validation.BindingResult.comment",session.getAttribute("org.springframework.validation.BindingResult.comment"));
            session.removeAttribute("flash");
        }
        else {
            session.removeAttribute("comment");
            session.removeAttribute("org.springframework.validation.BindingResult.comment");
            model.addAttribute("comment", new Comment());
        }

        return "one-post";
    }

    @PostMapping("/post/edit")
    public String editPost(@ModelAttribute("post") @Valid Post post, BindingResult bindingResult, Model model) {


        if(bindingResult.hasErrors()) {
            authService.authModelAdvice(model, userRepository);
            return "post-edit";
        }


        Post postDB = postRepository.findById(post.getId()).get();

        postDB.setTitle(post.getTitle());
        postDB.setAnons(post.getAnons());
        postDB.setFull_text(post.getFull_text());

        postRepository.save(postDB);


        return "redirect:/list?id="+String.valueOf(postDB.getId());
    }

    @PostMapping("/post/page-edit")
    public String goEditPost(@RequestParam long id,
                           Model model) {

        authService.authModelAdvice(model, userRepository);

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

    @GetMapping("/post/goEditorPage")
    public String goEditorPage(@RequestParam long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        authService.authModelAdvice(model, userRepository);

        if(!postRepository.existsById(id) || !postRepository.findById(id).get().getUser().getLogin().equals(auth.getName())) {
            return "redirect:/login";
        }

        Post post = postRepository.findById(id).get();

        User currentUser = userRepository.findByLogin(auth.getName());

        List<User> editors = post.getUserEditorsList();
        List<User> users = (List<User>) userRepository.findAll();

        users.removeAll(editors);
        users.remove(currentUser);

        model.addAttribute("users", users);
        model.addAttribute("editors", editors);
        model.addAttribute("post_id", id);

        return "editor-add-page";
    }

    @PostMapping("/post/addEditor")
    public String addEditor(@RequestParam long post_id, @RequestParam long user_id, Model model) {

        User user = userRepository.findById(user_id).get();
        Post post = postRepository.findById(post_id).get();

        post.getUserEditorsList().add(user);

        postRepository.save(post);

        return "redirect:/post/goEditorPage?id=" + post_id;
    }

    @PostMapping("/post/deleteEditor")
    public String deleteEditor(@RequestParam long post_id, @RequestParam long user_id, Model model, HttpSession session) {

        User user = userRepository.findById(user_id).get();
        Post post = postRepository.findById(post_id).get();

        post.getUserEditorsList().remove(user);

        postRepository.save(post);

        return "redirect:/post/goEditorPage?id=" + post_id;
    }

    @GetMapping("/add-post")
    public String goPostAddForm(Model model) {
        authService.authModelAdvice(model, userRepository);
        model.addAttribute("post", new Post());
        return "post-add-form";
    }

}
