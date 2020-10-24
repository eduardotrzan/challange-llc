package com.challenge.generic.controller.exceptions.http;

import org.springframework.http.HttpStatus;

public class InternalServerError extends BaseHttpException {

    @Override
    protected HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
