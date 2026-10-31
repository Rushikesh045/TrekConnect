package com.trekconnect.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * TrekConnect AI Microservice.
 * 
 * ARCHITECTURAL RESPONSIBILITY:
 * Operates as a dedicated, decoupled AI microservice on Port 8082 handling:
 * 1. TrekBot: Context-Aware RAG (Retrieval-Augmented Generation) Chatbot for trek itineraries, gear, and safety.
 *    Supports dynamic worldwide & Pan-India expeditions with native Ollama LLM integration and in-JVM fallback.
 * 2. OCR & KYC Engine: Optical character analysis and automated validation of Organizer KYC credentials.
 */
@SpringBootApplication
public class TrekconnectAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrekconnectAiApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(60000);
        return new RestTemplate(factory);
    }
}
