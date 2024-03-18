package com.mj.mysns.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mj.mysns.post.entity.Post;
import com.mj.mysns.post.entity.PostFile;
import com.mj.mysns.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostTest {

    @Mock
    private User mockUser;

    @Mock
    private PostFile mockPostFile;

    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
            .user(mockUser)
            .content("Test Content")
            .build();
    }

    @Test
    void createPost_nullUser_throwException() {
        assertThrowsExactly(
            IllegalArgumentException.class,
            () -> Post.builder().content("Test Content").user(null).build(),
            "user 가 null 입니다.");
    }

    @Test
    void createPost_withFiles_successAndEmptyFileList() {
        assertNotNull(post);
        assertEquals("Test Content", post.getContent());
        assertEquals(mockUser, post.getUser());
        assertNotNull(post.getFiles());
        assertTrue(post.getFiles().isEmpty());
    }

    @Test
    void createPost_withFiles_success() {
        Post post = Post.builder()
            .user(mockUser)
            .content("test content")
            .files(List.of(mockPostFile))
            .build();

        assertEquals(mockUser, post.getUser());
        assertEquals("test content", post.getContent());
        assertEquals(1, post.getFiles().size());
        assertEquals(mockPostFile, post.getFiles().get(0));
    }

}