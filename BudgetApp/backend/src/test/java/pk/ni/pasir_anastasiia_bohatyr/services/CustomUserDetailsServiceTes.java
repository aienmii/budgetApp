package pk.ni.pasir_anastasiia_bohatyr.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;
import pk.ni.pasir_anastasiia_bohatyr.service.CustomUserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    // -----------------------------
    // SUCCESS: user found
    // -----------------------------
    @Test
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("pass123");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("test@mail.com");

        assertEquals("test@mail.com", details.getUsername());
        assertEquals("pass123", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER")));
    }

    // -----------------------------
    // FAIL: user not found
    // -----------------------------
    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("missing@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@mail.com"));
    }
}
