package com.mj.mysns.domain.user.repository;

import com.mj.mysns.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByFirstAndLastAndEmail(String first, String last, String email);
}
