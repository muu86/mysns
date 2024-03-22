package com.mj.mysns.user.repository;

import static com.mj.mysns.user.entity.QUser.user;
import static com.mj.mysns.user.entity.QUserAddress.userAddress;
import static com.mj.mysns.user.entity.QUserFile.userFile;

import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class CustomizedUserRepositoryImpl implements
    CustomizedUserRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public CustomizedUserRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<User> findUserProfile(UserDto userDto) {
        if (userDto.username() == null) return Optional.empty();

        User found = queryFactory
            .selectFrom(user).distinct()
            .leftJoin(user.userAddresses, userAddress).fetchJoin()
            .where(user.username.eq(userDto.username()))
            .fetchOne();
        if (found == null) return Optional.empty();

        found = queryFactory
            .selectFrom(user).distinct()
            .leftJoin(user.userFiles, userFile).fetchJoin()
            .where(user.eq(found))
            .fetchOne();

        return Optional.ofNullable(found);
    }
}
