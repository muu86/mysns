package com.mj.mysns.domain.post.model.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_file")
@NoArgsConstructor
@Getter
public class PostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaFileId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    // 포스트에서 사진 나열 순서
    private Integer displayOrder;

    @Embedded
    private FileLocation fileLocation;

    public static PostFile create(Post post, Integer displayOrder, FileLocation fileLocation) {
        PostFile n = new PostFile();
        n.post = post;
        n.displayOrder = displayOrder;
        n.fileLocation = fileLocation;
        return n;
    }
}
