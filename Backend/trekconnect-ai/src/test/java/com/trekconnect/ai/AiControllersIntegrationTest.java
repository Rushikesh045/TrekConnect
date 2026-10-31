package com.trekconnect.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trekconnect.ai.dto.request.KycDocumentVerifyRequest;
import com.trekconnect.ai.dto.request.TrekBotChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AiControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAiHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("trekconnect-ai"))
                .andExpect(jsonPath("$.port").value(8082));
    }

    @Test
    void testTrekBotChatEndpoint() throws Exception {
        TrekBotChatRequest req = new TrekBotChatRequest("1", "Rajmachi Fort", "What meals and food are included in this trek?");

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").isNotEmpty())
                .andExpect(jsonPath("$.groundedIn").isNotEmpty())
                .andExpect(jsonPath("$.confidenceScore").isNumber());
    }

    @Test
    void testTrekBotQuickPrompts() throws Exception {
        mockMvc.perform(get("/api/ai/chat/quick-prompts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Gear & Packing']").isArray());
    }

    @Test
    void testOcrKycVerificationEndpoint() throws Exception {
        String panCardText = "GOVT OF INDIA INCOME TAX DEPARTMENT PERMANENT ACCOUNT NUMBER CARD ABCPE1234F NAME RUSHIKESH GHUGE DOB 10/10/1997";
        String base64 = Base64.getEncoder().encodeToString(panCardText.getBytes(StandardCharsets.UTF_8));

        KycDocumentVerifyRequest req = new KycDocumentVerifyRequest("Rushikesh Ghuge", base64);
        req.setDocumentFileName("pan_test.txt");

        mockMvc.perform(post("/api/ai/kyc/verify-document")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentTypeDetected").value("PAN_CARD"))
                .andExpect(jsonPath("$.extractedIdNumber").value("ABCPE1234F"))
                .andExpect(jsonPath("$.status").value("AUTO_VERIFIED"));
    }
}
