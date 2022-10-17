package com.example.blog.controllers;

import com.example.blog.models.Comment;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.CommentRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @PostMapping("/create-comment")
    public String createComment(@RequestParam long post_id, @RequestParam String text, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByLogin(authentication.getName());

        Post post = postRepository.findById(post_id).get();

        Comment comment = new Comment(text.trim(), new Date(), false, 0, 0.0, user, post);

        commentRepository.save(comment);

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

        Comment comment = commentRepository.findById(comment_id).get();

        model.addAttribute("comment", comment);

        return "comment-edit";
    }

    @PostMapping("/comment/edit")
    public String commentEdit(@RequestParam long id,
                              @RequestParam String text,
                              Model model) {

        Comment comment = commentRepository.findById(id).get();

        comment.setText(text);

        commentRepository.save(comment);

        return "redirect:/list?id=" + comment.getPost().getId();
    }
}
