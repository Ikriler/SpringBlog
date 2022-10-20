package com.example.blog.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
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

    @NotBlank(message = "Поле не должно быть пустым")
    @Size(min = 6, max = 20, message = "Поле должно быть размером от 6 до 20 символов")
    private String password;


    @NotNull(message = "Поле не должно быть пустым")
    @Positive(message = "Возраст не может быть отрицательным")
    @Min(value = 15, message = "Вам должно быть не менее 15 лет")
    @Max(value = 200, message = "Вы должно быть не более 200 лет")
    private int age;

    @DecimalMin(value = "0.0", message = "Рост должен быть больше 0")
    @DecimalMax(value = "200.0", message = "Рост должен быть меньше 200")
    @Positive(message = "Рост не может быть отрицательным")
    @NotNull(message = "Поле не должно быть пустым")
    private Double growth;


    @Temporal(TemporalType.DATE)
    @NotNull(message = "Дата не должна быть пустой")
    @Past(message = "Дата должна быть меньше сегодняшней")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date bd_date;

    private Boolean admin;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "contacts_id")
    private Contacts contacts;

    @ManyToMany
    @JoinTable(name = "editors", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    public List<Post> postEditorsList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> postList;

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

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public List<Post> getPostEditorsList() {
        return postEditorsList;
    }

    public void setPostEditorsList(List<Post> postEditorsList) {
        this.postEditorsList = postEditorsList;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

}
