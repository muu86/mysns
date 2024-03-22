package com.mj.mysns.user;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mj.mysns.common.exception.AddressNotFoundException;
import com.mj.mysns.common.exception.DuplicatedUserException;
import com.mj.mysns.common.exception.DuplicatedUsernameException;
import com.mj.mysns.common.file.FileLocation;
import com.mj.mysns.common.file.FileLocation.FileLocationType;
import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.location.repository.AddressRepository;
import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.entity.User;
import com.mj.mysns.user.repository.UserRepository;
import java.util.Optional;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.jts.JTS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class UserServiceImplTest {

    @Autowired UserService userService;
    @MockBean UserRepository userRepository;
    @MockBean AddressRepository addressRepository;

    @Test
    void findByIssuerAndSubject() {
    }

    @Test
    void findByAuthentication() {
    }

    @Test
    void saveUser_exisingUser_throwsException() {
        UserDto userDto = UserDto.builder().issuer("test").subject("test").username("test").legalAddressCode("1234").build();

        when(userRepository.findByIssuerAndSubject("test", "test")).thenReturn(Optional.of(User.builder()
            .build()));

        assertThrows(DuplicatedUserException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void saveUser_existingUsername_throwsException() {
        UserDto userDto = UserDto.builder().issuer("test").subject("test").username("test").legalAddressCode("1234").build();

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(User.builder()
            .build()));

        assertThrows(DuplicatedUsernameException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void saveUser_addressNotFound_throwsException() {
        UserDto userDto = UserDto.builder().issuer("test").subject("test").username("test").legalAddressCode("1234").build();

        when(addressRepository.findByCode("1234")).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void saveUser_success() {
        UserDto userDto = UserDto.builder().issuer("test").subject("test").username("test").legalAddressCode("1234").build();
        User toBeReturned = User.builder().issuer("test").subject("test").username("test").build();

        when(userRepository.findByIssuerAndSubject("test", "test")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(addressRepository.findByCode("1234")).thenReturn(Optional.of(LegalAddress.builder().build()));
        when(userRepository.save(any(User.class))).thenReturn(toBeReturned);

        userService.saveUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserProfileByUsername() {
        UserDto userDto = UserDto.builder().username("test").build();

        Point<G2D> point = point(WGS84,
            g(126.96567699902982, 37.59554172008826));
        org.locationtech.jts.geom.Point point1 = JTS.to(point);
        LegalAddress address1 = LegalAddress.builder().code("1").location(point1).build();
        LegalAddress address2 = LegalAddress.builder().code("2").location(point1).build();

        User returnedUser = User.builder()
            .username("test")
            .babyAge(12)
            .content("안녕하세요")
            .build();
        returnedUser.addUserAddress(address1);
        returnedUser.addUserAddress(address2);
        returnedUser.addUserFile(new FileLocation(FileLocationType.S3, "test_1"), true);
        returnedUser.addUserFile(new FileLocation(FileLocationType.S3, "test_2"), false);
        when(userRepository.findUserProfile(userDto)).thenReturn(Optional.of(returnedUser));

        Optional<UserDto> foundOptional = userService.getUserProfileByUsername(userDto);

        assertTrue(foundOptional.isPresent());
        UserDto found = foundOptional.get();
        assertEquals(found.username(), returnedUser.getUsername());
        assertEquals(found.babyAge(), returnedUser.getBabyAge());
        assertEquals(found.content(), returnedUser.getContent());
        assertNotNull(found.userAddresses());
        assertEquals(found.userAddresses().size(), 2);
        assertNotNull(found.userFiles());
        assertEquals(found.userFiles().size(), 2);
    }

    @Test
    void getUserProfileByUsername_userDtoNull_throwsException() {
        Exception exception = assertThrows(
            IllegalArgumentException.class, () -> {
                userService.getUserProfileByUsername(null);
            });
        assertTrue(exception.getMessage().contains("userDto 가 null 입니다."));
    }
}