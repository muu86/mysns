package com.mj.mysns.api;

import com.mj.mysns.domain.post.model.dto.CreatePostDto;
import com.mj.mysns.domain.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<String> createPost(
        @RequestParam String content,
        @RequestParam List<MultipartFile> files) {

        CreatePostDto createPostDto = CreatePostDto.builder()
            .content(content)
            .files(files).build();
        postService.createPost(createPostDto);

        return ResponseEntity.ok("포스트 저장 성공");
    }
}
