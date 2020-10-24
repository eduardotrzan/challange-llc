package com.challenge.generic.controller.exceptions.http;

import org.springframework.http.HttpStatus;

public class NotFoundError extends BaseHttpException {

    @Override
    protected HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
