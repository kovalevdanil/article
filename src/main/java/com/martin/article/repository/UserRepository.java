package com.martin.article.repository;

import com.martin.article.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    @Query(value = "select * from users where username = :username limit 1", nativeQuery = true)
    Optional<User> findByUsername(@Param("username")String username);
}
