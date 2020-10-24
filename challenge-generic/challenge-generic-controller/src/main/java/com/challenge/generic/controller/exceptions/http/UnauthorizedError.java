package com.challenge.generic.controller.exceptions.http;

import org.springframework.http.HttpStatus;

public class UnauthorizedError extends BaseHttpException {

    @Override
    protected HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
