package br.com.webfluxcourse.service;

import br.com.webfluxcourse.entity.User;
import br.com.webfluxcourse.mapper.UserMapper;
import br.com.webfluxcourse.model.request.UserRequest;
import br.com.webfluxcourse.repository.UserRepository;
import br.com.webfluxcourse.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void whenFindAll_thenReturnSucess() {
        User entity = buildUser();
        when(repository.findAll()).thenReturn(Flux.just(entity));

        Flux<User> result = service.findAll();

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).findAll();
    }

    @Test
    void whenUpdate_thenReturnSucess() {
        UserRequest request = buildUserRequest();
        User entity = buildUser();

        when(mapper.toEntity(any(UserRequest.class), any(User.class))).thenReturn(entity);
        when(repository.findById(anyString())).thenReturn(Mono.just(entity));
        when(repository.save(any(User.class))).thenReturn(Mono.just(entity));

        Mono<User> result = service.update("123", request);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void whenDelete_thenReturnSucess() {
        User entity = buildUser();

        when(repository.findAndRemove(anyString())).thenReturn(Mono.just(entity));

        Mono<User> result = service.delete("123");

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).findAndRemove(anyString());
    }

    private User buildUser() {
        return User.builder()
                .id("123")
                .name("alfeu")
                .email("alfeu@hotmail.com")
                .password("123")
                .build();
    }

    @Test
    void whenUserNotFound_thenThrowHandlerNotFoundException(){
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        try {
            service.findById("123").block();
        } catch (Exception ex) {
            assertEquals(ObjectNotFoundException.class, ex.getClass());

            assertEquals(format("Object not found. Id: %s, Type: %s", "123",
                    User.class.getSimpleName()), ex.getMessage());
        }
    }

    private UserRequest buildUserRequest() {
        return new UserRequest("alfeu", "alfeup@hotmail.com", "123");
    }


}