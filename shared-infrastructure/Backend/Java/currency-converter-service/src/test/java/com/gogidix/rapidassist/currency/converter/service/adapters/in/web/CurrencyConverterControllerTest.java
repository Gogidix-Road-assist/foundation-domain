package com.gogidix.rapidassist.currency.converter.service.adapters.in.web;

import com.gogidix.rapidassist.currency.converter.service.domain.model.ConversionResult;
import com.gogidix.rapidassist.currency.converter.service.domain.port.in.ConvertCurrencyQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterControllerTest {

    @Mock
    private ConvertCurrencyQuery convertCurrencyQuery;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CurrencyConverterController controller = new CurrencyConverterController(convertCurrencyQuery);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void convert_ShouldReturnConvertedAmount() throws Exception {
        ConversionResult result = new ConversionResult("USD", "EUR", new BigDecimal("100"), new BigDecimal("85.50"), new BigDecimal("0.855"));
        when(convertCurrencyQuery.convert(anyString(), any(), anyString(), anyString())).thenReturn(result);

        mockMvc.perform(post("/api/v1/currency/convert")
                        .contentType("application/json")
                        .content("{\"amount\":100,\"fromCurrency\":\"USD\",\"toCurrency\":\"EUR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.convertedAmount").value(85.50));
    }
}
