package com.martin.article.model;

import lombok.Data;
import org.hibernate.mapping.Join;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT NOT NULL")
    private String content;

    @Column(name = "publication_date", columnDefinition = "TIMESTAMP NOT NULL DEFAULT now()")
    private Date publicationDate;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToMany(targetEntity = Tag.class)
    @JoinTable(name = "article_tag",
            joinColumns = {@JoinColumn(name = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "article_user_liked",
            joinColumns = {@JoinColumn(name = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersLiked;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "article_user_disliked",
            joinColumns = {@JoinColumn(name = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersDisliked;
}
