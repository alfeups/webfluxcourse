package br.com.webfluxcourse.controller.impl;

import br.com.webfluxcourse.entity.User;
import br.com.webfluxcourse.mapper.UserMapper;
import br.com.webfluxcourse.model.request.UserRequest;
import br.com.webfluxcourse.model.response.UserResponse;
import br.com.webfluxcourse.service.UserService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static reactor.core.publisher.Mono.just;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    public static final String NAME = "alfeu";
    public static final String EMAIL = "alfeu@hotmail.com";
    final static String ID = "123";
    public static final String PASSWORD = "123";

    @Autowired
    private WebTestClient client;

    @MockBean
    private UserService service;

    @MockBean
    private UserMapper mapper;

    @MockBean
    private MongoClient mongoClient;

    @Test
    @DisplayName("Test endpoint SAVE with success.")
    void whenCallSave_thenReturnSuccess() {
        UserRequest request = buildUserRequest();

        when(service.save(any(UserRequest.class))).thenReturn(just(buildUser()));

        client.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();

        verify(service, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint SAVE throwing exception.")
    void whenCallSave_givenFieldsHaveSpaces_thenThrowsExceptionInvalidBody() {
        UserRequest request = buildBadUserRequest();

        when(service.save(any(UserRequest.class))).thenReturn(just(buildUser()));

        client.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot have blank spaces at the beginning or at the end.");
    }


    @Test
    @DisplayName("Test find by ID endpoint successfully.")
    void whenFindById_thenReturnSuccess() {
        when(service.findById(anyString())).thenReturn(just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(buildUserResponse());

        client.get().uri("/users/" + ID)
                .accept()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Test find by ID endpoint throwing exception.")
    void whenFindById_thenThrowException() {
        when(service.findById(anyString())).thenReturn(Mono.empty());
        when(mapper.toResponse(any(User.class))).thenReturn(buildUserResponse());

        client.get().uri("/users/" + "475685")
                .accept()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    @DisplayName("Test find all users endpoint successfully.")
    void findAll() {
        when(service.findAll()).thenReturn(Flux.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(buildUserResponse());

        client.get().uri("/users")
                .accept()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(ID)
                .jsonPath("$.[0].name").isEqualTo(NAME)
                .jsonPath("$.[0].email").isEqualTo(EMAIL)
                .jsonPath("$.[0].password").isEqualTo(PASSWORD);
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private User buildUser() {
        return User.builder()
                .id(ID)
                .name(NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }
    private UserRequest buildUserRequest() {
        return new UserRequest(NAME, EMAIL, PASSWORD);
    }

    private UserRequest buildBadUserRequest() {
        return new UserRequest(" alfeu ", EMAIL, PASSWORD);
    }

    private UserResponse buildUserResponse() {
        return new UserResponse(ID, NAME, EMAIL, PASSWORD);
    }
}