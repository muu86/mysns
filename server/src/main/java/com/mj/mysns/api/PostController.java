package com.mj.mysns.api;

import com.mj.mysns.api.payload.CreatePostRequest;
import com.mj.mysns.api.payload.CreatePostResponse;
import com.mj.mysns.domain.post.model.dto.PostDto;
import com.mj.mysns.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping(
        value = "/posts",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePostResponse> createPost(
        @Valid @RequestBody CreatePostRequest createPostRequest) {
        PostDto newPost = new PostDto(createPostRequest.content());
        PostDto savedPost = postService.createPost(new PostDto(null));
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new CreatePostResponse("success"));
    }
}
