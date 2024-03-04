package com.mj.mysns.domain.post.service;

import com.mj.mysns.domain.post.model.dto.PostDto;
import com.mj.mysns.domain.post.model.entity.Post;

public interface PostService {

    PostDto createPost(PostDto postDto);

}
