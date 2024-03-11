package com.mj.mysns.domain.post.service;

import com.mj.mysns.common.file.FileService;
import com.mj.mysns.domain.post.model.dto.CreatePostDto;
import com.mj.mysns.domain.post.model.entity.FileLocation;
import com.mj.mysns.domain.post.model.entity.FileLocation.FileLocationType;
import com.mj.mysns.domain.post.model.entity.Post;
import com.mj.mysns.domain.post.model.entity.PostFile;
import com.mj.mysns.domain.post.repository.PostRepository;
import com.mj.mysns.domain.user.model.entity.User;
import com.mj.mysns.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultPostService implements PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final FileService fileService;

    @Override
    public void createPost(CreatePostDto createPostDto) {

        // user 정보
        Jwt token = getToken();
        Optional<User> found = userRepository.findByEmail(token.getClaimAsString("email"));
        if (found.isEmpty()) {
            log.info("user 를 찾을 수 없습니다.");
            return;
        }
        User user = found.get();

        // post entity 생성
        Post post = Post.create(createPostDto.content(), user);
        // file 저장
        int failedCount = 0;
        for (MultipartFile file : createPostDto.files()) {
            Optional<String> result = fileService.saveFile(file);
            if (result.isPresent()) {
                String key = result.get();
                // postfile entity 생성
                PostFile createdPostFile = PostFile.create(post, 0,
                    // file location 하드코딩
                    new FileLocation(FileLocationType.S3, key));

                post.getFiles().add(createdPostFile);
            } else {
                failedCount++;
            }
        }
        log.info("{}개의 파일 업로드 결과: {}개 성공, {}개 실패", createPostDto.files().size(), createPostDto.files().size() - failedCount, failedCount);

        // post 저장
        postRepository.save(post);
    }

    private Jwt getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (Jwt) authentication.getPrincipal();
    }
}
