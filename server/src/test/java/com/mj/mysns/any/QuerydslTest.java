package com.mj.mysns.any;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuerydslTest {

    @PersistenceContext
    EntityManager em;

    @Test
    void t1() {
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//
//        QLegalAddress legalAddress = QLegalAddress.legalAddress;
//        LegalAddress legalAddress1 = queryFactory.selectFrom(legalAddress)
//            .where(legalAddress.id.eq(1L))
//            .fetchFirst();
//        System.out.println(legalAddress1);
    }
}
