package com.example.blog.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    private Boolean hide;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private int likeRate;

    private Double cringeStatus;

    public Comment(String text, Date dateTime, Boolean hide, int likeRate, Double cringeStatus, User user, Post post) {
        this.text = text;
        this.dateTime = dateTime;
        this.hide = hide;
        this.likeRate = likeRate;
        this.cringeStatus = cringeStatus;
        this.user = user;
        this.post = post;
    }

    public Comment() {}

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getHide() {
        return this.hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public int getLikeRate() {
        return this.likeRate;
    }

    public void setLikeRate(int likeRate) {
        this.likeRate = likeRate;
    }

    public Double getCringeStatus() {
        return this.cringeStatus;
    }

    public void setCringeStatus(Double cringeStatus) {
        this.cringeStatus = cringeStatus;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
