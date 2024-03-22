package com.mj.mysns.user.repository;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mj.mysns.common.file.FileLocation;
import com.mj.mysns.common.file.FileLocation.FileLocationType;
import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.location.repository.AddressRepository;
import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUtil;
import java.util.List;
import java.util.Optional;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.jts.JTS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CustomizedUserRepositoryImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        DockerImageName.parse("postgis/postgis:16-master").asCompatibleSubstituteFor("postgres"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @PersistenceContext
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    User saved;

    @BeforeEach
    void setup() {
        Point<G2D> point = point(WGS84,
            g(126.96567699902982, 37.59554172008826));
        org.locationtech.jts.geom.Point point1 = JTS.to(point);
        LegalAddress address1 = LegalAddress.builder().code("1").location(point1).build();
        LegalAddress address2 = LegalAddress.builder().code("2").location(point1).build();
        addressRepository.saveAll(List.of(address1, address2));

        saved = User.builder()
            .username("test")
            .babyAge(12)
            .content("안녕하세요")
            .build();
        saved.addUserAddress(address1);
        saved.addUserAddress(address2);
        saved.addUserFile(new FileLocation(FileLocationType.S3, "test_1"), true);
        saved.addUserFile(new FileLocation(FileLocationType.S3, "test_2"), false);

        userRepository.save(saved);

        em.flush();
        em.clear();
    }

    @Test
    void findUserProfile_usernameExists_returnUser() {
        Optional<User> found = userRepository.findUserProfile(
            UserDto.builder().username("test").build());

        assertTrue(found.isPresent());
        User found1 = found.get();
        assertEquals(found1.getUsername(), saved.getUsername());

        PersistenceUtil util = Persistence.getPersistenceUtil();
        assertTrue(util.isLoaded(found1.getUserAddresses()));
        assertTrue(util.isLoaded(found1.getUserFiles()));
    }

    @Test
    void findUserProfile_usernameNotExists_returnEmpty() {
        Optional<User> found = userRepository.findUserProfile(
            UserDto.builder().username("test1").build());

        assertTrue(found.isEmpty());
    }

    @Test
    void findUserProfile_usernameNull_returnEmpty() {
        Optional<User> found = userRepository.findUserProfile(
            UserDto.builder().username(null).build());

        assertTrue(found.isEmpty());
    }
}