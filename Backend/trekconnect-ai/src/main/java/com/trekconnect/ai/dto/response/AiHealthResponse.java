package com.trekconnect.ai.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Health check response reporting AI microservice runtime capabilities and status.
 */
public class AiHealthResponse {

    private String status;
    private String service;
    private String version;
    private int port;
    private List<String> capabilities;
    private LocalDateTime timestamp;

    public AiHealthResponse() {
    }

    public AiHealthResponse(String status, String service, String version, int port, List<String> capabilities) {
        this.status = status;
        this.service = service;
        this.version = version;
        this.port = port;
        this.capabilities = capabilities;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
