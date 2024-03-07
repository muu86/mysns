package com.mj.mysns.domain.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mj.mysns.domain.post.model.dto.PostDto;
import com.mj.mysns.domain.post.model.entity.Post;
import com.mj.mysns.domain.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class DefaultPostServiceTest {

    @MockBean
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Test
    void createPost_validPost_shouldSucceed() {
        PostDto validPost = new PostDto("올바른 컨텐츠");
        Post postToCreate = Post.create(validPost.content());
        when(postRepository.save(any(Post.class)))
            .thenReturn(postToCreate);

        PostDto createdPost = postService.createPost(validPost);

        assertNotNull(createdPost);
        assertEquals(createdPost.content(), validPost.content());
    }
}