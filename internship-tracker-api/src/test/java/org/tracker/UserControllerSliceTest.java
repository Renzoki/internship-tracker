package org.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tracker.configuration.JwtAuthenticationFilter;
import org.tracker.configuration.SecurityConfig;
import org.tracker.controller.UserController;
import org.tracker.mapper.UserMapper;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.service.JwtService;
import org.tracker.service.UserService;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        UserMapper.class
})
public class UserControllerSliceTest {
    private UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTEiLCJuYW1lIjoiUmVueiBUYWJ1em8iLCJpYXQiOjE1MTYyMzkwMjJ9." +
            "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtService jwtService;

    @MockitoSpyBean
    private UserMapper userMapper;

    @BeforeEach
    public void jwtSetup(){
        when(jwtService.extractId(any(String.class))).thenReturn(mockId);
        when(jwtService.extractEmail(any(String.class))).thenReturn("renzonifico@gmail.com");
    }

    @Test
    public void getUserById_userExists() throws Exception {
        when(userService.getUserById(any(UUID.class))).thenReturn(
                new User(mockId,
                        "Renz",
                        "Tabuzo",
                        "renzonifico@gmail.com",
                        null,
                        null)
        );

        String expectedJson = """
        {
            "id": "%s",
            "firstName": "Renz",
            "lastName": "Tabuzo",
            "email": "renzonifico@gmail.com"
        }
        """.formatted(mockId);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/self")
                .header("Authorization", "Bearer " + mockToken);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void postUser_validRequestBody() throws Exception {
        when(userService.createNewUser(any(CreateUserCommand.class)))
                .thenReturn(new User(mockId,
                        "Renz",
                        "Tabuzo",
                        "renzonifico@gmail.com",
                        null,
                        null));

        String requestBody = """
        {
            "firstName": "Renz",
            "lastName": "Tabuzo",
            "email": "renzonifico@gmail.com",
            "password": "password123"
        }
        """;

        String expectedJson = """
        {
            "id": "%s",
            "firstName": "Renz",
            "lastName": "Tabuzo",
            "email": "renzonifico@gmail.com"
        }
        """.formatted(mockId);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void postUser_emptyFields() throws Exception {
        String requestBody = """
        {
            "firstName": "",
            "lastName": "",
            "email": "",
            "password": ""
        }
        """;

        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postUser_invalidEmailField() throws Exception {
        String requestBody = """
        {
            "firstName": "Renz",
            "lastName": "Tabuzo",
            "email": "renzonificoAtgmail.com",
            "password": "password123"
        }
        """;

        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_fullUpdate() throws Exception {
        when(userService.updateExistingUser(any(UpdateUserCommand.class)))
                .thenReturn(new User(
                        mockId,
                        "Renz",
                        "Tabuzo",
                        "renzonifico@gmail.com",
                        null,
                        null
                        ));

        String requestBody = """
                {
                    "firstName": "Renz",
                    "lastName": "Tabuzo",
                    "email": "renzonifico@gmail.com",
                    "password": "password123"
                }
                """;

        String expectedJson = """
                {
                    "id": "%s",
                    "firstName": "Renz",
                    "lastName": "Tabuzo",
                    "email": "renzonifico@gmail.com"
                }
                """.formatted(mockId);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/self")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void updateUser_partialUpdate() throws Exception {
        when(userService.updateExistingUser(any(UpdateUserCommand.class)))
                .thenReturn(new User(
                        mockId,
                        "Renz",
                        "Tabuzo",
                        "renzonifico@gmail.com",
                        null,
                        null
                ));

        //Update only first name and password
        String requestBody = """
                { 
                    "firstName": "Renz",
                    "password": "password123"
                }
                """;

        String expectedJson = """
                {
                    "id": "%s",
                    "firstName": "Renz",
                    "lastName": "Tabuzo",
                    "email": "renzonifico@gmail.com"
                }
                """.formatted(mockId);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/self")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void updateUser_blankUpdate() throws Exception {
        String requestBody = """
            { }
            """;

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/self")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);


        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_containsNotNullButBlankFields() throws Exception {
        String requestBody = """
            { 
                "firstName": "Renz",
                "lastName": "   ",
                "email": "renzonifico@gmail.com",
                "password": "   "
            }
            """;

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/self")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);


        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_invalidEmailField() throws Exception {
        String requestBody = """
        {
            "firstName": "Renz",
            "lastName": "Tabuzo",
            "email": "renzonificoAtgmail.com",
            "password": "password123"
        }
        """;

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/self")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_userExists() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/self")
                .header("Authorization", "Bearer " + mockToken);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(any(UUID.class));
    }
}
