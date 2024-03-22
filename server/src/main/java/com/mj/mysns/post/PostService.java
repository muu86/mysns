package com.mj.mysns.post;

import com.mj.mysns.post.dto.CreatePostDto;
import com.mj.mysns.post.entity.Post;
import java.util.List;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface PostService {

    void createPost(CreatePostDto createPostDto);

    void updatePost(Long postId, CreatePostDto createPostDto);

    List<Post> getPostsNear(String latitude, String longitude, int offset);
}
