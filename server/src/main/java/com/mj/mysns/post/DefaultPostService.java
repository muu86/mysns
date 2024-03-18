package com.mj.mysns.post;

import com.mj.mysns.common.file.FileService;
import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.post.dto.CreatePostDto;
import com.mj.mysns.post.entity.FileLocation;
import com.mj.mysns.post.entity.FileLocation.FileLocationType;
import com.mj.mysns.post.entity.Post;
import com.mj.mysns.post.entity.PostFile;
import com.mj.mysns.user.UserService;
import com.mj.mysns.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j

@Service
public class DefaultPostService implements PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    private final FileService fileService;

    @Override
    public void createPost(CreatePostDto createPostDto) {

        // user 정보
        Optional<User> found = userService.findUserByAuthentication(null);
        if (found.isEmpty()) {
            log.info("user 를 찾을 수 없습니다.");
            return;
        }
        User user = found.get();

        // post entity 생성
        Post post = Post.builder().content(createPostDto.content()).user(user).build();
        // file 저장
        int failedCount = 0;
        for (MultipartFile file : createPostDto.files()) {
            Optional<String> result = fileService.saveFile(file);
            if (result.isPresent()) {
                String key = result.get();
                // postfile entity 생성 후 post에 add
                post.getFiles().add(PostFile.builder()
                    .post(post)
                    .displayOrder(0)
                    // file location 하드코딩
                    .fileLocation(new FileLocation(FileLocationType.S3, key))
                    .build());
            } else {
                failedCount++;
            }
        }
        log.info("{}개의 파일 업로드 결과: {}개 성공, {}개 실패", createPostDto.files().size(), createPostDto.files().size() - failedCount, failedCount);

        // post 저장
        postRepository.save(post);
    }

    @Override
    public void updatePost(Long postId, CreatePostDto createPostDto) {
        Optional<Post> byId = postRepository.findById(postId);
        if (byId.isEmpty()) {
            log.info("post 를 찾을 수 없습니다.");
            return;
        }

        Post post = byId.get();
        // 일단 content 만 수정
        post.setContent(createPostDto.content());
        postRepository.save(post);
    }

    @Override
    public List<Post> getPosts(LegalAddress address) {

        List<Post> posts = postRepository.findPostByCode(address.getCode());

        return posts;
    }

    @Override
    public List<Post> getPostsNear(String latitude, String longitude, int offset) {
        List<Post> postNear = postRepository.findPostNear(latitude, longitude, offset);
        return postNear;
    }

}
