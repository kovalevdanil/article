package com.martin.article.model;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "published_date", columnDefinition = "TIMESTAMP NOT NULL DEFAULT now()")
    private Date publishedDate;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "comment_user_liked",
            joinColumns = {@JoinColumn(name = "comment_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersLiked;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "comment_user_disliked",
            joinColumns = {@JoinColumn(name = "comment_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersDisliked;
}
