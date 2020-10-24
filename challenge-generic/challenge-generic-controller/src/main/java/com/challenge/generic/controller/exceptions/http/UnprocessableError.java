package com.challenge.generic.controller.exceptions.http;

import org.springframework.http.HttpStatus;

public class UnprocessableError extends BaseHttpException {

    @Override
    protected HttpStatus getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
