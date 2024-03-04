package com.mj.mysns.domain.post.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mj.mysns.domain.post.model.entity.Post;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PostRepositoryTest {

    @Autowired private PostRepository postRepository;

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.3.0");

    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    private Post savedPost;

    @BeforeEach
    void setupDb() {
        postRepository.deleteAll();
        Post newPost = Post.create("새 포스트");
        savedPost = postRepository.save(newPost);
        assertEquals(newPost.getPostId(), savedPost.getPostId());
    }

    @Test
    public void create() {
        Post newPost = Post.create("새 포스트");
        postRepository.save(newPost);

        Post foundPost = postRepository.findById(newPost.getPostId()).get();
        assertEquals(newPost.getPostId(), foundPost.getPostId());
    }
}