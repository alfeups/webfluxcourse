package br.com.webfluxcourse.service;

import br.com.webfluxcourse.entity.User;
import br.com.webfluxcourse.mapper.UserMapper;
import br.com.webfluxcourse.model.request.UserRequest;
import br.com.webfluxcourse.model.response.UserResponse;
import br.com.webfluxcourse.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;


    @Test
    void whenSaveUser_thenReturnSucess() {
        UserRequest request = buildUserRequest();
        User entity = buildUser();

        when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
        when(repository.save(any(User.class))).thenReturn(Mono.just(entity));

        Mono<User> result = service.save(request);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void whenFindById_thenReturnSucess() {
        User entity = buildUser();
        when(repository.findById(anyString())).thenReturn(Mono.just(entity));

        Mono<User> result = service.findById("123");

        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getClass() == User.class
                        && Objects.equals(user.getId(), "123"))
                .expectComplete()
                .verify();

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void findAll() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private User buildUser() {
        return User.builder()
                .id("123")
                .name("alfeu")
                .email("alfeu@hotmail.com")
                .password("123")
                .build();
    }

    private UserRequest buildUserRequest() {
        return new UserRequest("alfeu", "alfeup@hotmail.com", "123");
    }

    private UserResponse buildUserResponse() {
        return new UserResponse("1","alfeu", "alfeup@hotmail.com", "123");
    }
}