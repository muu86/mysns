package com.mj.mysns.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mj.mysns.user.UserService;
import com.mj.mysns.user.dto.UserDto;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    private WebTestClient client;

    @Test
    void username_exists_returnUser() throws Exception {

        String username = "abc";
        given(userService.findByUsername(username)).willReturn(
            Optional.ofNullable(UserDto.builder().username(username).build()));

        client.get().uri("/user" + "?username=" + username)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.username").isEqualTo(username);


        verify(userService).findByUsername(username);
    }

    @Test
    void username_notExists_status404() {
        String username = "abc";
        given(userService.findByUsername(anyString())).willReturn(Optional.empty());

        client.get().uri("/user" + "?username=" + username)
            .exchange()
            .expectStatus().isNotFound();

        verify(userService).findByUsername(username);
    }
}