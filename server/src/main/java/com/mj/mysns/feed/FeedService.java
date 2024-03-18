package com.mj.mysns.feed;


import com.mj.mysns.post.PostService;
import com.mj.mysns.post.entity.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {

    private final PostService postService;

    public List<Post> getFeeds(String latitude, String longitude, int offset) {

        List<Post> postsNear = postService.getPostsNear(latitude, longitude, offset);

        return postsNear;
    }

}
