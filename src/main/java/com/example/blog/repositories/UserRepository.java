package com.example.blog.repositories;

import com.example.blog.models.Post;
import com.example.blog.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByLogin(String login);

    Iterable<User> findByLoginContains(String title);

    Boolean existsByLogin(String login);

}
