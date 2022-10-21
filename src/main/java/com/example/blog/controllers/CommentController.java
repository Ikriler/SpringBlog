package com.example.blog.controllers;

import com.example.blog.models.Comment;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.CommentRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;

@Controller
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private AuthService authService = new AuthService();

    @PostMapping("/create-comment")
    public String createComment(@ModelAttribute("comment") @Valid Comment comment, BindingResult bindingResult, @RequestParam long post_id, Model model, HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            return "redirect:/login";
        }

        if(bindingResult.hasErrors()) {
            session.setAttribute("org.springframework.validation.BindingResult.comment", bindingResult);
            session.setAttribute("comment", comment);
            session.setAttribute("flash", true);
            return "redirect:/list?id="+String.valueOf(post_id);
        }

        User user = userRepository.findByLogin(authentication.getName());

        Post post = postRepository.findById(post_id).get();

        Comment commentDB = new Comment(comment.getText().trim(), new Date(), false, 0, 0.0, user, post);

        commentRepository.save(commentDB);

        return "redirect:/list?id="+String.valueOf(post_id);
    }

    @PostMapping("comment/delete")
    public String commentDelete(@RequestParam long post_id,
                                @RequestParam long comment_id,
                                Model model) {

        Comment comment = commentRepository.findById(comment_id).get();

        commentRepository.delete(comment);

        return "redirect:/list?id="+post_id;
    }


    @PostMapping("comment/page-edit")
    public String goCommentEdit(@RequestParam long comment_id,
                                Model model) {

        authService.authModelAdvice(model, userRepository);

        Comment comment = commentRepository.findById(comment_id).get();

        model.addAttribute("comment", comment);

        return "comment-edit";
    }

    @PostMapping("/comment/edit")
    public String commentEdit(@ModelAttribute("comment") @Valid Comment comment, BindingResult bindingResult,
                              Model model) {

        if(bindingResult.hasErrors()) {
            authService.authModelAdvice(model, userRepository);
            return "comment-edit";
        }

        Comment commentDB = commentRepository.findById(comment.getId()).get();

        commentDB.setText(comment.getText());

        commentRepository.save(commentDB);

        return "redirect:/list?id=" + commentDB.getPost().getId();
    }
}
