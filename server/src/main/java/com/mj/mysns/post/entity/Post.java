package com.mj.mysns.post.entity;

import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(optional = false)
    private User user;

    @Column(length = 300)
    @Setter
    private String content;

    // user 가 사진 순서를 바꿀 수 있도록 할 것
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostFile> files = new ArrayList<>();

    @ManyToOne
    @Setter
    private LegalAddress legalAddress;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime deletedAt;

    @Builder
    private Post(User user, String content, List<PostFile> files) {
        Assert.notNull(user, "user 가 null 입니다.");

        this.user = user;
        this.content = content;
        this.files = files == null ? new ArrayList<>() : files;
    }

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setModifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }
}
