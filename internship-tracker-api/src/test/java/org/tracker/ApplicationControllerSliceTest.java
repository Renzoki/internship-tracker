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
import org.tracker.exception.ApplicationAccessDeniedException;
import org.tracker.exception.ApplicationNotFoundException;
import org.tracker.mapper.ApplicationMapper;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.enums.ApplicationStatus;
import org.tracker.model.enums.WorkMode;
import org.tracker.model.request.UpdateApplicationStatusCommand;
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
    private final String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTEiLCJuYW1lIjoiUmVueiBUYWJ1em8iLCJpYXQiOjE1MTYyMzkwMjJ9." +
            "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String mockUrl = "https://example.com";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationService applicationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoSpyBean
    private ApplicationMapper mapper;

    @BeforeEach
    public void setup() {
        String mockEmail = "renzonifico@gmail.com";
        when(jwtService.extractId(any(String.class))).thenReturn(mockId);
        when(jwtService.extractEmail(any(String.class))).thenReturn(mockEmail);
    }

    @Test
    public void getAllApplications_HasApplications_returns200() throws Exception {
        when(applicationService.getAllApplications(any(UUID.class)))
                .thenReturn(List.of(
                        new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                                WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null),
                        new Application(mockId, null, "IBM", "Quality Assurance Intern", "Taguig",
                                WorkMode.REMOTE, mockUrl, LocalDate.parse("2004-09-23"), null)
                ));

        String expectedJson = """
                    [
                        {
                            "id": "%s",
                            "companyName": "Oracle",
                            "positionTitle": "Software Engineer Intern",
                            "location": "Makati",
                            "workMode": "HYBRID",
                            "applicationUrl": "%s",
                            "status": "APPLIED",
                            "dateApplied": "2004-02-14"
                        },
                        {
                            "id": "%s",
                            "companyName": "IBM",
                            "positionTitle": "Quality Assurance Intern",
                            "location": "Taguig",
                            "workMode": "REMOTE",
                            "applicationUrl": "%s",
                            "status": "APPLIED",
                            "dateApplied": "2004-09-23"
                        }
                    ]
                """.formatted(mockId, mockUrl, mockId, mockUrl);

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
    public void getAllApplications_noApplications_returns200() throws Exception {
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

    @Test
    public void getApplicationById_applicationExists_returns200() throws Exception {
        when(applicationService.getApplicationById(any(UUID.class), any(UUID.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        String expectedJson = """
        {
            "id": "%s",
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s",
            "status": "APPLIED",
            "dateApplied": "2004-02-14"
        }
        """.formatted(mockId, mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/applications/" + mockId)
                .header("Authorization", "Bearer " + mockToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void getApplicationById_applicationDoesNotExist_returns404() throws Exception {
        when(applicationService.getApplicationById(any(UUID.class), any(UUID.class)))
                .thenThrow(new ApplicationNotFoundException(mockId));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/applications/" + mockId)
                .header("Authorization", "Bearer " + mockToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getApplicationById_invalidUUID_returns400() throws Exception {
        when(applicationService.getApplicationById(any(UUID.class), any(UUID.class)))
                .thenThrow(new ApplicationNotFoundException(mockId));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/applications/" + "invalid-uuid")
                .header("Authorization", "Bearer " + mockToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postApplication_validRequestBody_returns201() throws Exception {
        when(applicationService.addNewApplication(any(CreateApplicationCommand.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s",
            "dateApplied": "2004-02-14"
        }
        """.formatted(mockUrl);

        String expectedJson = """
        {
            "id": "%s",
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s",
            "status": "APPLIED",
            "dateApplied": "2004-02-14"
        }
        """.formatted(mockId, mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/applications")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void postApplication_missingRequestBody_returns400() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/applications")
                .header("Authorization", "Bearer " + mockToken);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postApplication_missingFieldsInRequest_returns400() throws Exception {
        String requestBody = """
        {
            "companyName": "Oracle",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s"
        }
        """.formatted(mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/applications")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postApplication_invalidApplicationLinkFormat_returns400() throws Exception {
        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "invalid-link-123",
            "dateApplied": "2004-02-14"
        }
        """;

        RequestBuilder request = MockMvcRequestBuilders
                .post("/applications")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postApplication_applicationDateSetInFuture_returns400() throws Exception {
        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s",
            "dateApplied": "3025-02-14"
        }
        """.formatted(mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/applications")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postApplication_invalidWorkModeEnum_returns400() throws Exception {
        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "REMOTE/HYBDID",
            "applicationUrl": "%s",
            "dateApplied": "3025-02-14"
        }
        """.formatted(mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/applications")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateApplicationDetails_validRequestBody_returns200() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s"
        }
        """.formatted(mockUrl);

        String expectedJson = """
        {
            "id": "%s",
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s",
            "status": "APPLIED",
            "dateApplied": "2004-02-14"
        }
        """.formatted(mockId, mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void updateApplicationDetails_notAllFieldsPresent_returns200() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        //missing fields
        String requestBody = """
        {
            "companyName": "Oracle",
            "workMode": "HYBRID",
            "applicationUrl": "%s"
        }
        """.formatted(mockUrl);

        String expectedJson = """
        {
            "id": "%s",
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s",
            "status": "APPLIED",
            "dateApplied": "2004-02-14"
        }
        """.formatted(mockId, mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void updateApplicationDetails_fieldsContainingEmptyStringsPresent_returns400() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        String requestBody = """
        {
            "companyName": " ",
            "positionTitle": "Software Engineer Intern",
            "location": " ",
            "workMode": "HYBRID",
            "applicationUrl": "%s"
        }
        """.formatted(mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateApplicationDetails_invalidApplicationUrlFormat_returns400() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "not-a-url"
        }
        """;

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateApplicationDetails_invalidWorkModeEnum_returns400() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "JAMAICA",
            "applicationUrl": "%s"
        }
        """.formatted(mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateApplicationDetails_requestNotContainingAnyFields_returns400() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenReturn(new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                        WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null));

        String requestBody = "{}";

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateApplicationDetails_applicationIdNotFound_returns404() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenThrow(new ApplicationNotFoundException(mockId));

        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s"
        }
        """.formatted(mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateApplicationDetails_applicationAccessDenied_returns403() throws Exception {
        when(applicationService.updateApplicationDetails(any(UpdateApplicationDetailsCommand.class)))
                .thenThrow(new ApplicationAccessDeniedException(mockId, mockId));

        String requestBody = """
        {
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s"
        }
        """.formatted(mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/details")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateApplicationStatus_validRequestBody_returns200() throws Exception {
        Application mockApplication = new Application(mockId, null, "Oracle", "Software Engineer Intern", "Makati",
                WorkMode.HYBRID, mockUrl, LocalDate.parse("2004-02-14"), null);
        mockApplication.setStatus(ApplicationStatus.FOR_INTERVIEW);

        when(applicationService.updateApplicationStatus(any(UpdateApplicationStatusCommand.class)))
                .thenReturn(mockApplication);

        String requestBody = """
            {
                "status": "FOR_INTERVIEW"
            }
        """;

        String expectedJson = """
        {
            "id": "%s",
            "companyName": "Oracle",
            "positionTitle": "Software Engineer Intern",
            "location": "Makati",
            "workMode": "HYBRID",
            "applicationUrl": "%s",
            "status": "FOR_INTERVIEW",
            "dateApplied": "2004-02-14"
        }
        """.formatted(mockId, mockUrl);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/status")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void updateApplicationStatus_applicationDoesNotExist_returns404() throws Exception {
        when(applicationService.updateApplicationStatus(any(UpdateApplicationStatusCommand.class)))
                .thenThrow(new ApplicationNotFoundException(mockId));

        String requestBody = """
            {
                "status": "FOR_INTERVIEW"
            }
        """;

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/status")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateApplicationStatus_missingRequestBody_returns400() throws Exception {
        String requestBody = "";

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/status")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateApplicationStatus_nullStatusEnumProvided_returns400() throws Exception {
        String requestBody = """
                {
                    "status": "null"
                }
       """;

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/status")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateApplicationStatus_invalidStatusEnumProvided_returns400() throws Exception {
        String requestBody = """
                {
                    "status": "FAILED"
                }
       """;

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/applications/" + mockId + "/status")
                .header("Authorization", "Bearer " + mockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
}
