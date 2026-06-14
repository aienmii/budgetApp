package pk.ni.pasir_anastasiia_bohatyr.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pk.ni.pasir_anastasiia_bohatyr.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void shouldGenerateToken() {
        //given
        User user = new User();
        user.setEmail("admin");
        user.setPassword("admin");

        //when
        String token = jwtUtil.generateToken(user);

        //then
        assertFalse(token.isBlank());
    }

    @Test
    public void shouldNotGenerateToken() {
        //g
        User user = new User();

        assertThrows(IllegalStateException.class,
                () -> jwtUtil.generateToken(user));

    }

    @Test
    public void shouldValidateToken() {
        //given

        User user = new User();
        user.setEmail("admin");
        user.setPassword("admin");
        String token = jwtUtil.generateToken(user);

        //when
        boolean result = jwtUtil.validateToken(token);

        //then
        assertTrue(result);
    }
}
