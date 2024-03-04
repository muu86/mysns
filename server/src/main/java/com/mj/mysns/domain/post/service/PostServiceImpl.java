package com.mj.mysns.domain.post.service;

import com.mj.mysns.domain.post.model.dto.PostDto;
import com.mj.mysns.domain.post.model.entity.Post;
import com.mj.mysns.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = Post.create(postDto.getContent());
        Post saved = postRepository.save(post);
        return new PostDto(saved.getContent());
    }
}
