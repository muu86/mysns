package com.mj.mysns.domain.post.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record CreatePostDto(

    @NotNull String content,

//    User user,

    List<MultipartFile> files
) {}
