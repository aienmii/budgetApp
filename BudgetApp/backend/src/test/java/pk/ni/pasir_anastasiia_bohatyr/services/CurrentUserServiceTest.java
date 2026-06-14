package pk.ni.pasir_anastasiia_bohatyr.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;
import pk.ni.pasir_anastasiia_bohatyr.service.CurrentUserService;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CurrentUserService currentUserService;

    // ----------------------------------------------------
    // SUCCESS: authenticated user exists in database
    // ----------------------------------------------------
    @Test
    void shouldReturnCurrentUser() throws Exception {
        // Mock authentication
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@mail.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Mock user in DB
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        // Execute
        User result = currentUserService.getCurrentUser();

        // Verify
        assertEquals("test@mail.com", result.getEmail());
    }

    // ----------------------------------------------------
    // FAIL: no authentication in SecurityContext
    // ----------------------------------------------------
    @Test
    void shouldThrowWhenNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThrows(AccessDeniedException.class,
                () -> currentUserService.getCurrentUser());
    }

    // ----------------------------------------------------
    // FAIL: authentication exists but user not found in DB
    // ----------------------------------------------------
    @Test
    void shouldThrowWhenUserNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("missing@mail.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("missing@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> currentUserService.getCurrentUser());
    }
}
