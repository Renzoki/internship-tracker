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
import org.tracker.controller.ApplicationController;
import org.tracker.mapper.ApplicationMapper;
import org.tracker.model.entities.Application;
import org.tracker.model.enums.WorkMode;
import org.tracker.service.ApplicationService;
import org.tracker.service.JwtService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ApplicationMapper.class
})
public class ApplicationControllerSliceTest {
    private final UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTEiLCJuYW1lIjoiUmVueiBUYWJ1em8iLCJpYXQiOjE1MTYyMzkwMjJ9." +
            "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationService applicationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoSpyBean
    private ApplicationMapper mapper;

    @BeforeEach
    public void setup(){
        String mockEmail = "renzonifico@gmail.com";
        when(jwtService.extractId(any(String.class))).thenReturn(mockId);
        when(jwtService.extractEmail(any(String.class))).thenReturn(mockEmail);
    }

    @Test
    public void userExists_HasApplications_returns200() throws Exception {
        when(applicationService.getAllApplications(any(UUID.class)))
                .thenReturn(List.of(
                        new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                                        WorkMode.HYBRID, "mockUrl", LocalDate.parse("2004-02-14"), null),
                        new Application(mockId, null, "IBM", "Quality Assurance Intern", "Taguig",
                                WorkMode.REMOTE, "mockUrl", LocalDate.parse("2004-09-23"), null)
                ));

        String expectedJson = """
            [
                {
                    "id": "%s",
                    "companyName": "Oracle",
                    "positionTitle": "Software Engineer Intern",
                    "location": "Makati",
                    "workMode": "HYBRID",
                    "applicationUrl": "mockUrl",
                    "status": "APPLIED",
                    "dateApplied": "2004-02-14"
                },
                {
                    "id": "%s",
                    "companyName": "IBM",
                    "positionTitle": "Quality Assurance Intern",
                    "location": "Taguig",
                    "workMode": "REMOTE",
                    "applicationUrl": "mockUrl",
                    "status": "APPLIED",
                    "dateApplied": "2004-09-23"
                }
            ]
        """.formatted(mockId, mockId);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/applications/self")
                .header("Authorization", "Bearer " + mockToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void userExists_noApplications_returns200() throws Exception {
        when(applicationService.getAllApplications(any(UUID.class)))
                .thenReturn(List.of());

        String expectedJson = "[]";

        RequestBuilder request = MockMvcRequestBuilders
                .get("/applications/self")
                .header("Authorization", "Bearer " + mockToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }
}
