package com.mj.mysns.batch.mockdata;

import com.mj.mysns.post.PostRepository;
import com.mj.mysns.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class MockPostWriter implements ItemWriter<Post> {

    private final PostRepository postRepository;

    @Override
    public void write(Chunk<? extends Post> chunk) throws Exception {
        postRepository.saveAll(chunk);
    }
}
