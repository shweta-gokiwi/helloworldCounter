package com.hello.helloworld.admin.payload.response;

import org.springframework.http.HttpStatus;

public class HelloWorldResponse {

    private HttpStatus status;
    private String message;

    // Constructor
    public HelloWorldResponse(int statusCode, String message) {
        this.status = HttpStatus.valueOf(statusCode);
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
