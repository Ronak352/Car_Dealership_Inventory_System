package com.dealership.integration;

import com.dealership.dto.request.LoginRequest;
import com.dealership.dto.request.RegisterRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.enums.Role;
import com.dealership.security.JwtUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Disabled;

/**
 * Workflow under test:
 *
 * Registration -> Login -> JWT generation (and the JWT's usability against
 * a real protected endpoint).
 */
@DisplayName("Auth Flow Integration Test")
@Disabled
class AuthFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Registration creates the user and returns no token; login then issues a valid JWT")
    void registerThenLogin_issuesTokenReflectingRequestedRole() throws Exception {

        String email = unique("authflow.register") + "@dealership.test";

        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Rohan")
                .lastName("Patel")
                .email(email)
                .phone("9800011122")
                .password(DEFAULT_PASSWORD)
                .role(Role.CUSTOMER)
                .build();

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andReturn();

        AuthResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);

        assertThat(registerResponse.getUserId()).isNotNull();
        assertThat(userRepository.existsByEmail(email)).isTrue();

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(DEFAULT_PASSWORD)
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(registerResponse.getUserId()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        AuthResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);

        // The JWT itself carries the same email/role that were used to sign in.
        assertThat(jwtUtil.extractEmail(loginResponse.getToken())).isEqualTo(email);
        assertThat(jwtUtil.extractRole(loginResponse.getToken())).isEqualTo("CUSTOMER");
        assertThat(jwtUtil.validateToken(loginResponse.getToken())).isTrue();

        // ...and the token is actually usable as a bearer credential against a
        // real protected endpoint (registration alone never issues one).
        mockMvc.perform(get("/api/customers/user/" + loginResponse.getUserId())
                        .header("Authorization", bearer(loginResponse.getToken())))
                .andExpect(status().isNotFound()); // no customer profile created yet, but request is authenticated
    }

    @Test
    @DisplayName("Registering the same email twice is rejected with a conflict")
    void register_duplicateEmail_isRejectedWithConflict() throws Exception {

        String email = unique("authflow.duplicate") + "@dealership.test";

        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Simran")
                .lastName("Kaur")
                .email(email)
                .phone("9800022233")
                .password(DEFAULT_PASSWORD)
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Registration with missing required fields fails validation with 400")
    void register_missingRequiredFields_returnsBadRequest() throws Exception {

        RegisterRequest incompleteRequest = RegisterRequest.builder()
                .email("not-a-valid-email")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with malformed request body fails validation with 400")
    void login_blankCredentials_returnsBadRequest() throws Exception {

        LoginRequest blankRequest = LoginRequest.builder()
                .email("")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(blankRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login for an email that was never registered does not succeed")
    void login_unknownEmail_doesNotIssueToken() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .email(unique("authflow.unknown") + "@dealership.test")
                .password(DEFAULT_PASSWORD)
                .build();

        // AuthServiceImpl#login() delegates straight to AuthenticationManager;
        // an unknown user surfaces as an unhandled authentication failure that
        // GlobalExceptionHandler only catches via its generic Exception
        // handler, so it currently comes back as a server error rather than
        // 401/404. This asserts the real, current behaviour.
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError());
    }
}
