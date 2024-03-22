package com.mj.mysns.post.entity;

import com.mj.mysns.common.file.FileLocation;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor
@Getter

@Entity
public class PostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    // 포스트에서 사진 나열 순서
    private Integer displayOrder;

    @Embedded
    private FileLocation fileLocation;

    @Builder
    private PostFile(Post post, Integer displayOrder, FileLocation fileLocation) {
        Assert.notNull(post, "post 가 null 입니다.");

        this.post = post;
        this.displayOrder = displayOrder;
        this.fileLocation = fileLocation;
    }
}
