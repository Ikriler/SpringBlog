package com.example.blog.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String login;

    private String password;

    private int age;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Post> postList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Comment> commentList = new ArrayList<>();

    private Double growth;

    @Temporal(TemporalType.TIMESTAMP)
    private Date bd_date;

    private Boolean admin;

    public User(String login, String password, int age, Double growth, Date bd_date, Boolean admin) {
        this.login = login;
        this.password = password;
        this.age = age;
        this.growth = growth;
        this.bd_date = bd_date;
        this.admin = admin;
    }

    public User() {}


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Double getGrowth() {
        return this.growth;
    }

    public void setGrowth(Double growth) {
        this.growth = growth;
    }

    public Date getBd_date() {
        return this.bd_date;
    }

    public void setBd_date(Date bd_date) {
        this.bd_date = bd_date;
    }

    public Boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public List<Post> getPostList() {
        return this.postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public List<Comment> getCommentList() {
        return this.commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
