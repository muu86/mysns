package com.mj.mysns.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.mj.mysns.post.entity.FileLocation;
import com.mj.mysns.post.entity.FileLocation.FileLocationType;
import com.mj.mysns.post.entity.Post;
import com.mj.mysns.post.entity.PostFile;
import com.mj.mysns.user.UserRepository;
import com.mj.mysns.user.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired private UserRepository userRepository;

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

    Post saved;

    @BeforeEach
    void saveOnePost() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        User savedUser = userRepository.save(User.builder().email("test@gmail.com").build());
        Post created = Post.builder()
            .content("새 포스트")
            .user(savedUser)
            .build();
        created.getFiles().add(PostFile.builder().post(created).displayOrder(0).fileLocation(new FileLocation(FileLocationType.S3, "111")).build());
        created.getFiles().add(PostFile.builder().post(created).displayOrder(1).fileLocation(new FileLocation(FileLocationType.S3, "222")).build());
        created.getFiles().add(PostFile.builder().post(created).displayOrder(2).fileLocation(new FileLocation(FileLocationType.S3, "333")).build());
        saved = postRepository.save(created);

        assertNotNull(saved.getPostId());
        assertEquals(saved.getPostId(), created.getPostId());
    }

}