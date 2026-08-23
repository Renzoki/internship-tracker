package org.tracker.controller;

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
import org.tracker.exception.UserNotFoundException;
import org.tracker.mapper.UserMapper;
import org.tracker.model.entities.User;
import org.tracker.service.UserService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserMapper.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoSpyBean
    private UserMapper userMapper;

    @Test
    public void getAllUsers_oneUser() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userService.getAllUsers()).thenReturn(
                List.of(new User(mockId,
                        "Renz",
                        "Tabuzo",
                        "renzonifico@gmail.com",
                        null,
                        null))
        );

        String expectedJson = """
        [{
            "id": "%s",
            "firstName": "Renz",
            "lastName": "Tabuzo",
            "email": "renzonifico@gmail.com"
        }]
        """.formatted(mockId);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users")
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void getAllUsers_noUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    public void getUserById_userExists() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userService.getUserById(mockId)).thenReturn(
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
                .get("/users/" + mockId)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void getUserById_userDoesNotExist() throws Exception {
        UUID nonexistendId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        when(userService.getUserById(nonexistendId))
                .thenThrow(new UserNotFoundException(nonexistendId));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/" + nonexistendId)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

}
