package com.mj.mysns.post;

import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.post.dto.CreatePostDto;
import com.mj.mysns.post.entity.Post;
import java.util.List;

public interface PostService {

    void createPost(CreatePostDto createPostDto);

    void updatePost(Long postId, CreatePostDto createPostDto);

    List<Post> getPosts(LegalAddress address);

    List<Post> getPostsNear(String latitude, String longitude, int offset);
}
