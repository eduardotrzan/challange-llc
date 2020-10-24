package com.challenge.generic.controller.exceptions.http;

import org.springframework.http.HttpStatus;

public class BadRequestError extends BaseHttpException {

    @Override
    protected HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
