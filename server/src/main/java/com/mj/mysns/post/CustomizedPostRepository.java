package com.mj.mysns.post;

import com.mj.mysns.post.entity.Post;
import java.util.List;

public interface CustomizedPostRepository {

    List<Post> findPostByCode(String code);

    List<Post> findPostNear(String latitude, String longitude, int offset);
}
