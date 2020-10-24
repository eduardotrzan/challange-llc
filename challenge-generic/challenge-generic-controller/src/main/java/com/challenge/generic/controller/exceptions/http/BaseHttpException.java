package com.challenge.generic.controller.exceptions.http;

import lombok.Getter;
import lombok.ToString;

import org.springframework.http.HttpStatus;

import com.challenge.generic.controller.exceptions.error.ErrorLevel;

@Getter
@ToString(of = { "httpStatus", "errorLevel", "description" })
public abstract class BaseHttpException extends RuntimeException {

    private static final long serialVersionUID = 7594975012021334502L;

    private final HttpStatus httpStatus;

    private ErrorLevel errorLevel = ErrorLevel.ERROR;

    private String description;

    protected BaseHttpException() {
        this.httpStatus = this.getHttpStatus();
    }

    @Override
    public String getMessage() {
        return this.getDescription();
    }

    public <T extends BaseHttpException> T withErrorLevel(ErrorLevel errorLevel) {
        this.errorLevel = errorLevel;
        return (T) this;
    }

    public <T extends BaseHttpException> T withDescription(String description) {
        this.description = description;
        return (T) this;
    }

    protected abstract HttpStatus getHttpStatus();
}
