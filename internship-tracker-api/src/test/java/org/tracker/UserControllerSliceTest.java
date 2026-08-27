package org.tracker;

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
import org.tracker.controller.UserController;
import org.tracker.exception.UserNotFoundException;
import org.tracker.mapper.UserMapper;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.service.UserService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserMapper.class)
public class UserControllerSliceTest {

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
        UUID nonexistentId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        when(userService.getUserById(nonexistentId))
                .thenThrow(new UserNotFoundException(nonexistentId));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/" + nonexistentId)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void postUser_validRequestBody() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
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
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
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
                .put("/users/" + mockId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void updateUser_partialUpdate() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
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
                .put("/users/" + mockId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void updateUser_blankUpdate() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String requestBody = """
            { }
            """;

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/" + mockId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);


        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_containsNotNullButBlankFields() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String requestBody = """
            { 
                "firstName": "Renz",
                "lastName": "   ",
                "email": "renzonifico@gmail.com",
                "password": "   "
            }
            """;

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/" + mockId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);


        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_invalidEmailField() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        String requestBody = """
        {
            "firstName": "Renz",
            "lastName": "Tabuzo",
            "email": "renzonificoAtgmail.com",
            "password": "password123"
        }
        """;

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/" + mockId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_userExists() throws Exception {
        UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/" + mockId);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(any(UUID.class));
    }

    @Test
    public void deleteUser_userDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        doThrow(new UserNotFoundException(nonExistentId))
                .when(userService).deleteUserById(nonExistentId);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/" + nonExistentId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUserById(any(UUID.class));
    }
}
