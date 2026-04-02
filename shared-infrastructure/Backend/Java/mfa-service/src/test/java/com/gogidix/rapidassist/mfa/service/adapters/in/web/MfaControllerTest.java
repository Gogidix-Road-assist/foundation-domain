package com.gogidix.rapidassist.mfa.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.mfa.service.domain.model.MfaEnrollment;
import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import com.gogidix.rapidassist.mfa.service.domain.port.in.EnrollMfaCommand;
import com.gogidix.rapidassist.mfa.service.domain.port.in.VerifyMfaCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MfaController.class)
class MfaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollMfaCommand enrollMfaCommand;

    @MockBean
    private VerifyMfaCommand verifyMfaCommand;

    @BeforeEach
    void setUp() {
        RequestContext ctx = new RequestContext("tenant-123", "US", "corr-456", "user-789");
        RequestContextHolder.set(ctx);
    }

    @Test
    void enroll_Success() throws Exception {
        MfaEnrollment enrollment = new MfaEnrollment("enrollment-123", "user-456", "TOTP", Instant.now());
        when(enrollMfaCommand.enroll(eq("tenant-123"), eq("user-456"), eq("TOTP")))
                .thenReturn(enrollment);

        EnrollMfaRequest request = new EnrollMfaRequest("user-456", "TOTP");

        mockMvc.perform(post("/api/v1/mfa/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value("enrollment-123"));
    }

    @Test
    void verify_Success() throws Exception {
        MfaVerificationResult result = new MfaVerificationResult(true, null);
        when(verifyMfaCommand.verify(eq("tenant-123"), eq("user-456"), eq("enrollment-123"), eq("123456")))
                .thenReturn(result);

        VerifyMfaRequest request = new VerifyMfaRequest("user-456", "enrollment-123", "123456");

        mockMvc.perform(post("/api/v1/mfa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    void verify_Failed() throws Exception {
        MfaVerificationResult result = new MfaVerificationResult(false, "Invalid code");
        when(verifyMfaCommand.verify(eq("tenant-123"), eq("user-456"), eq("enrollment-123"), eq("000000")))
                .thenReturn(result);

        VerifyMfaRequest request = new VerifyMfaRequest("user-456", "enrollment-123", "000000");

        mockMvc.perform(post("/api/v1/mfa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.reason").value("Invalid code"));
    }
}
