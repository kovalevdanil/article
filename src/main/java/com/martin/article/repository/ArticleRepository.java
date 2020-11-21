package com.martin.article.repository;

import com.martin.article.model.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {

    @Query(value = "SELECT COUNT(*) FROM articles WHERE slug like :slug ", nativeQuery = true )
    Integer countBySlug(@Param("slug") String slug);

    @Query(value = "SELECT * FROM articles WHERE slug = :slug", nativeQuery = true)
    Optional<Article> findBySlug(String slug);
}
