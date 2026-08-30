package org.tracker;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tracker.configuration.JwtAuthenticationFilter;
import org.tracker.configuration.SecurityConfig;
import org.tracker.model.entities.User;
import org.tracker.service.JwtService;
import org.tracker.service.UserService;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfigSliceTest.TestController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        SecurityConfigSliceTest.TestController.class
})
public class SecurityConfigSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @RestController
    @RequestMapping("/test")
    public static class TestController {
        @PostMapping("/authorized-endpoint")
        public void authorizedEndpoint(){}

        @PostMapping("/unauthorized-endpoint")
        public void unauthorizedEndpoint(){}
    }

    @Test
    public void protectedEndpoint_allowedOrigin_validJwt_returns200() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User mockUser = new User(mockId, "Renz", "Tabuzo", "renzonifico@gmail.com",
                null, Instant.now());

        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTEiLCJuYW1lIjoiUmVueiBUYWJ1em8iLCJpYXQiOjE1MTYyMzkwMjJ9." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        when(jwtService.extractId(any(String.class)))
                .thenReturn(mockId);

        when(userService.getUserById(any(UUID.class)))
                .thenReturn(mockUser);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/test/authorized-endpoint")
                .header("Authorization", "Bearer " + mockToken)
                .header("Origin", "https://authorized-domain.com");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void protectedEndpoint_disallowedOrigin_returns403() throws Exception {
        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTEiLCJuYW1lIjoiUmVueiBUYWJ1em8iLCJpYXQiOjE1MTYyMzkwMjJ9." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        RequestBuilder request = MockMvcRequestBuilders
                .post("/test/authorized-endpoint")
                .header("Authorization", "Bearer " + mockToken)
                .header("Origin", "https://unauthorized-domain.com");

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void protectedEndpoint_missingJwt_returns401() throws Exception {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTE`MTExMTExMTEiLCJuYW1lIjoiUmVueiBUYWJ1em8iLCJpYXQiOjE1MTYyMzkwMjJ9." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV`adQssw5c";

        when(jwtService.extractId(any(String.class)))
                .thenThrow(new JwtException("Invalid jwt"));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/test/authorized-endpoint")
                .header("Authorization", "Bearer " + invalidToken)
                .header("Origin", "https://authorized-domain.com");

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void invalidEndpoint_allowedOrigin_validJwt_returns403() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User mockUser = new User(mockId, "Renz", "Tabuzo", "renzonifico@gmail.com",
                null, Instant.now());

        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTEiLCJuYW1lIjoiUmVueiBUYWJ1em8iLCJpYXQiOjE1MTYyMzkwMjJ9." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        when(jwtService.extractId(any(String.class)))
                .thenReturn(mockId);

        when(userService.getUserById(any(UUID.class)))
                .thenReturn(mockUser);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/test/unauthorized-endpoint")
                .header("Authorization", "Bearer " + mockToken)
                .header("Origin", "https://authorized-domain.com");

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }
}
