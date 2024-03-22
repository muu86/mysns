package com.mj.mysns.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mj.mysns.user.UserService;
import com.mj.mysns.user.dto.UserDto;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    WebTestClient client;

    @Autowired MessageSource ms;

    @Test
    void getUserProfile_withoutUsername_status400() {
        client.get().uri("/user/profile")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getUserProfile_emptyUsername_status400() {
        client.get().uri("/user/profile?username=")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getUserProfile_notExistingUsername_statue404() {
        UserDto userDto = UserDto.builder().username("test").build();
        when(userService.getUserProfileByUsername(userDto)).thenReturn(Optional.empty());
        client.get().uri("/user/profile=username=test")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void getUserProfile_existingUsername_statue200() {
        UserDto userDto = UserDto.builder().username("test").build();

        when(userService.getUserProfileByUsername(userDto)).thenReturn(Optional.of(UserDto.builder()
            .username("test")
            .babyAge(10)
            .content("소개글")
            .build()));

        client.get().uri("/user/profile?username=test")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.username").isEqualTo("test");

        verify(userService, times(1)).getUserProfileByUsername(userDto);
    }

    @Test
    void checkUserExistsByIssuerAndSubject_notExists_statue404() {
        UserDto userDto = UserDto.builder().issuer("test").subject("test").build();

        when(userService.checkUserByIssuerAndSubject(userDto)).thenReturn(Optional.empty());

        client.get().uri(uriBuilder -> uriBuilder.path("/user/exists")
                .queryParam("issuer", "test")
                .queryParam("subject", "test").build())
            .exchange()
            .expectStatus().isNotFound();

        verify(userService, times(1)).checkUserByIssuerAndSubject(argThat(arg -> {
            assertNotNull(arg);
            assertEquals(arg.issuer(), "test");
            assertEquals(arg.subject(), "test");
            return true;
        }));
    }

    @Test
    void checkUserExistsByIssuerAndSubject_exists_statue200() {
        UserDto userDto = UserDto.builder().issuer("test").subject("test").build();

        when(userService.checkUserByIssuerAndSubject(userDto)).thenReturn(Optional.of(UserDto.builder()
            .username("test")
            .babyAge(10)
            .content("소개글")
            .build()));

        client.get().uri(uriBuilder -> uriBuilder
                .path("/user/exists")
                .queryParam("issuer", "test")
                .queryParam("subject", "test").build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.message").isEqualTo("success")
            .jsonPath("$.username").isEqualTo("test");

        verify(userService, times(1)).checkUserByIssuerAndSubject(argThat(arg -> {
            assertNotNull(arg);
            assertEquals(arg.issuer(), "test");
            assertEquals(arg.subject(), "test");
            return true;
        }));
    }

    @Test
    void createUser_invalidBabyAge_status400() {
        MultipartBodyBuilder fd = new MultipartBodyBuilder();
        fd.part("sub", "sub");
        fd.part("iss", "iss");
        fd.part("first", "first");
        fd.part("last", "last");
        fd.part("email", "email@emal.com");
        fd.part("emailVerified", true);
        fd.part("username", "username");
        fd.part("babyAge", -1);
        fd.part("legalAddressCode", "123");

        client.post().uri("/user")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(fd.build()))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.babyAge").isEqualTo(ms.getMessage("Min.CreateUserPayload.babyAge", null, null));
    }

    @Test
    void createUser_nullFields_status400() {
        MultipartBodyBuilder fd = new MultipartBodyBuilder();
        fd.part("emailVerified", true);
        fd.part("babyAge", 1);


        client.post().uri("/user")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(fd.build()))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(7)
            .jsonPath("$.sub").isEqualTo("공백일 수 없습니다")
            .jsonPath("$.iss").isEqualTo("공백일 수 없습니다")
            .jsonPath("$.first").isEqualTo("공백일 수 없습니다")
            .jsonPath("$.last").isEqualTo("공백일 수 없습니다")
            .jsonPath("$.email").isEqualTo("공백일 수 없습니다")
            .jsonPath("$.username").isEqualTo(ms.getMessage("NotBlank.CreateUserPayload.username", null, null))
            .jsonPath("$.legalAddressCode").isEqualTo("공백일 수 없습니다");
    }

    @Test
    void createUser_valid_status200() {
        MultipartBodyBuilder fd = new MultipartBodyBuilder();
        fd.part("sub", "sub");
        fd.part("iss", "iss");
        fd.part("first", "first");
        fd.part("last", "last");
        fd.part("email", "email@email.com");
        fd.part("emailVerified", true);
        fd.part("username", "username");
        fd.part("babyAge", 1);
        fd.part("legalAddressCode", "123");

        doNothing().when(userService).saveUser(any(UserDto.class));

        client.post().uri("/user")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(fd.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.message").isEqualTo("success");

        verify(userService).saveUser(argThat(arg -> {
            assertEquals(arg.subject(), "sub");
            assertEquals(arg.issuer(), "iss");
            assertEquals(arg.first(), "first");
            assertEquals(arg.last(), "last");
            assertEquals(arg.email(), "email@email.com");
            assertEquals(arg.emailVerified(), true);
            assertEquals(arg.username(), "username");
            assertEquals(arg.babyAge(), 1);
            assertEquals(arg.legalAddressCode(), "123");
            return true;
        }));
    }
}