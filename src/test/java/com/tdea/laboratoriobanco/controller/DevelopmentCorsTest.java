package com.tdea.laboratoriobanco.controller;

import com.tdea.laboratoriobanco.config.DevelopmentCorsConfig;
import com.tdea.laboratoriobanco.service.BankAccountService;
import com.tdea.laboratoriobanco.service.BankTransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountController.class)
@Import(DevelopmentCorsConfig.class)
@ActiveProfiles("dev")
class DevelopmentCorsTest {
    @Autowired MockMvc mvc;
    @MockitoBean BankAccountService accounts;
    @MockitoBean BankTransferService transfers;

    @Test
    void acceptsLocalViteOrigin() throws Exception {
        mvc.perform(options("/api/accounts")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void rejectsOtherOrigins() throws Exception {
        mvc.perform(options("/api/accounts")
                .header("Origin", "https://untrusted.example")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void rejectsArbitraryBalanceModificationMethods() throws Exception {
        mvc.perform(options("/api/accounts/1")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "PUT"))
                .andExpect(status().isForbidden());
    }
}
