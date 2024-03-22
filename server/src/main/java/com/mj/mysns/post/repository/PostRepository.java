package com.mj.mysns.post.repository;

import com.mj.mysns.post.entity.Post;
import com.mj.mysns.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, CustomizedPostRepository {

//    Optional<Post> findByPostId(Long id);

    Optional<List<Post>> findByUser(User user);
}
