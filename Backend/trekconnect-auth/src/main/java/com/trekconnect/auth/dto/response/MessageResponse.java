package com.trekconnect.auth.dto.response;

public class MessageResponse {

    private String message;
    private Boolean success;

    public MessageResponse() {}

    public MessageResponse(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }

    public static MessageResponseBuilder builder() {
        return new MessageResponseBuilder();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class MessageResponseBuilder {
        private String message;
        private Boolean success;

        public MessageResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public MessageResponseBuilder success(Boolean success) {
            this.success = success;
            return this;
        }

        public MessageResponse build() {
            return new MessageResponse(message, success);
        }
    }
}
