package com.mj.mysns.post.payload;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

public record CreatePostPayload(

    String content,

    MultiValueMap<String, MultipartFile> files
) {
}
