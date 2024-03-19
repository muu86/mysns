package com.mj.mysns.user;

import com.mj.mysns.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByIssuerAndSubject(String issuer, String subject);

    Optional<User> findByEmail(String email);
}
