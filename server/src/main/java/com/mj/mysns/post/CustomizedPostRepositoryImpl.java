package com.mj.mysns.post;

import static com.mj.mysns.location.entity.QLegalAddress.legalAddress;
import static com.mj.mysns.post.entity.QPost.post;
import static com.mj.mysns.post.entity.QPostFile.postFile;
import static com.mj.mysns.user.entity.QUser.user;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import com.mj.mysns.post.entity.Post;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public CustomizedPostRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> findPostByCode(String code) {
        JPAQuery<Post> query = queryFactory.selectFrom(post)
            .join(post.legalAddress, legalAddress)
            .where(legalAddress.code.eq(code))
            .limit(10)
            .orderBy(post.postId.asc());
        List<Post> result = query.fetch();
        return result;
    }

    @Override
    public List<Post> findPostNear(String latitude, String longitude, int offset) {
        Point<G2D> point1 = point(WGS84,
            g(Double.parseDouble(longitude), Double.parseDouble(latitude)));

        List<Post> posts = queryFactory
            .selectFrom(post)
            .leftJoin(post.files, postFile).fetchJoin()
            .join(post.user, user).fetchJoin()
            .join(post.legalAddress, legalAddress).fetchJoin()
            .orderBy(
                stringTemplate(
                    "st_distancesphere({0}, {1})",
                    stringTemplate("st_centroid({0})", legalAddress.location), point1)
                    .asc())
            .limit(10)
            .offset(offset)
            .fetch();
        return posts;

    }
}
