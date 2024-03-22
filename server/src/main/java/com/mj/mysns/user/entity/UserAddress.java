package com.mj.mysns.user.entity;

import com.mj.mysns.location.entity.LegalAddress;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter

@Entity
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private LegalAddress legalAddress;

    public UserAddress(User user, LegalAddress legalAddress) {
        this.user = user;
        this.legalAddress = legalAddress;
    }
}
