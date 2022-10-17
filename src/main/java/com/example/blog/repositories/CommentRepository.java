package com.example.blog.repositories;

import com.example.blog.models.Comment;
import com.example.blog.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findCommentByPost(Post post);

    List<Comment> findCommentByPostAndTextOrderByDateTimeDesc(Post post, String text);

    List<Comment> findCommentByPostAndTextContainsOrderByDateTimeDesc(Post post, String text);

}
