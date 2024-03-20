package com.mj.mysns.post.repository;

import com.mj.mysns.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, CustomizedPostRepository, QuerydslPredicateExecutor<Post> {

//    Optional<Post> findByPostId(Long id);

}
