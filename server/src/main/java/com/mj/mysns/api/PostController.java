package com.mj.mysns.api;

import com.mj.mysns.post.PostService;
import com.mj.mysns.post.dto.CreatePostDto;
import com.mj.mysns.post.entity.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.Jwt.Builder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

//    @PostMapping("/posts")
//    public ResponseEntity<Authentication> createPost(
//        Authentication authentication,
//        @RequestParam String content,
//        @RequestParam List<MultipartFile> files) {
//
//        CreatePostDto createPostDto = CreatePostDto.builder()
//            .content(content)
//            .files(files).build();
//        postService.createPost(createPostDto);
//
//        // 프론트와 ajax 통신 테스트
//        return ResponseEntity.status(200).body(authentication);
//    }

    @PostMapping
    public ResponseEntity<String> createPost(
        @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient,
        @AuthenticationPrincipal JwtAuthenticationToken token,
        Authentication authentication,
        @RequestParam Long postId,
        @RequestParam String content,
        @RequestParam List<MultipartFile> files) {

        // 임시로 createPostDto 사용
        CreatePostDto createPostDto = CreatePostDto.builder()
            .content(content)
            .files(files).build();
        postService.updatePost(postId, createPostDto);

        // 프론트와 ajax 통신 테스트
        return ResponseEntity.status(200).body("hi");
    }
}
