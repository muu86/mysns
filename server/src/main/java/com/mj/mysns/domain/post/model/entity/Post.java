package com.mj.mysns.domain.post.model.entity;

import com.mj.mysns.domain.user.model.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Version
    private Long version;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    // user가 아마 사진의 순서를 바꿀 수 있기 때문에 순서가 중요할 것 같다.
    // 나중에 고려하는 걸로
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostFile> files = new ArrayList<>();

    @Builder
    private Post(User user, String content, List<PostFile> files) {
        this.user = user;
        this.content = content;
        this.files = files;
    }

    public static Post create(String content, User user) {
        Assert.notNull(user, "user 가 null 입니다.");

        Post post = new Post();
        post.user = user;
        post.content = content;
        return post;
    }
}
