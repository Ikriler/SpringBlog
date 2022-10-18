package com.example.blog.controllers;

import com.example.blog.models.Comment;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.CommentRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/post-create")
    public String postCreate(@ModelAttribute("post") @Valid Post post, BindingResult bindingResult, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByLogin(auth.getName());

        if (auth != null && auth.getName() != "anonymousUser") {
            model.addAttribute("auth", true);
        } else {
            model.addAttribute("auth", false);
        }


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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth != null && auth.getName() != "anonymousUser") {
                model.addAttribute("auth", true);
            }
            else {
                model.addAttribute("auth", false);
            }
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
