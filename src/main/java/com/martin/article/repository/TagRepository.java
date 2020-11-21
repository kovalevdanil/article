package com.martin.article.repository;

import com.martin.article.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {

    @Query(value = "SELECT * FROM tags WHERE name = :name LIMIT 1", nativeQuery = true)
    Optional<Tag> findByName(String name);
}
