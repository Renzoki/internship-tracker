package org.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tracker.controller.AuthController;
import org.tracker.mapper.AuthMapper;
import org.tracker.model.entities.User;
import org.tracker.repository.UserRepository;
import org.tracker.service.impl.AuthServiceImpl;
import org.tracker.service.impl.JwtServiceImpl;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({AuthServiceImpl.class,
         AuthMapper.class,
         BCryptPasswordEncoder.class,
         JwtServiceImpl.class})
public class AuthControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void handleLogin_userExists() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String passwordHash = passwordEncoder.encode("password123");

        when(userRepository.findUserByEmail(any(String.class)))
                .thenReturn(Optional.of(new User(
                        mockId,
                        null,
                        null,
                        "renzonifico@gmail.com",
                        passwordHash,
                        null
                        ))
                );

        String requestBody = """
                {
                    "email": "renzonifico@gmail.com",
                    "password": "password123"
                }
                """;

        RequestBuilder request = MockMvcRequestBuilders
                .post("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").value(
                        matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")
                ));
    }

    @Test
    public void handleLogin_userDoesNotExist() throws Exception {
        when(userRepository.findUserByEmail(any(String.class)))
                .thenReturn(Optional.empty());

        String requestBody = """
                {
                    "email": "nonexistent_email@gmail.com",
                    "password": "nonexistentpassword"
                }
                """;

        RequestBuilder request = MockMvcRequestBuilders
                .post("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void handleLogin_invalidPassword() throws Exception {
        String passwordHash = passwordEncoder.encode("123password");

        when(userRepository.findUserByEmail(any(String.class)))
                .thenReturn(Optional.of(new User(
                                null,
                                null,
                                null,
                                "renzonifico@gmail.com",
                                passwordHash,
                                null
                        ))
                );

        String requestBody = """
                {
                    "email": "renzonifico@gmail.com",
                    "password": "password123"
                }
                """;

        RequestBuilder request = MockMvcRequestBuilders
                .post("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void handleLogin_blankEmail() throws Exception {
        String requestBody = """
                {
                    "email": "",
                    "password": "password123"
                }
                """;

        RequestBuilder request = MockMvcRequestBuilders
                .post("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

    }
}
