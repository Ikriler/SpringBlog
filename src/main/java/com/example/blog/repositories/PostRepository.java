package com.example.blog.repositories;

import com.example.blog.models.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    Iterable<Post> findByTitleContains(String title);
    Iterable<Post> findByTitle(String title);
}
