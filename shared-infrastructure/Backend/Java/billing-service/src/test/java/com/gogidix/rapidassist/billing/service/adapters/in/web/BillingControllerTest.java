package com.gogidix.rapidassist.billing.service.adapters.in.web;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import com.gogidix.rapidassist.billing.service.domain.port.in.GetBillingAccountQuery;
import com.gogidix.rapidassist.billing.service.domain.port.in.SetBillingPlanCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BillingControllerTest {

    @Mock
    private GetBillingAccountQuery getBillingAccountQuery;

    @Mock
    private SetBillingPlanCommand setBillingPlanCommand;

    private MockMvc mockMvc;
    private RequestContext requestContext;

    @BeforeEach
    void setUp() {
        BillingController controller = new BillingController(getBillingAccountQuery, setBillingPlanCommand);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        requestContext = new RequestContext("tenant-123", "user-123", "email@example.com");
    }

    @Test
    void getAccount_ShouldReturnBillingAccount() throws Exception {
        BillingAccount account = new BillingAccount(BillingPlan.PREMIUM, LocalDateTime.now());
        when(getBillingAccountQuery.get(anyString())).thenReturn(account);

        mockMvc.perform(get("/api/v1/billing/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan").value("PREMIUM"));
    }

    @Test
    void setPlan_ShouldUpdateBillingPlan() throws Exception {
        BillingAccount account = new BillingAccount(BillingPlan.ENTERPRISE, LocalDateTime.now());
        when(setBillingPlanCommand.setPlan(anyString(), any())).thenReturn(account);

        mockMvc.perform(post("/api/v1/billing/account/plan")
                        .contentType("application/json")
                        .content("{\"plan\":\"ENTERPRISE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan").value("ENTERPRISE"));
    }
}
