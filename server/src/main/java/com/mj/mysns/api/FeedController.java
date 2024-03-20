package com.mj.mysns.api;

import com.mj.mysns.feed.FeedService;
import com.mj.mysns.post.entity.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j

@RestController
public class FeedController {

    private final FeedService feedService;


    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed(
        Authentication authentication,
        @AuthenticationPrincipal
        // 위치정보
        @RequestParam("latitude") String latitude,
        @RequestParam("longitude") String longitude,
        @RequestParam("offset") int offset
    ) {
        List<Post> feeds = feedService.getFeeds(latitude, longitude, offset);

        return ResponseEntity.ok(feeds);
    }
}
